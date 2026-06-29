package albergue.backend.controller;

import albergue.backend.model.Campana;
import albergue.backend.model.Donacion;
import albergue.backend.model.Usuario;
import albergue.backend.repository.CampanaRepository;
import albergue.backend.repository.DonacionRepository;
import albergue.backend.repository.UsuarioRepository; // 🚀 Coincide exactamente con tu paquete
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
    private UsuarioRepository usuarioRepository; // 🔑 Conectado a tu repositorio nativo

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
    // 3. PROCESAR DONACIÓN EN TIEMPO REAL (Versión Blindada anti-Error 500)
    // =========================================================================
    @PostMapping("/donar")
    @Transactional // 🔑 Asegura la atomicidad en PostgreSQL (Supabase)
    public ResponseEntity<?> registrarDonacion(@RequestBody DonacionRequest request) {
        try {
            System.out.println("LOG BACKEND: Procesando abono para Campana ID: " + request.getCampanaId());

            // A. Buscamos la campaña en la base de datos
            Campana campana = campanaRepository.findById(request.getCampanaId())
                    .orElseThrow(() -> new RuntimeException("Campaña no encontrada con ID: " + request.getCampanaId()));

            // B. Buscamos al usuario usando tu findById heredado por JpaRepository
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));

            // C. Sumamos el abono al total recaudado
            campana.setMontoRecaudado(campana.getMontoRecaudado() + request.getMonto());
            campanaRepository.save(campana);

            // D. Instanciamos la donación usando los setters limpios de tu entidad
            Donacion donacion = new Donacion();
            donacion.setMonto(request.getMonto());
            donacion.setUsuario(usuario);
            donacion.setCampana(campana);
            donacion.setFecha(java.time.LocalDateTime.now());

            // E. Guardamos en la tabla de donaciones
            donationRepository.save(donacion);

            // 🚀 RESPUESTA PLANA: Corta de raíz el bucle infinito de Jackson
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "¡Donación registrada y barra de progreso actualizada con éxito!"
            ));

        } catch (Exception e) {
            System.err.println("[FALLO CRÍTICO EN DONAR]: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error interno: " + e.getMessage()));
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

    // =========================================================================
    // 🚀 DTO INTERNO: Mapea el JSON plano del Motorola de forma segura
    // =========================================================================
    public static class DonacionRequest {
        private Integer campanaId;
        private Integer usuarioId;
        private Double monto;

        public DonacionRequest() {}

        public Integer getCampanaId() { return campanaId; }
        public void setCampanaId(Integer campanaId) { this.campanaId = campanaId; }

        public Integer getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

        public Double getMonto() { return monto; }
        public void setMonto(Double monto) { this.monto = monto; }
    }
}