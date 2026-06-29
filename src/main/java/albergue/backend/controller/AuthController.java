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

    // --- ACTUALIZAR PERFIL (AGREGA ESTO) ---
    @PutMapping("/actualizar")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(@RequestBody Usuario usuarioModificado) {
        System.out.println("DEBUG UPDATE: Intentando actualizar a: " + usuarioModificado.getCorreo());
        
        try {
            // Buscamos al usuario existente por su correo
            Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(usuarioModificado.getCorreo());
            
            if (usuarioOpt.isPresent()) {
                Usuario usuarioExistente = usuarioOpt.get();
                
                // Actualizamos únicamente los campos modificados
                usuarioExistente.setNombre(usuarioModificado.getNombre());
                
                // Si en el futuro agregas más campos (teléfono, dirección), se actualizan aquí:
                // usuarioExistente.setTelefono(usuarioModificado.getTelefono());

                usuarioRepository.save(usuarioExistente); // JPA hace el UPDATE en PostgreSQL
                System.out.println("DEBUG UPDATE: Usuario actualizado en BD con éxito.");
                
                return ResponseEntity.ok(Map.of("status", "success", "message", "Perfil actualizado en BD"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            System.out.println("DEBUG UPDATE: Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error interno en el servidor"));
        }
    }
}