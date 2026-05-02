package cl.duoc.hotel.controller;

import cl.duoc.hotel.dto.DashboardStats;
import cl.duoc.hotel.dto.ReservaRequest;
import cl.duoc.hotel.model.Reserva;
import cl.duoc.hotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService service;

    /** GET /api/reservas */
    @GetMapping
    public List<Reserva> listar() {
        return service.findAll();
    }

    /** GET /api/reservas/{id} */
    @GetMapping("/{id}")
    public Reserva obtener(@PathVariable Long id) {
        return service.findById(id);
    }

    /** POST /api/reservas  — crea cliente si no existe, crea reserva y marca habitación */
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaRequest req) {
        try {
            Reserva r = service.crearReserva(req);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Reserva confirmada exitosamente.",
                    "reservaId", r.getId(),
                    "total", r.getTotal(),
                    "estado", r.getEstado()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/reservas/{id}/cancelar */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelarReserva(id));
    }

    /** GET /api/reservas/dashboard */
    @GetMapping("/dashboard")
    public DashboardStats dashboard() {
        return service.getDashboardStats();
    }
}
