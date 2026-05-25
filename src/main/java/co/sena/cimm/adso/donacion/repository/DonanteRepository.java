package co.sena.cimm.adso.donacion.repository;

import co.sena.cimm.adso.donacion.domain.Donante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.sena.cimm.adso.donacion.enums.TipoSangre;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonanteRepository extends JpaRepository<Donante, Long> {

    boolean existsByDocumento(String documento);

    Optional<Donante> findByDocumento(String documento);

    List<Donante> findByTipoSangre(TipoSangre tipoSangre);

    boolean existsByIdAndAceptaConsentimientoTrue(Long id);

}