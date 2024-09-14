package br.com.infratec.controller.mapper;

import br.com.infratec.dto.ChamadoDTO;
import br.com.infratec.model.TbChamado;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ComentarioDTOMapper.class, UsuarioDTOMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class ChamadoDTOMapper extends AbstractDTOMapper<ChamadoDTO, TbChamado> {
}
