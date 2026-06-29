package albergue.backend.controller;

import albergue.backend.model.Mascota;
import albergue.backend.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // 👈 Importamos la respuesta estructurada HTTP
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {

    @Autowired
    private MascotaRepository mascotaRepository;

    @GetMapping("/listar")
    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll();
    }

    @PostMapping("/agregar")
    public Mascota agregar(@RequestBody Mascota nuevaMascota) {
        // Hibernate se encarga de la relación con Albergue basándose en el ID enviado
        return mascotaRepository.save(nuevaMascota);
    }

    @PutMapping("/actualizar/{id}")
    public Mascota actualizar(@PathVariable Integer id, @RequestBody Mascota mascotaActualizada) {
        return mascotaRepository.findById(id)
                .map(mascota -> {
                    mascota.setNombre(mascotaActualizada.getNombre());
                    mascota.setRaza(mascotaActualizada.getRaza());
                    mascota.setEdad(mascotaActualizada.getEdad());
                    mascota.setNivelEnergia(mascotaActualizada.getNivelEnergia());
                    mascota.setTamano(mascotaActualizada.getTamano());
                    
                    // 👇 Cambiado aquí para que use el campo real de tu modelo Java
                    mascota.setHistoria(mascotaActualizada.getHistoria());
                    
                    if (mascotaActualizada.getFotoUrl() != null) {
                        mascota.setFotoUrl(mascotaActualizada.getFotoUrl());
                    }
                    return mascotaRepository.save(mascota);
                })
                .orElseGet(() -> {
                    mascotaActualizada.setId(id);
                    return mascotaRepository.save(mascotaActualizada);
                });
    }

    // 👇 EL NUEVO MÉTODO MÁGICO QUE TE FALTABA PARA ELIMINAR DESDE LA APP
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            // 1. Verificamos si el perrito o gatito realmente existe en Supabase
            if (!mascotaRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            // 2. Ejecutamos la eliminación en la base de datos PostgreSQL
            mascotaRepository.deleteById(id);
            
            // 3. Respondemos un código 200 OK limpio con un JSON válido
            return ResponseEntity.ok().body("{\"message\": \"Mascota eliminada con éxito\"}");
            
        } catch (Exception e) {
            // Si salta un error (como restricción de FK si ya fue postulada), te avisará en consola
            System.err.println("[ERROR AL ELIMINAR MASCOTA]: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
}