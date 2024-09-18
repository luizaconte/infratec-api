package br.com.infratec.model.converters;

import br.com.infratec.enums.TipoUsuario;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;
import java.util.Optional;

public class TipoUsuarioConverter implements AttributeConverter<TipoUsuario, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final TipoUsuario tipoUsuario) {
        if (Objects.isNull(tipoUsuario)) {
            return null;
        }
        return tipoUsuario.getId();
    }

    @Override
    public TipoUsuario convertToEntityAttribute(final Integer code) {
        return Optional.ofNullable(code).map(TipoUsuario::of).orElse(null);
    }
}
