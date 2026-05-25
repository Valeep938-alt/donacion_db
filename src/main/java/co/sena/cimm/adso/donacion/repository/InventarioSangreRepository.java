package co.sena.cimm.adso.donacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.sena.cimm.adso.donacion.domain.InventarioSangre;
import co.sena.cimm.adso.donacion.enums.TipoSangre;

import java.util.Optional;

@Repository
public interface InventarioSangreRepository extends JpaRepository<InventarioSangre, Long> {

    Optional<InventarioSangre> findByTipoSangre(TipoSangre tipoSangre);
}