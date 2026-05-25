package co.sena.cimm.adso.donacion.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimientoResponse {

    private Long id;
    private Long donanteId;
    private Boolean aceptaConsentimiento;
    private String firmaConsentimiento;
    private LocalDateTime fechaFirma;

    private String donanteNombres;
    private String donanteDocumento;
    private String archivoConsentimiento;
}