package albergue.backend.repository;

import albergue.backend.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    // Ya tienes acceso a findAll(), save(), findById(), etc. automáticamente
}