package cl.duoc.hotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 100)
    private String nombre;

    @NotBlank
    @Column(length = 100)
    private String apellido;

    @NotBlank
    @Column(unique = true, length = 20)
    private String rut;

    @Email
    @NotBlank
    @Column(length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;
}
