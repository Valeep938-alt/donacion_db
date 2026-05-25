package co.sena.cimm.adso.donacion.dto;

import lombok.Data;

@Data // Esto genera los Getters y Setters automáticamente gracias a Lombok
public class LoginRequest {
    private String username;
    private String password;
}