package co.sena.cimm.adso.donacion.dto.Response;

import co.sena.cimm.adso.donacion.enums.TipoSangre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {

    private TipoSangre tipoSangre;
    private Double volumenDisponibleML;
    private LocalDate fechaActualizacion;
}