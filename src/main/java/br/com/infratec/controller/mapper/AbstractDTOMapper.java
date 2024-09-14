package br.com.infratec.controller.mapper;

import org.mapstruct.Mapping;

public abstract class AbstractDTOMapper<D, E> {

    public abstract D toDto(final E entity);

    public E toEntity(final D dto) {
        return toEntity((Integer) null, dto);
    }

    @Mapping(source = "id", target = "id")
    public abstract E toEntity(final Integer id, final D dto);

    @Mapping(source = "id", target = "id")
    public abstract E toEntity(final Long id, final D dto);

}
