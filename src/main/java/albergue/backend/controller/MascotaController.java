package albergue.backend.controller;

import albergue.backend.model.Mascota;
import albergue.backend.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}