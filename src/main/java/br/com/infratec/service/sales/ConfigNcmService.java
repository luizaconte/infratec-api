package br.com.infratec.service.sales;

import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.enums.TipoPessoa;
import br.com.infratec.model.sales.TbConfigNcm;
import br.com.infratec.repository.sales.ConfigNcmRepository;
import br.com.infratec.util.JwtService;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ConfigNcmService {
    private final ConfigNcmRepository configNcmRepository;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbConfigNcm> configNcmCustomRsqlVisitor = new CustomRsqlVisitor<>();

    @Autowired
    public ConfigNcmService(ConfigNcmRepository configNcmRepository, RSQLParser rsqlParser) {
        this.configNcmRepository = configNcmRepository;
        this.rsqlParser = rsqlParser;
    }

    public Optional<TbConfigNcm> findById(final Integer id) {
        return configNcmRepository.findById(id);
    }

    public Page<TbConfigNcm> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return configNcmRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbConfigNcm> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(configNcmCustomRsqlVisitor);
        return configNcmRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }


    public void atualizar(TbConfigNcm tbConfigNcm) {
        tbConfigNcm.setDataAlteracao(Instant.now());
        tbConfigNcm.setLoginAlteracao(JwtService.getLogin());
        configNcmRepository.save(tbConfigNcm);
    }

    public void salvar(TbConfigNcm tbConfigNcm) {
        tbConfigNcm.setDataInclusao(Instant.now());
        tbConfigNcm.setLoginInclusao(JwtService.getLogin());
        configNcmRepository.save(tbConfigNcm);
    }

    public Optional<TbConfigNcm> buscarNcm(Integer idNcm, TipoPessoa tipoPessoa, String uf) {
        return configNcmRepository.findByIdNcmAndTipoPessoaAndUf(idNcm, tipoPessoa, uf);
    }
}
