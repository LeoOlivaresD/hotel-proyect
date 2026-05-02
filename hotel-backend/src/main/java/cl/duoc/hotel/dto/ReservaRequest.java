package cl.duoc.hotel.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * DTO que recibe el frontend al crear una reserva.
 * Contiene datos del cliente + detalle de la reserva en un solo payload.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {

    // ── Datos del cliente ────────────────────────────────────────
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private String rut;

    @Email @NotBlank
    private String correo;

    private String telefono;

    // ── Detalle reserva ──────────────────────────────────────────
    @NotNull
    private Long habitacion_id;

    @Min(1)
    private Integer huespedes = 1;

    @NotNull
    private LocalDate fecha_entrada;

    @NotNull
    private LocalDate fecha_salida;

    private String observaciones;

    // ── Pago ─────────────────────────────────────────────────────
    private String metodo_pago = "credito";
    private String estado_pago = "pendiente";
}
