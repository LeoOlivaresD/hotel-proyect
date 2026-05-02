package cl.duoc.hotel.repository;

import cl.duoc.hotel.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    List<Habitacion> findByEstado(String estado);
    List<Habitacion> findByTipo(String tipo);
    List<Habitacion> findByEstadoAndTipo(String estado, String tipo);
    long countByEstado(String estado);
    boolean existsByNumero(String numero);
}
