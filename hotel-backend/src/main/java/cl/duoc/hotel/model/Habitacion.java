package cl.duoc.hotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "habitacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, length = 10)
    private String numero;

    @NotBlank
    @Column(length = 30)
    private String tipo;

    @Min(1)
    private Integer piso;

    @Min(1)
    private Integer capacidad;

    @DecimalMin("0.0")
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 20)
    private String estado = "disponible"; // disponible | ocupada | mantenimiento

    @Column(length = 255)
    private String amenidades;

    private Integer m2;
}
