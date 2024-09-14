package br.com.infratec.controller.mapper;

import br.com.infratec.dto.DepartamentoDTO;
import br.com.infratec.model.TbDepartamento;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class DepartamentoDTOMapper extends AbstractDTOMapper<DepartamentoDTO, TbDepartamento> {
}
