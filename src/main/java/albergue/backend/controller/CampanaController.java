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
    @PostMapping("/donar")
    @Transactional // 🔑 Mantiene la transacción segura en PostgreSQL
    public ResponseEntity<?> registrarDonacion(@RequestBody Map<String, Object> payload) {
        try {
            Integer campanaId = Integer.parseInt(payload.get("campanaId").toString());
            Integer usuarioId = Integer.parseInt(payload.get("usuarioId").toString());
            Double monto = Double.parseDouble(payload.get("monto").toString());

            // 1. Buscamos la campaña de forma segura
            Campana campana = campanaRepository.findById(campanaId)
                    .orElseThrow(() -> new RuntimeException("Campaña no encontrada con ID: " + campanaId));

            // 2. IMPORTANTE: Inyecta el repositorio de usuarios que ya tienes en tu
            // proyecto
            // Si tu repositorio se llama usuarioRepository, búscalo de esta forma:
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado con ID: " + usuarioId));

            // 3. Incrementamos el monto recaudado acumulado
            campana.setMontoRecaudado(campana.getMontoRecaudado() + monto);
            campanaRepository.save(campana);

            // 4. Registramos la transacción en el historial relacional
            Donacion donacion = new Donacion(monto, usuario, campana);
            donationRepository.save(donacion);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "¡Donación registrada con éxito!"));
        } catch (Exception e) {
            System.err.println("[ERROR EN CONTROLADOR DONAR]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
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