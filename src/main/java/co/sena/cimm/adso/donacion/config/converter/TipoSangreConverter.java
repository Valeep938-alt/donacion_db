package co.sena.cimm.adso.donacion.config.converter;

import co.sena.cimm.adso.donacion.enums.TipoSangre;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoSangreConverter implements AttributeConverter<TipoSangre, String> {

    @Override
    public String convertToDatabaseColumn(TipoSangre attribute) {
        // Enum → String para guardar en BD
        return attribute != null ? attribute.getDescripcion() : null;
    }

    @Override
    public TipoSangre convertToEntityAttribute(String dbData) {
        // String de BD → Enum para usar en Java
        if (dbData == null || dbData.isEmpty()) return null;
        return TipoSangre.fromDescripcion(dbData);
    }
}