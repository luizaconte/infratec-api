package br.com.infratec.service.sales;

import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.model.sales.TbNcm;
import br.com.infratec.repository.sales.NcmRepository;
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
public class NcmService {
    private final NcmRepository ncmRepository;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbNcm> ncmCustomRsqlVisitor = new CustomRsqlVisitor<>();

    @Autowired
    public NcmService(NcmRepository ncmRepository, RSQLParser rsqlParser) {
        this.ncmRepository = ncmRepository;
        this.rsqlParser = rsqlParser;
    }

    public Optional<TbNcm> findById(final Integer id) {
        return ncmRepository.findById(id);
    }

    public Optional<TbNcm> findByNumero(final String numero) {
        return ncmRepository.findByNumero(numero);
    }

    public Page<TbNcm> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return ncmRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbNcm> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(ncmCustomRsqlVisitor);
        return ncmRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }
}
