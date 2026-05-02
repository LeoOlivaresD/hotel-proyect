package cl.duoc.hotel;

import cl.duoc.hotel.dto.ReservaRequest;
import cl.duoc.hotel.model.*;
import cl.duoc.hotel.repository.*;
import cl.duoc.hotel.service.ReservaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService – Pruebas unitarias de lógica de negocio")
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepo;
    @Mock private ClienteRepository clienteRepo;
    @Mock private HabitacionRepository habRepo;
    @InjectMocks private ReservaService service;

    private Habitacion habDisponible;
    private ReservaRequest req;

    @BeforeEach
    void setup() {
        habDisponible = Habitacion.builder()
                .id(1L).numero("101").tipo("Simple").piso(1)
                .capacidad(1).precio(new BigDecimal("65.00")).estado("disponible").m2(20)
                .build();

        req = new ReservaRequest();
        req.setNombre("María"); req.setApellido("González");
        req.setRut("12.345.678-9"); req.setCorreo("maria@test.com");
        req.setHabitacion_id(1L); req.setHuespedes(1);
        req.setFecha_entrada(LocalDate.of(2025, 6, 1));
        req.setFecha_salida(LocalDate.of(2025, 6, 4));
        req.setMetodo_pago("credito"); req.setEstado_pago("pendiente");
    }

    // ── HU-01: Crear reserva exitosa ─────────────────────────────────────────
    @Test
    @DisplayName("HU-01: Crear reserva con datos válidos debe retornar reserva confirmada")
    void crearReservaExitosa() {
        when(clienteRepo.findByRut(anyString())).thenReturn(Optional.empty());
        when(clienteRepo.save(any())).thenReturn(
                Cliente.builder().id(1L).nombre("María").apellido("González")
                        .rut("12.345.678-9").correo("maria@test.com").build());
        when(habRepo.findById(1L)).thenReturn(Optional.of(habDisponible));
        when(reservaRepo.findConflictos(anyLong(), any(), any())).thenReturn(List.of());
        when(reservaRepo.save(any())).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        Reserva result = service.crearReserva(req);

        assertThat(result.getEstado()).isEqualTo("confirmada");
        assertThat(result.getTotal()).isEqualByComparingTo("232.35"); // 65*3*1.19
        verify(habRepo).save(argThat(h -> "ocupada".equals(h.getEstado())));
    }

    // ── HU-01: Fecha inválida ────────────────────────────────────────────────
    @Test
    @DisplayName("HU-01: Fecha salida anterior a entrada debe lanzar excepción")
    void crearReservaFechaInvalida() {
        req.setFecha_salida(LocalDate.of(2025, 5, 28)); // antes de entrada

        assertThatThrownBy(() -> service.crearReserva(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("salida debe ser posterior");
    }

    // ── HU-01: Habitación no disponible ──────────────────────────────────────
    @Test
    @DisplayName("HU-01: Reservar habitación ocupada debe lanzar excepción")
    void crearReservaHabitacionOcupada() {
        habDisponible.setEstado("ocupada");
        when(habRepo.findById(1L)).thenReturn(Optional.of(habDisponible));

        assertThatThrownBy(() -> service.crearReserva(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no está disponible");
    }

    // ── HU-01: Conflicto de fechas ───────────────────────────────────────────
    @Test
    @DisplayName("HU-01: Reservar habitación con fechas en conflicto debe lanzar excepción")
    void crearReservaConflictoFechas() {
        when(habRepo.findById(1L)).thenReturn(Optional.of(habDisponible));
        when(reservaRepo.findConflictos(anyLong(), any(), any()))
                .thenReturn(List.of(new Reserva())); // hay conflicto

        assertThatThrownBy(() -> service.crearReserva(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya tiene una reserva");
    }

    // ── HU-07: Cancelar reserva ──────────────────────────────────────────────
    @Test
    @DisplayName("HU-07: Cancelar reserva debe cambiar estado a 'cancelada' y liberar habitación")
    void cancelarReserva() {
        Reserva existente = Reserva.builder()
                .id(1L).estado("confirmada").habitacion(habDisponible).build();
        habDisponible.setEstado("ocupada");

        when(reservaRepo.findById(1L)).thenReturn(Optional.of(existente));
        when(reservaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva result = service.cancelarReserva(1L);

        assertThat(result.getEstado()).isEqualTo("cancelada");
        assertThat(result.getHabitacion().getEstado()).isEqualTo("disponible");
    }

    // ── Cliente reutilizado si ya existe por RUT ─────────────────────────────
    @Test
    @DisplayName("HU-01: Si el cliente ya existe por RUT, debe reutilizarse sin duplicar")
    void crearReservaClienteExistente() {
        Cliente clienteExistente = Cliente.builder().id(5L).rut("12.345.678-9").build();
        when(clienteRepo.findByRut("12.345.678-9")).thenReturn(Optional.of(clienteExistente));
        when(habRepo.findById(1L)).thenReturn(Optional.of(habDisponible));
        when(reservaRepo.findConflictos(anyLong(), any(), any())).thenReturn(List.of());
        when(reservaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.crearReserva(req);

        // No debe guardar un nuevo cliente
        verify(clienteRepo, never()).save(any());
    }
}
