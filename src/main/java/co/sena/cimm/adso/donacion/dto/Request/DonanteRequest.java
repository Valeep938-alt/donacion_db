package co.sena.cimm.adso.donacion.dto.Request;

import co.sena.cimm.adso.donacion.enums.TipoSangre;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonanteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellidos;

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El tipo de sangre es obligatorio")
    private TipoSangre tipoSangre;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "50.0", message = "El peso mínimo para donar es 50 kg")
    @Positive(message = "El peso debe ser un valor positivo")
    private Double peso;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{7,10}$", message = "El teléfono debe tener entre 7 y 10 dígitos")
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @PastOrPresent(message = "La fecha de última donación no puede ser futura")
    private LocalDate fechaUltimaDonacion;

    @NotNull(message = "Debe indicar si acepta el consentimiento")
    private Boolean aceptaConsentimiento;

    private String firmaConsentimiento;
}