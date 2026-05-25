package co.sena.cimm.adso.donacion.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimientoRequest {

    @NotNull(message = "El ID del donante es obligatorio")
    private Long donanteId;

    @NotNull(message = "Debe indicar si acepta el consentimiento informado")
    private Boolean aceptaConsentimiento;

    private String firmaConsentimiento;

    @PastOrPresent(message = "La fecha de firma no puede ser futura")
    private LocalDateTime fechaFirma;
}