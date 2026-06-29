package albergue.backend.repository;

import albergue.backend.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    // 🚀 LA CLAVE: Spring Boot mapea esto directo a la FK numérica (usuario_id) en PostgreSQL
    List<Solicitud> findByUsuarioId(Integer usuarioId);
    
    // Sincronizado exactamente con el campo 'estadoProceso' de tu entidad para el panel del administrador
    List<Solicitud> findByEstadoProceso(String estadoProceso);
    
    // Mantenemos este por si necesitas buscar postulaciones por correo de respaldo
    List<Solicitud> findByUsuarioCorreoIgnoreCase(String correo);
}