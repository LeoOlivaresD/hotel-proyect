package cl.duoc.hotel;

import cl.duoc.hotel.model.Habitacion;
import cl.duoc.hotel.repository.HabitacionRepository;
import cl.duoc.hotel.service.HabitacionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HabitacionService – Pruebas unitarias")
class HabitacionServiceTest {

    @Mock private HabitacionRepository repo;
    @InjectMocks private HabitacionService service;

    // ── HU-02: Listar habitaciones disponibles ───────────────────────────────
    @Test
    @DisplayName("HU-02: Filtrar por estado 'disponible' debe retornar solo disponibles")
    void filtrarPorEstado() {
        List<Habitacion> disponibles = List.of(
                Habitacion.builder().id(1L).numero("101").estado("disponible").build(),
                Habitacion.builder().id(2L).numero("204").estado("disponible").build()
        );
        when(repo.findByEstado("disponible")).thenReturn(disponibles);

        List<Habitacion> result = service.findByFiltro("disponible", null);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(h -> "disponible".equals(h.getEstado()));
    }

    // ── HU-02: Filtrar por tipo ──────────────────────────────────────────────
    @Test
    @DisplayName("HU-02: Filtrar por tipo 'Suite' debe retornar solo suites")
    void filtrarPorTipo() {
        List<Habitacion> suites = List.of(
                Habitacion.builder().id(5L).tipo("Suite").estado("disponible").build()
        );
        when(repo.findByTipo("Suite")).thenReturn(suites);

        List<Habitacion> result = service.findByFiltro(null, "Suite");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo("Suite");
    }

    // ── HU-02: Habitación no encontrada ─────────────────────────────────────
    @Test
    @DisplayName("HU-02: Buscar habitación inexistente debe lanzar excepción")
    void habitacionNoEncontrada() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrada");
    }

    // ── Cambiar estado habitación ────────────────────────────────────────────
    @Test
    @DisplayName("Cambiar estado a 'mantenimiento' debe persistir el cambio")
    void cambiarEstado() {
        Habitacion h = Habitacion.builder().id(1L).estado("disponible").build();
        when(repo.findById(1L)).thenReturn(Optional.of(h));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Habitacion result = service.updateEstado(1L, "mantenimiento");

        assertThat(result.getEstado()).isEqualTo("mantenimiento");
    }
}
