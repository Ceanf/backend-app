package albergue.backend.controller;

import albergue.backend.model.Solicitud;
import albergue.backend.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*") // Crucial para la conexión nativa con tu Motorola
public class SolicitudController {

    @Autowired
    private SolicitudRepository solicitudRepository;

    // 1. ENDPOINT PARA RECIBIR LA POSTULACIÓN DEL ADOPTANTE
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarSolicitud(@RequestBody Solicitud nuevaSolicitud) {
        try {
            nuevaSolicitud.setFechaEnvio(LocalDateTime.now()); // Registra la fecha y hora actual
            nuevaSolicitud.setEstadoProceso("Pendiente"); 
            
            Solicitud guardada = solicitudRepository.save(nuevaSolicitud);
            System.out.println("DEBUG BACKEND: Solicitud guardada con éxito. ID Solicitud: " + guardada.getId());
            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            System.err.println("[ERROR AL GUARDAR SOLICITUD]: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    // 2. ENDPOINT PARA QUE EL ADMINISTRADOR LISTE TODAS LAS POSTULACIONES
    @GetMapping("/listar")
    public List<Solicitud> listarTodas() {
        return solicitudRepository.findAll();
    }

    // 3. ENDPOINT PARA QUE EL ADMIN CAMBIE EL ESTADO (APROBAR / RECHAZAR)
    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        return solicitudRepository.findById(id)
                .map(solicitud -> {
                    solicitud.setEstadoProceso(nuevoEstado); 
                    solicitudRepository.save(solicitud);
                    return ResponseEntity.ok().body("{\"message\": \"Estado actualizado a " + nuevoEstado + "\"}");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 👇 4. NUEVO ENDPOINT (CORREGIDO): BUSCAR SOLICITUDES EXCLUSIVAS POR EL ID REAL DEL USUARIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Solicitud>> listarPorUsuarioId(@PathVariable Integer usuarioId) {
        System.out.println("DEBUG BACKEND: Buscando solicitudes en PostgreSQL para el Usuario ID: " + usuarioId);
        try {
            // 🚀 Buscamos directamente por la llave foránea numérica en Supabase
            List<Solicitud> misSolicitudes = solicitudRepository.findByUsuarioId(usuarioId);
            System.out.println("DEBUG BACKEND: Solicitudes encontradas: " + misSolicitudes.size());
            return ResponseEntity.ok(misSolicitudes);
        } catch (Exception e) {
            System.err.println("[ERROR BUSQUEDA SOLICITUDES BY ID]: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}