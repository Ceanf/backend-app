package albergue.backend.controller;

import albergue.backend.model.Campana;
import albergue.backend.model.Donacion;
import albergue.backend.model.Usuario;
import albergue.backend.repository.CampanaRepository;
import albergue.backend.repository.DonacionRepository;
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
    @Transactional // 🔑 Garantiza consistencia: Actualiza la meta y guarda el historial en un solo bloque
    public ResponseEntity<?> registrarDonacion(@RequestBody Map<String, Object> payload) {
        try {
            Integer campanaId = Integer.parseInt(payload.get("campanaId").toString());
            Integer usuarioId = Integer.parseInt(payload.get("usuarioId").toString());
            Double monto = Double.parseDouble(payload.get("monto").toString());

            // A. Buscamos la campaña afectada en PostgreSQL
            Campana campana = campanaRepository.findById(campanaId)
                    .orElseThrow(() -> new RuntimeException("Campaña de recaudación no encontrada"));
            
            // B. Incrementamos el acumulado con el monto enviado desde el Motorola
            campana.setMontoRecaudado(campana.getMontoRecaudado() + monto);
            campanaRepository.save(campana);

            // C. Instanciamos el usuario comprador de manera referencial para la FK
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);

            // D. Registramos la transacción legítima en la tabla 'donaciones'
            Donacion donacion = new Donacion(monto, usuario, campana);
            donationRepository.save(donacion);

            return ResponseEntity.ok(Map.of(
                "status", "success", 
                "message", "¡Donación registrada y barra de progreso actualizada con éxito!"
            ));
        } catch (Exception e) {
            System.err.println("[ERROR EN TRANSACCIÓN DONAR]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Fallo al procesar abono: " + e.getMessage()));
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