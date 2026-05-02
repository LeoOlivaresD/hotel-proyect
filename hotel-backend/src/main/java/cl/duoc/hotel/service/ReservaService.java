package cl.duoc.hotel.service;

import cl.duoc.hotel.dto.DashboardStats;
import cl.duoc.hotel.dto.ReservaRequest;
import cl.duoc.hotel.model.*;
import cl.duoc.hotel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final ClienteRepository clienteRepo;
    private final HabitacionRepository habRepo;

    public List<Reserva> findAll() {
        return reservaRepo.findAll();
    }

    public Reserva findById(Long id) {
        return reservaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + id));
    }

    @Transactional
    public Reserva crearReserva(ReservaRequest req) {

        // 1. Validar fechas
        if (!req.getFecha_salida().isAfter(req.getFecha_entrada())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la entrada.");
        }

        // 2. Buscar o crear cliente por RUT
        Cliente cliente = clienteRepo.findByRut(req.getRut())
                .orElseGet(() -> clienteRepo.save(
                        Cliente.builder()
                                .nombre(req.getNombre())
                                .apellido(req.getApellido())
                                .rut(req.getRut())
                                .correo(req.getCorreo())
                                .telefono(req.getTelefono())
                                .build()));

        // 3. Verificar habitación disponible
        Habitacion hab = habRepo.findById(req.getHabitacion_id())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada."));

        if (!"disponible".equalsIgnoreCase(hab.getEstado())) {
            throw new IllegalStateException("La habitación no está disponible.");
        }

        // 4. Verificar conflictos de fechas
        List<Reserva> conflictos = reservaRepo.findConflictos(
                hab.getId(), req.getFecha_entrada(), req.getFecha_salida());
        if (!conflictos.isEmpty()) {
            throw new IllegalStateException("La habitación ya tiene una reserva en esas fechas.");
        }

        // 5. Calcular total con IVA 19%
        long noches = req.getFecha_entrada().until(req.getFecha_salida()).getDays();
        BigDecimal precioNoche = hab.getPrecio();
        BigDecimal subtotal = precioNoche.multiply(BigDecimal.valueOf(noches));
        BigDecimal total = subtotal.multiply(new BigDecimal("1.19"))
                .setScale(2, RoundingMode.HALF_UP);

        // 6. Crear reserva
        Reserva reserva = Reserva.builder()
                .cliente(cliente)
                .habitacion(hab)
                .fechaEntrada(req.getFecha_entrada())
                .fechaSalida(req.getFecha_salida())
                .huespedes(req.getHuespedes())
                .metodoPago(req.getMetodo_pago())
                .estadoPago(req.getEstado_pago())
                .estado("confirmada")
                .total(total)
                .observaciones(req.getObservaciones())
                .build();

        Reserva saved = reservaRepo.save(reserva);

        // 7. Marcar habitación como ocupada
        hab.setEstado("ocupada");
        habRepo.save(hab);

        return saved;
    }

    @Transactional
    public Reserva cancelarReserva(Long id) {
        Reserva r = findById(id);
        r.setEstado("cancelada");
        // Liberar habitación
        r.getHabitacion().setEstado("disponible");
        habRepo.save(r.getHabitacion());
        return reservaRepo.save(r);
    }

    public DashboardStats getDashboardStats() {
        return new DashboardStats(
                reservaRepo.countByEstado("confirmada"),
                habRepo.countByEstado("ocupada"),
                reservaRepo.sumTotalConfirmadas(),
                reservaRepo.countCheckinshoy(LocalDate.now())
        );
    }
}
