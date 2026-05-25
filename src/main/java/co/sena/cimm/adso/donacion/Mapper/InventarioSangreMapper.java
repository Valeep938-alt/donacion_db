package co.sena.cimm.adso.donacion.Mapper;

import co.sena.cimm.adso.donacion.domain.InventarioSangre;
import co.sena.cimm.adso.donacion.dto.Request.InventarioRequest;
import co.sena.cimm.adso.donacion.dto.Response.InventarioResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventarioSangreMapper {

    @Mapping(target = "volumenDisponibleML", source = "cantidadML")
    @Mapping(target = "fechaActualizacion", ignore = true)
    InventarioResponse toResponse(InventarioSangre inventario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ultimaActualizacion", ignore = true)
    InventarioSangre toEntity(InventarioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ultimaActualizacion", ignore = true)
    void updateEntity(@MappingTarget InventarioSangre inventario, InventarioRequest request);
}