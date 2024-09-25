package br.com.infratec.controller.mapper;

import br.com.infratec.dto.UsuarioDTO;
import br.com.infratec.model.TbUsuario;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {DepartamentoDTOMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class UsuarioDTOMapper extends AbstractDTOMapper<UsuarioDTO, TbUsuario> {

    @Mapping(source = "dto.departamento.id", target = "idDepartamento")
    @Mapping(ignore = true, target = "departamento")
    public abstract TbUsuario toEntity(final Integer id, final UsuarioDTO dto);
}
