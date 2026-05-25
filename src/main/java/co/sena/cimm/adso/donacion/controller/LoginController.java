package co.sena.cimm.adso.donacion.controller;

import co.sena.cimm.adso.donacion.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // La ruta base será /api/auth
public class LoginController {

    @PostMapping("/login") // La ruta completa será /api/auth/login
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        
        // --- AQUÍ ESTÁ LA LÓGICA QUEMADA ---
        
        // Definimos el usuario y contraseña correctos
        String usuarioCorrecto = "admin";
        String passwordCorrecto = "1234";

        // Comparamos lo que llegó con lo que tenemos guardado
        if (request.getUsername().equals(usuarioCorrecto) && 
            request.getPassword().equals(passwordCorrecto)) {
            
            // Si coinciden, devolvemos un mensaje de éxito y estado 200 (OK)
            return ResponseEntity.ok("Login exitoso. Bienvenido Admin.");
            
        } else {
            
            // Si no coinciden, devolvemos error y estado 401 (Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Usuario o contraseña incorrectos.");
        }
    }
}