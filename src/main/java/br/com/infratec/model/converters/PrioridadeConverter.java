package br.com.infratec.model.converters;

import br.com.infratec.enums.Prioridade;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;
import java.util.Optional;

public class PrioridadeConverter implements AttributeConverter<Prioridade, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final Prioridade prioridade) {
        if (Objects.isNull(prioridade)) {
            return null;
        }
        return prioridade.getId();
    }

    @Override
    public Prioridade convertToEntityAttribute(final Integer code) {
        return Optional.ofNullable(code).map(Prioridade::of).orElse(null);
    }
}
