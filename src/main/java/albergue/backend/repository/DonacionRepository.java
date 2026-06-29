package albergue.backend.repository;

import albergue.backend.model.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Integer> {
    // 💡 Permite buscar el historial específico de una campaña para ver los donantes
    List<Donacion> findByCampanaIdOrderByFechaDesc(Integer campanaId);
}