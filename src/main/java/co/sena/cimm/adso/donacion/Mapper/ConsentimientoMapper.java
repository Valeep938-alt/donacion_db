package co.sena.cimm.adso.donacion.Mapper;

import co.sena.cimm.adso.donacion.domain.Consentimiento;
import co.sena.cimm.adso.donacion.dto.Request.ConsentimientoRequest;
import co.sena.cimm.adso.donacion.dto.Response.ConsentimientoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConsentimientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donante", ignore = true)
    @Mapping(target = "firmaConsentimiento", source = "firmaConsentimiento")
    @Mapping(target = "activo", source = "aceptaConsentimiento")
    @Mapping(target = "tipoFirma", constant = "PDF")
    @Mapping(target = "fechaFirma", ignore = true)
    @Mapping(target = "archivoConsentimiento", ignore = true)
    Consentimiento toEntity(ConsentimientoRequest request);

    @Mapping(target = "donanteId", source = "donante.id")
    @Mapping(target = "donanteNombres", expression = "java(c.getDonante().getNombres() + \" \" + c.getDonante().getApellidos())")
    @Mapping(target = "donanteDocumento", source = "donante.documento")
    @Mapping(target = "aceptaConsentimiento", source = "activo")
    @Mapping(target = "firmaConsentimiento", source = "firmaConsentimiento")
    @Mapping(target = "archivoConsentimiento", source = "archivoConsentimiento")
    ConsentimientoResponse toResponse(Consentimiento c);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donante", ignore = true)
    @Mapping(target = "firmaConsentimiento", source = "firmaConsentimiento")
    @Mapping(target = "activo", source = "aceptaConsentimiento")
    @Mapping(target = "tipoFirma", constant = "PDF")
    @Mapping(target = "fechaFirma", ignore = true)
    @Mapping(target = "archivoConsentimiento", ignore = true)
    void updateEntity(ConsentimientoRequest request, @MappingTarget Consentimiento consentimiento);
}