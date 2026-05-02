package cl.duoc.hotel.service;

import cl.duoc.hotel.model.Habitacion;
import cl.duoc.hotel.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository repo;

    public List<Habitacion> findAll() {
        return repo.findAll();
    }

    public List<Habitacion> findByFiltro(String estado, String tipo) {
        if (estado != null && tipo != null) return repo.findByEstadoAndTipo(estado, tipo);
        if (estado != null) return repo.findByEstado(estado);
        if (tipo != null)   return repo.findByTipo(tipo);
        return repo.findAll();
    }

    public Habitacion findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + id));
    }

    public Habitacion save(Habitacion h) {
        return repo.save(h);
    }

    public Habitacion updateEstado(Long id, String estado) {
        Habitacion h = findById(id);
        h.setEstado(estado);
        return repo.save(h);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public long countByEstado(String estado) {
        return repo.countByEstado(estado);
    }
}
