package br.com.infratec.controller.mapper;

import br.com.infratec.dto.UsuarioDTO;
import br.com.infratec.model.TbUsuario;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class UsuarioDTOMapper extends AbstractDTOMapper<UsuarioDTO, TbUsuario> {
}
