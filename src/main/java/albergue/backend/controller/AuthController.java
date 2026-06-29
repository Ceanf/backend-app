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

    // --- LOGIN (CORREGIDO PARA ID REAL) ---
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
            
            // 👇 🚀 SOLUCIÓN CLAVE: Devolvemos el ID único de la base de datos
            response.put("id", user.getId()); 
            
            System.out.println("DEBUG LOGIN: Login exitoso. ID enviado al dispositivo: " + user.getId());
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
            if (nuevoUsuario.getRol() == null || nuevoUsuario.getRol().isEmpty()) {
                nuevoUsuario.setRol("ROLE_ADOPTANTE");
            }
            
            Usuario guardado = usuarioRepository.save(nuevoUsuario);
            System.out.println("DEBUG REGISTRO: Usuario guardado exitosamente con ID: " + guardado.getId());
            
            return ResponseEntity.ok(Map.of(
                "status", "success", 
                "message", "Usuario registrado exitosamente",
                "id", guardado.getId() // Retornamos también el ID al registrarse
            ));
        } catch (Exception e) {
            System.out.println("DEBUG REGISTRO: Error al guardar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al procesar el registro"));
        }
    }

    // --- ACTUALIZAR PERFIL (CORREGIDO PARA TRABAJAR POR ID REAL) ---
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(@PathVariable Integer id, @RequestBody Usuario usuarioModificado) {
        System.out.println("DEBUG UPDATE: Intentando actualizar usuario con ID único: " + id);
        
        try {
            // 🚀 Buscamos directamente por la llave primaria (ID) en lugar de cadenas de texto
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuarioExistente = usuarioOpt.get();
                
                // Sincronizamos únicamente los campos permitidos desde el perfil del front
                usuarioExistente.setNombre(usuarioModificado.getNombre());
                usuarioExistente.setCorreo(usuarioModificado.getCorreo()); // Permite actualización de correo fluida
                
                // Si la entidad maneja más campos, se escalan limpiamente aquí:
                // usuarioExistente.setTelefono(usuarioModificado.getTelefono());

                usuarioRepository.save(usuarioExistente); // El ORM ejecuta un UPDATE limpio
                System.out.println("DEBUG UPDATE: Registro de ID " + id + " actualizado en PostgreSQL.");
                
                return ResponseEntity.ok(Map.of("status", "success", "message", "Perfil actualizado con éxito en la base de datos"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "El ID de usuario especificado no existe"));
            }
        } catch (Exception e) {
            System.out.println("DEBUG UPDATE: Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error interno en el servidor ferroviario"));
        }
    }
}