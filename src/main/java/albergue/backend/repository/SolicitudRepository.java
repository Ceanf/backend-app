package albergue.backend.repository;

import albergue.backend.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    // Codificamos un método extra súper útil por si en el futuro quieres 
    // listar únicamente las solicitudes de un usuario específico
    List<Solicitud> findByUsuarioId(Integer usuarioId);
    
    // Método extra útil por si quieres filtrar las solicitudes en base a un estado específico
    // Por ejemplo: traer solo las que están "Pendiente" para el administrador
    List<Solicitud> findByEstado(String estado);
}