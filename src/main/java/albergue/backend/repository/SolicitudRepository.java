package albergue.backend.repository;

import albergue.backend.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    // 👇 CORREGIDO: Spring Boot entiende que debe entrar al objeto 'usuario' y sacar su 'id'
    List<Solicitud> findByUsuario_Id(Integer usuarioId);
    
    // 👇 CORREGIDO: Sincronizado exactamente con el campo 'estadoProceso' de tu entidad Solicitud.java
    List<Solicitud> findByEstadoProceso(String estadoProceso);
}