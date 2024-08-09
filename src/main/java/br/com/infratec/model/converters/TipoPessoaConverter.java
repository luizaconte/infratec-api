package br.com.infratec.model.converters;

import br.com.infratec.enums.TipoPessoa;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;
import java.util.Optional;

public class TipoPessoaConverter implements AttributeConverter<TipoPessoa, Character> {

    @Override
    public Character convertToDatabaseColumn(final TipoPessoa tipoPessoa) {
        if (Objects.isNull(tipoPessoa)) {
            return null;
        }
        return tipoPessoa.getId();
    }

    @Override
    public TipoPessoa convertToEntityAttribute(final Character code) {
        return Optional.ofNullable(code).map(TipoPessoa::of).orElse(null);
    }
}
