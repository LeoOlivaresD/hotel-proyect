package cl.duoc.hotel.controller;

import cl.duoc.hotel.model.Habitacion;
import cl.duoc.hotel.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService service;

    /** GET /api/habitaciones?estado=disponible&tipo=Suite */
    @GetMapping
    public List<Habitacion> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo) {
        return service.findByFiltro(estado, tipo);
    }

    /** GET /api/habitaciones/{id} */
    @GetMapping("/{id}")
    public Habitacion obtener(@PathVariable Long id) {
        return service.findById(id);
    }

    /** POST /api/habitaciones */
    @PostMapping
    public ResponseEntity<Habitacion> crear(@Valid @RequestBody Habitacion h) {
        return ResponseEntity.ok(service.save(h));
    }

    /** PUT /api/habitaciones/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> actualizar(
            @PathVariable Long id, @Valid @RequestBody Habitacion h) {
        h.setId(id);
        return ResponseEntity.ok(service.save(h));
    }

    /** PATCH /api/habitaciones/{id}/estado */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Habitacion> cambiarEstado(
            @PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(service.updateEstado(id, estado));
    }

    /** DELETE /api/habitaciones/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
