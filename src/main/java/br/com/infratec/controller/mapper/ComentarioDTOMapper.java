package br.com.infratec.controller.mapper;

import br.com.infratec.dto.ComentarioDTO;
import br.com.infratec.model.TbComentario;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class ComentarioDTOMapper extends AbstractDTOMapper<ComentarioDTO, TbComentario> {

    @Override
    public TbComentario toEntity(ComentarioDTO dto) {
        return toEntity(dto.getId(), dto);
    }

}
