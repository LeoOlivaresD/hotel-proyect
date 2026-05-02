package cl.duoc.hotel.repository;

import cl.duoc.hotel.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByEstado(String estado);
    long countByEstado(String estado);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM Reserva r WHERE r.estado = 'confirmada'")
    BigDecimal sumTotalConfirmadas();

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.fechaEntrada = :hoy AND r.estado <> 'cancelada'")
    long countCheckinshoy(LocalDate hoy);

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.id = :habId " +
           "AND r.estado <> 'cancelada' " +
           "AND ((r.fechaEntrada <= :salida) AND (r.fechaSalida >= :entrada))")
    List<Reserva> findConflictos(Long habId, LocalDate entrada, LocalDate salida);
}
