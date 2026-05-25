package co.sena.cimm.adso.donacion.Mapper;

import co.sena.cimm.adso.donacion.domain.Donante;
import co.sena.cimm.adso.donacion.dto.Request.DonanteRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonanteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DonanteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombres", expression = "java(request.getNombres())")
    @Mapping(target = "apellidos", expression = "java(request.getApellidos())")
    @Mapping(target = "documento", expression = "java(request.getDocumento())")
    @Mapping(target = "fechaNacimiento", expression = "java(request.getFechaNacimiento())")
    @Mapping(target = "tipoSangre", expression = "java(request.getTipoSangre())")
    @Mapping(target = "peso", expression = "java(request.getPeso())")
    @Mapping(target = "telefono", expression = "java(request.getTelefono())")
    @Mapping(target = "correo", expression = "java(request.getCorreo())")
    @Mapping(target = "direccion", expression = "java(request.getDireccion())")
    @Mapping(target = "fechaUltimaDonacion", expression = "java(request.getFechaUltimaDonacion())")
    @Mapping(target = "aceptaConsentimiento", expression = "java(request.getAceptaConsentimiento())")
    @Mapping(target = "firmaConsentimiento", expression = "java(request.getFirmaConsentimiento())")
    Donante toEntity(DonanteRequest request);

    @Mapping(target = "tieneConsentimientoFirmado",
             expression = "java(donante.getFirmaConsentimiento() != null && !donante.getFirmaConsentimiento().isBlank())")
    DonanteResponse toResponse(Donante donante);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombres", expression = "java(request.getNombres())")
    @Mapping(target = "apellidos", expression = "java(request.getApellidos())")
    @Mapping(target = "documento", expression = "java(request.getDocumento())")
    @Mapping(target = "fechaNacimiento", expression = "java(request.getFechaNacimiento())")
    @Mapping(target = "tipoSangre", expression = "java(request.getTipoSangre())")
    @Mapping(target = "peso", expression = "java(request.getPeso())")
    @Mapping(target = "telefono", expression = "java(request.getTelefono())")
    @Mapping(target = "correo", expression = "java(request.getCorreo())")
    @Mapping(target = "direccion", expression = "java(request.getDireccion())")
    @Mapping(target = "fechaUltimaDonacion", expression = "java(request.getFechaUltimaDonacion())")
    @Mapping(target = "aceptaConsentimiento", expression = "java(request.getAceptaConsentimiento())")
    @Mapping(target = "firmaConsentimiento", expression = "java(request.getFirmaConsentimiento())")
    void updateEntity(DonanteRequest request, @MappingTarget Donante donante);
}