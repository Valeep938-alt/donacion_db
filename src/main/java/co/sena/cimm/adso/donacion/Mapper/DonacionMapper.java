package co.sena.cimm.adso.donacion.Mapper;

import co.sena.cimm.adso.donacion.domain.Donacion;
import co.sena.cimm.adso.donacion.dto.Request.DonacionRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonacionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DonacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigoDonacion", ignore = true)
    @Mapping(target = "donante", ignore = true)
    @Mapping(target = "tipoSangre", ignore = true)
    @Mapping(target = "cantidadML", expression = "java(request.getCantidadML().intValue())")
    @Mapping(target = "fechaDonacion", expression = "java(request.getFechaDonacion())")
    @Mapping(target = "observaciones", expression = "java(request.getObservaciones())")
    Donacion toEntity(DonacionRequest request);

    @Mapping(target = "donanteId", expression = "java(donacion.getDonante().getId())")
    @Mapping(target = "donanteNombres", expression = "java(donacion.getDonante().getNombres())")
    @Mapping(target = "donanteApellidos", expression = "java(donacion.getDonante().getApellidos())")
    @Mapping(target = "donanteDocumento", expression = "java(donacion.getDonante().getDocumento())")
    @Mapping(target = "cantidadML", expression = "java((double) donacion.getCantidadML())")
    @Mapping(target = "fechaDonacion", expression = "java(donacion.getFechaDonacion())")
    @Mapping(target = "observaciones", expression = "java(donacion.getObservaciones())")
    @Mapping(target = "codigoDonacion", expression = "java(donacion.getCodigoDonacion())")
    @Mapping(target = "tipoSangre", expression = "java(donacion.getTipoSangre())")
    DonacionResponse toResponse(Donacion donacion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigoDonacion", ignore = true)
    @Mapping(target = "donante", ignore = true)
    @Mapping(target = "tipoSangre", ignore = true)
    @Mapping(target = "cantidadML", expression = "java(request.getCantidadML().intValue())")
    @Mapping(target = "fechaDonacion", expression = "java(request.getFechaDonacion())")
    @Mapping(target = "observaciones", expression = "java(request.getObservaciones())")
    void updateEntity(DonacionRequest request, @MappingTarget Donacion donacion);
}