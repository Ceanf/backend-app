package albergue.backend.repository;

import albergue.backend.model.Campana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampanaRepository extends JpaRepository<Campana, Integer> {
    // Hereda todas las operaciones CRUD nativas
}