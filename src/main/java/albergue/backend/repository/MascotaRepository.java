package albergue.backend.repository;

import albergue.backend.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    
    // 👇 ¡AGREGA ESTO!: JPA generará el SQL automático para buscar por la columna especie ignorando mayúsculas/minúsculas
    List<Mascota> findByEspecieIgnoreCase(String especie);
}