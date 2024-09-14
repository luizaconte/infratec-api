package br.com.infratec.service;

import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.TbChamado;
import br.com.infratec.repository.ChamadoRepository;
import br.com.infratec.util.JwtService;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChamadoService {
    private final ChamadoRepository chamadoRepository;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbChamado> chamadoCustomRsqlVisitor = new CustomRsqlVisitor<>();

    @Autowired
    public ChamadoService(ChamadoRepository chamadoRepository, RSQLParser rsqlParser) {
        this.chamadoRepository = chamadoRepository;
        this.rsqlParser = rsqlParser;
    }

    public Optional<TbChamado> findById(final Long id) {
        return chamadoRepository.findById(id);
    }

    public Page<TbChamado> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return chamadoRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbChamado> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(chamadoCustomRsqlVisitor);
        return chamadoRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }

    public void atualizar(TbChamado tbChamado) {
        tbChamado.setDataAlteracao(Instant.now());
        chamadoRepository.save(tbChamado);
    }

    public void salvar(TbChamado tbChamado) {
        chamadoRepository.save(tbChamado);
    }
}
