package co.sena.cimm.adso.donacion.domain;

import co.sena.cimm.adso.donacion.config.converter.TipoSangreConverter;
import co.sena.cimm.adso.donacion.enums.TipoSangre;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioSangre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = TipoSangreConverter.class)
    @Column(name = "tipo_sangre", nullable = false, unique = true, length = 20)
    private TipoSangre tipoSangre;

    @Column(name = "cantidad_ml", nullable = false)
    private Integer cantidadML;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
}