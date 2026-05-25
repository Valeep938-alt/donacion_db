package co.sena.cimm.adso.donacion.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonacionResponse {
    private Long id;
    private String codigoDonacion;
    
    // ID del donante
    private Long donanteId;
    
    private String donanteNombres;
    private String donanteApellidos;
    private String donanteDocumento;
    
    private Double cantidadML;
    private LocalDate fechaDonacion;
    private String observaciones;

    private String tipoSangre;
    private String archivoConsentimiento;
}