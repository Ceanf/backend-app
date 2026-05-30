package albergue.backend.repository;

import albergue.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Esto permite buscar usuarios por correo automáticamente
    Optional<Usuario> findByCorreo(String correo);
}