package br.com.infratec.model.converters;

import br.com.infratec.enums.StatusProcessamento;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;
import java.util.Optional;

public class StatusProcessamentoConverter implements AttributeConverter<StatusProcessamento, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final StatusProcessamento status) {
        if (Objects.isNull(status)) {
            return null;
        }
        return status.getId();
    }

    @Override
    public StatusProcessamento convertToEntityAttribute(final Integer code) {
        return Optional.ofNullable(code).map(StatusProcessamento::of).orElse(null);
    }
}
