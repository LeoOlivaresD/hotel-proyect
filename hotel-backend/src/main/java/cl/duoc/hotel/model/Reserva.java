package cl.duoc.hotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion;

    @NotNull
    private LocalDate fechaEntrada;

    @NotNull
    private LocalDate fechaSalida;

    @Min(1)
    private Integer huespedes = 1;

    @Column(length = 30)
    private String metodoPago;

    @Column(length = 20)
    private String estadoPago = "pendiente";

    @Column(length = 20)
    private String estado = "pendiente"; // pendiente | confirmada | cancelada

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 500)
    private String observaciones;
}
