package br.com.infratec.service.sales;


import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.model.sales.TbLogPedidos;
import br.com.infratec.repository.sales.LogPedidosRepository;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LogPedidosService {

    private final LogPedidosRepository logPedidosRepository;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbLogPedidos> tbLogPedidosCustomRsqlVisitor = new CustomRsqlVisitor<>();


    @Autowired
    public LogPedidosService(LogPedidosRepository logPedidosRepository, RSQLParser rsqlParser) {
        this.logPedidosRepository = logPedidosRepository;
        this.rsqlParser = rsqlParser;
    }

    public void salvarLog(Long idPedido, String descricao) {
        TbLogPedidos tbLogPedidos = TbLogPedidos.builder()
                .idPedido(idPedido)
                .descricao(descricao)
                .dataInclusao(LocalDateTime.now())
                .build();
        logPedidosRepository.save(tbLogPedidos);
    }

    public Optional<TbLogPedidos> findById(final Long id) {
        return logPedidosRepository.findById(id);
    }

    public Page<TbLogPedidos> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return logPedidosRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbLogPedidos> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(tbLogPedidosCustomRsqlVisitor);
        return logPedidosRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }
}
