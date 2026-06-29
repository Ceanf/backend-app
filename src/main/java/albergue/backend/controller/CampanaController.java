package albergue.backend.controller;

import albergue.backend.model.Campana;
import albergue.backend.model.Donacion;
import albergue.backend.model.Usuario;
import albergue.backend.repository.CampanaRepository;
import albergue.backend.repository.DonacionRepository;
import albergue.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campanas")
@CrossOrigin(origins = "*")
public class CampanaController {

    @Autowired
    private CampanaRepository campanaRepository;

    @Autowired
    private DonacionRepository donationRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; // Cambia el nombre según cómo se llame tu interfaz de usuarios
    // =========================================================================
    // 1. LISTAR TODAS LAS CAMPAÑAS (Para la pestaña de Impacto del Adoptante)
    // =========================================================================

    @GetMapping("/listar")
    public List<Campana> listarTodas() {
        return campanaRepository.findAll();
    }

    // =========================================================================
    // 2. CREAR NUEVA CAMPAÑA DE RECAUDACIÓN (Desde el Perfil del Albergue)
    // =========================================================================
    @PostMapping("/crear")
    public ResponseEntity<?> crearCampana(@RequestBody Campana nuevaCampana) {
        try {
            // Aseguramos que inicie en 0 soles de recaudación nativa
            if (nuevaCampana.getMontoRecaudado() == null) {
                nuevaCampana.setMontoRecaudado(0.0);
            }
            Campana guardada = campanaRepository.save(nuevaCampana);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (Exception e) {
            System.err.println("[ERROR AL CREAR CAMPAÑA]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "No se pudo publicar la colecta: " + e.getMessage()));
        }
    }

    // =========================================================================
    // 3. PROCESAR DONACIÓN EN TIEMPO REAL (Lógica Transaccional Combinada)
    // =========================================================================
   // =========================================================================
    // 3. PROCESAR DONACIÓN EN TIEMPO REAL (Versión Blindada anti-Error 500)
    // =========================================================================
    @PostMapping("/donar")
    @Transactional 
    public ResponseEntity<?> registrarDonacion(@RequestBody DonacionRequest request) {
        try {
            // 1. Buscamos la campaña de forma segura usando el DTO
            Campana campana = campanaRepository.findById(request.getCampanaId())
                    .orElseThrow(() -> new RuntimeException("Campaña no encontrada con ID: " + request.getCampanaId()));
            
            // 2. Buscamos el usuario adoptante de forma segura
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado con ID: " + request.getUsuarioId()));

            // 3. Incrementamos el monto acumulado en PostgreSQL
            campana.setMontoRecaudado(campana.getMontoRecaudado() + request.getMonto());
            campanaRepository.save(campana);

            // 4. Registramos la transacción legítima en la tabla 'donaciones'
            Donacion donacion = new Donacion(request.getMonto(), usuario, campana);
            donationRepository.save(donacion);

            return ResponseEntity.ok(Map.of(
                "status", "success", 
                "message", "¡Donación registrada y barra de progreso actualizada con éxito!"
            ));
        } catch (Exception e) {
            System.err.println("[ERROR CRÍTICO EN DONAR]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // 👇 🚀 CLASE AUXILIAR (DTO): Colócala al final de tu archivo CampanaController.java (afuera del último corchete de los métodos, pero dentro de la clase principal)
    public static class DonacionRequest {
        private Integer campanaId;
        private Integer usuarioId;
        private Double monto;

        // Getters y Setters necesarios para Jackson
        public Integer getCampanaId() { return campanaId; }
        public void setCampanaId(Integer campanaId) { this.campanaId = campanaId; }

        public Integer getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

        public Double getMonto() { return monto; }
        public void setMonto(Double monto) { this.monto = monto; }
    }
    // =========================================================================
    // 4. VER HISTORIAL DE DONANTES DE UNA CAMPAÑA (Para el Panel del Albergue)
    // =========================================================================
    @GetMapping("/{campanaId}/donantes")
    public ResponseEntity<?> listarDonantesCampana(@PathVariable Integer campanaId) {
        try {
            List<Donacion> donantes = donationRepository.findByCampanaIdOrderByFechaDesc(campanaId);
            return ResponseEntity.ok(donantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "No se pudo obtener el historial"));
        }
    }
}