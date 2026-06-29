package albergue.backend.controller;

import albergue.backend.model.Campana;
import albergue.backend.repository.CampanaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campanas")
@CrossOrigin(origins = "*")
public class CampanaController {

    @Autowired
    private CampanaRepository campanaRepository;

    // Listar todas las campañas (Útil para que los Adoptantes vean en qué apoyar)
    @GetMapping("/listar")
    public List<Campana> listarTodas() {
        return campanaRepository.findAll();
    }

    // Registrar una nueva meta de fondos desde el perfil del albergue
    @PostMapping("/crear")
    public ResponseEntity<?> crearCampana(@RequestBody Campana nuevaCampana) {
        try {
            Campana guardada = campanaRepository.save(nuevaCampana);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (Exception e) {
            System.err.println("[ERROR AL CREAR CAMPAÑA]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "No se pudo publicar la colecta: " + e.getMessage()));
        }
    }
}