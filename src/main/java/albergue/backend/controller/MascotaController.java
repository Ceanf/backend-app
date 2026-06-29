package albergue.backend.controller;

import albergue.backend.model.Mascota;
import albergue.backend.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*") // Crucial para la conexión nativa con tu Motorola
public class MascotaController {

    @Autowired
    private MascotaRepository mascotaRepository;

    // 1. LISTAR TODAS LAS MASCOTAS
    @GetMapping("/listar")
    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll();
    }

    // 2. AGREGAR NUEVA MASCOTA (OPTIMIZADO CON RESPONSE ENTITY)
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestBody Mascota nuevaMascota) {
        try {
            System.out.println("DEBUG BACKEND: Registrando nueva mascota: " + nuevaMascota.getNombre());
            
            // Hibernate procesa la inserción usando las relaciones de ID numérico enviadas en el JSON
            Mascota guardada = mascotaRepository.save(nuevaMascota);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (Exception e) {
            System.err.println("[ERROR AL AGREGAR MASCOTA]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "No se pudo guardar la mascota: " + e.getMessage()));
        }
    }

    // 3. ACTUALIZAR MASCOTA EXISTENTE POR SU ID REAL
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Mascota mascotaActualizada) {
        System.out.println("DEBUG BACKEND: Solicitud de actualización para Mascota ID: " + id);
        
        return mascotaRepository.findById(id)
                .map(mascota -> {
                    mascota.setNombre(mascotaActualizada.getNombre());
                    mascota.setRaza(mascotaActualizada.getRaza());
                    mascota.setEdad(mascotaActualizada.getEdad());
                    mascota.setNivelEnergia(mascotaActualizada.getNivelEnergia());
                    mascota.setTamano(mascotaActualizada.getTamano());
                    mascota.setHistoria(mascotaActualizada.getHistoria());
                    
                    if (mascotaActualizada.getFotoUrl() != null) {
                        mascota.setFotoUrl(mascotaActualizada.getFotoUrl());
                    }
                    
                    Mascota persistida = mascotaRepository.save(mascota);
                    return ResponseEntity.ok(persistida);
                })
                .orElseGet(() -> {
                    mascotaActualizada.setId(id);
                    Mascota creada = mascotaRepository.save(mascotaActualizada);
                    return ResponseEntity.status(HttpStatus.CREATED).body(creada);
                });
    }

    // 4. ELIMINAR MASCOTA DESDE LA APP
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            System.out.println("DEBUG BACKEND: Intentando eliminar Mascota ID: " + id);
            
            // Verificamos si la mascota realmente existe en Supabase
            if (!mascotaRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "La mascota especificada no existe"));
            }
            
            // Ejecutamos la eliminación en la base de datos PostgreSQL
            mascotaRepository.deleteById(id);
            
            // Respondemos un código 200 OK limpio con un JSON válido para evitar que el front tire error de parseo
            return ResponseEntity.ok().body(Map.of("status", "success", "message", "Mascota eliminada con éxito"));
            
        } catch (Exception e) {
            // Si salta un error (como restricción de FK si tiene solicitudes activas), se registra en consola
            System.err.println("[ERROR AL ELIMINAR MASCOTA]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error de integridad relacional: " + e.getMessage()));
        }
    }
}