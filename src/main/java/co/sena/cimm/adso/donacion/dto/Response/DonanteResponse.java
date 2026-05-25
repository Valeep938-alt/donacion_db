package co.sena.cimm.adso.donacion.dto.Response;

import co.sena.cimm.adso.donacion.enums.TipoSangre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonanteResponse {

    private Long id;

    // Datos personales
    private String nombres;
    private String apellidos;
    private String documento;
    private LocalDate fechaNacimiento;

    // Datos médicos
    private TipoSangre tipoSangre;
    private Double peso;

    // Contacto
    private String telefono;
    private String correo;
    private String direccion;

    // Historial de donación
    private LocalDate fechaUltimaDonacion;

    // Consentimiento
    private Boolean aceptaConsentimiento;
    private Boolean tieneConsentimientoFirmado;

    // Campo calculado
    public Integer getEdad() {
        if (fechaNacimiento == null) return null;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}