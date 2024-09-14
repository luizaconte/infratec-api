package br.com.infratec.service;

import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.TbDepartamento;
import br.com.infratec.repository.DepartamentoRepository;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepartamentoService {
    private final DepartamentoRepository departamentoRepository;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbDepartamento> departamentoCustomRsqlVisitor = new CustomRsqlVisitor<>();

    @Autowired
    public DepartamentoService(DepartamentoRepository departamentoRepository, RSQLParser rsqlParser) {
        this.departamentoRepository = departamentoRepository;
        this.rsqlParser = rsqlParser;
    }

    public Optional<TbDepartamento> findById(final Long id) {
        return departamentoRepository.findById(id);
    }

    public Page<TbDepartamento> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return departamentoRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbDepartamento> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(departamentoCustomRsqlVisitor);
        return departamentoRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }

    public void atualizar(TbDepartamento tbDepartamento) {
        departamentoRepository.save(tbDepartamento);
    }

    public void salvar(TbDepartamento tbDepartamento) {
        Optional<TbDepartamento> optionalTbDepartamento = departamentoRepository.findByNomeContainsIgnoreCase(tbDepartamento.getNome());
        if (optionalTbDepartamento.isEmpty()) {
            departamentoRepository.save(tbDepartamento);
        } else {
            throw new ZCException("O departamento fornecido já existe. Verifique!");
        }
    }
}
