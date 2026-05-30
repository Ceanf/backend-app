package albergue.backend.controller;

import albergue.backend.model.Usuario;
import albergue.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        System.out.println("DEBUG LOGIN: Intentando login para: " + email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(email);

        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
            Usuario user = usuarioOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("role", user.getRol());
            response.put("nombre", user.getNombre());
            return ResponseEntity.ok(response);
        }

        System.out.println("DEBUG LOGIN: Fallo de autenticación para: " + email);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", "error", "message", "Correo o contraseña incorrectos"));
    }

    // --- REGISTRO ---
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Usuario nuevoUsuario) {
        System.out.println("DEBUG REGISTRO: Intentando registrar a: " + nuevoUsuario.getCorreo());

        // 1. Validar si el correo ya existe
        if (usuarioRepository.findByCorreo(nuevoUsuario.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "error", "message", "El correo ya está registrado"));
        }

        // 2. Guardar el nuevo usuario
        try {
            // Aseguramos que el rol tenga un valor por defecto si no viene en el JSON
            if (nuevoUsuario.getRol() == null || nuevoUsuario.getRol().isEmpty()) {
                nuevoUsuario.setRol("ROLE_ADOPTANTE");
            }
            
            usuarioRepository.save(nuevoUsuario);
            System.out.println("DEBUG REGISTRO: Usuario guardado exitosamente.");
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Usuario registrado exitosamente"));
        } catch (Exception e) {
            System.out.println("DEBUG REGISTRO: Error al guardar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al procesar el registro"));
        }
    }
}