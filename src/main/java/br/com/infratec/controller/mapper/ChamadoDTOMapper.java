package br.com.infratec.controller.mapper;

import br.com.infratec.dto.ChamadoDTO;
import br.com.infratec.model.TbChamado;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ComentarioDTOMapper.class, UsuarioDTOMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class ChamadoDTOMapper extends AbstractDTOMapper<ChamadoDTO, TbChamado> {

    @Mapping(source = "dto.usuarioCriacao.id", target = "idUsuarioCriacao")
    @Mapping(ignore = true, target = "usuarioCriacao")
    @Mapping(source = "dto.usuarioResponsavel.id", target = "idUsuarioResponsavel")
    @Mapping(ignore = true, target = "usuarioResponsavel")
    public abstract TbChamado toEntity(final Integer id, final ChamadoDTO dto);
}
