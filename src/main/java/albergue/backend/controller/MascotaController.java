package albergue.backend.controller;

import albergue.backend.model.Mascota;
import albergue.backend.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}