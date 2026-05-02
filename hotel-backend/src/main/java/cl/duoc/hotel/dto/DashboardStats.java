package cl.duoc.hotel.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {
    private long reservasActivas;
    private long habitacionesOcupadas;
    private BigDecimal ingresosMes;
    private long checkinshoy;
}
