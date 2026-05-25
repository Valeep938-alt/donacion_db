package co.sena.cimm.adso.donacion.dto.Request;

import co.sena.cimm.adso.donacion.enums.TipoSangre;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {

    @NotNull(message = "El tipo de sangre es obligatorio")
    private TipoSangre tipoSangre;

    @NotNull(message = "La cantidad en ml es obligatoria")
    @Positive(message = "La cantidad debe ser un valor positivo")
    private Double cantidadML;

    @Size(max = 250, message = "El motivo de ajuste no puede exceder los 250 caracteres")
    private String motivoAjuste;
}