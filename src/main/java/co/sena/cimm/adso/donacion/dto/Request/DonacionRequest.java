package co.sena.cimm.adso.donacion.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonacionRequest {

    private Long donanteId;

    @NotNull(message = "La cantidad en ml es obligatoria")
    @Positive(message = "La cantidad debe ser un valor positivo")
    private Double cantidadML;

    @PastOrPresent(message = "La fecha de donación no puede ser futura")
    private LocalDate fechaDonacion;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    private String observaciones;

    private Boolean aceptaConsentimiento;
    private MultipartFile archivoConsentimiento;    
}