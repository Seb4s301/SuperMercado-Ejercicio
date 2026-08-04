package lat.sebascasavilca.EjercicioSupermercado.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaDto {
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private Double total;

    @NotNull(message = "Debe indicar la sucursal")
    private Long idSucursal;

    @Valid
    private List<DetalleVentaDto> detalle;
}
