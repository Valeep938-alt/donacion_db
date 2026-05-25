package co.sena.cimm.adso.donacion.repository;

import co.sena.cimm.adso.donacion.domain.Consentimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentimientoRepository extends JpaRepository<Consentimiento, Long> {
    
    Optional<Consentimiento> findByDonanteId(Long donanteId);
    
    boolean existsByDonanteId(Long donanteId);
}