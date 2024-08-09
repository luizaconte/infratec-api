package br.com.infratec.service.sales;

import br.com.infratec.config.Constants;
import br.com.infratec.controller.mapper.PedidoDTOMapper;
import br.com.infratec.dto.*;
import br.com.infratec.dto.meli.*;
import br.com.infratec.enums.EstadosBrasil;
import br.com.infratec.enums.StatusProcessamento;
import br.com.infratec.enums.TipoPessoa;
import br.com.infratec.enums.meli.TipoImpressaoEtiqueta;
import br.com.infratec.enums.meli.TipoInfo;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.erp.Produto;
import br.com.infratec.model.sales.TbConfigNcm;
import br.com.infratec.model.sales.TbNcm;
import br.com.infratec.model.sales.TbPedido;
import br.com.infratec.model.sales.TbPedidoItem;
import br.com.infratec.repository.sales.PedidoRepository;
import br.com.infratec.service.erp.ProdutoService;
import br.com.infratec.service.erp.VendaService;
import br.com.infratec.util.JwtService;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PedidoService {

    private final MeliService meliService;
    private final NfeService nfeService;
    private final ConfiguracaoService configuracaoService;
    private final LogPedidosService logPedidosService;
    private final ConfigNcmService configNcmService;
    private final NcmService ncmService;
    private final ProdutoService produtoService;
    private final VendaService vendaService;

    private final PedidoRepository pedidoRepository;

    private final PedidoDTOMapper mapper;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbPedido> pedidoCustomRsqlVisitor = new CustomRsqlVisitor<>();


    @Autowired
    public PedidoService(MeliService meliService,
                         PedidoRepository pedidoRepository,
                         NfeService nfeService,
                         ConfiguracaoService configuracaoService,
                         LogPedidosService logPedidosService,
                         ConfigNcmService configNcmService, NcmService ncmService, ProdutoService produtoService, VendaService vendaService,
                         PedidoDTOMapper mapper,
                         RSQLParser rsqlParser) {
        this.meliService = meliService;
        this.pedidoRepository = pedidoRepository;
        this.nfeService = nfeService;
        this.configuracaoService = configuracaoService;
        this.logPedidosService = logPedidosService;
        this.configNcmService = configNcmService;
        this.ncmService = ncmService;
        this.produtoService = produtoService;
        this.vendaService = vendaService;
        this.mapper = mapper;
        this.rsqlParser = rsqlParser;
    }

    public PageResult<Order> findAllPedidosMeli(PageRequestDTO pageRequestDTO) {
        PageResult<Order> result = meliService.search(pageRequestDTO);
        result.getResults().forEach(order -> {
            List<TbPedido> tbPedidoList = pedidoRepository.findByIdOrder(order.getId());
            order.setStatusProcessamento(!tbPedidoList.isEmpty() ? tbPedidoList.getFirst().getStatus() : StatusProcessamento.NAO_PROCESSADO);
        });
        return result;
    }

    public Order findPedidoByIdMeli(Long id) {
        return meliService.getPedido(id);
    }

    public PedidoDTO findPedidoByIdProcessados(Long id) {
        Optional<TbPedido> tbPedido = pedidoRepository.findById(id);
        if (tbPedido.isPresent()) {
            return mapper.toDto(tbPedido.get());
        } else {
            throw new ZCException("Pedido não encontrado.");
        }
    }

    public Billing findDadosFaturamentoById(Long id) {
        return meliService.getDadosFaturamentoPedido(id);
    }

    public ResponseProcessamento processar(Long lote, PageRequestDTO pageRequestDTO) {
        pageRequestDTO.setPageSize(50);
        pageRequestDTO.setPageIndex(0);
        PageResult<Order> orderPageResult = findAllPedidosMeli(pageRequestDTO);

        int qtdPages = orderPageResult.getPaging().getTotal() / pageRequestDTO.getPageSize() + 1;
        int pagAtual = 1;
        if (!orderPageResult.getResults().isEmpty()) {
            List<Order> orderList = orderPageResult.getResults();
            while (pagAtual <= qtdPages) {
                pageRequestDTO.setPageIndex(pagAtual);
                PageResult<Order> allPedidosMeli = findAllPedidosMeli(pageRequestDTO);
                orderList.addAll(allPedidosMeli.getResults());
                pagAtual = pagAtual + 1;
            }
            return salvarPedidos(orderList, lote);
        }
        throw new ZCException("A busca de pedidos não retornou nenhum resultado!");
    }

    private ResponseProcessamento salvarPedidos(List<Order> orderList, Long lote) {
        List<TbPedido> tbPedidoList = new ArrayList<>();
        List<Order> ordersFull = new ArrayList<>();
        orderList.stream().filter(order -> order.getStatusProcessamento().equals(StatusProcessamento.NAO_PROCESSADO)).forEach(order -> {
            Boolean isFull = Boolean.FALSE;
            Optional<Shipments> shipments = getShipments(order.getId());
            if (shipments.isPresent() && shipments.get().getLogisticType().equalsIgnoreCase("fulfillment")) {
                ordersFull.add(order);
                isFull = Boolean.TRUE;
            }
            TbPedido tbPedido = new TbPedido();
            tbPedido.setIdOrder(order.getId());
            tbPedido.setStatus(StatusProcessamento.PROCESSADO);
            tbPedido.setLote(lote);
            tbPedido.setDataCriacao(order.getDateCreated().toLocalDateTime());
            tbPedido.setValorTotal(order.getTotalAmount());
            tbPedido.setValorPago(order.getPaidAmount());
            tbPedido.setIdShipment(shipments.map(Shipments::getId).orElse(null));
            tbPedido.setIsFull(isFull);
            tbPedido.setNomeCliente(shipments.map(Shipments::getReceiverName).orElse(null));
            tbPedido.setDataInclusao(LocalDateTime.now());
            tbPedido.setLoginInclusao(JwtService.getLogin());
            tbPedidoList.add(tbPedido);
        });
        List<TbPedido> pedidosSaved = pedidoRepository.saveAllAndFlush(tbPedidoList);
        InfoProcessamento infoProcessamento = InfoProcessamento.builder()
                .total(pedidosSaved.stream().mapToDouble(TbPedido::getValorTotal).sum())
                .totalPago(pedidosSaved.stream().mapToDouble(TbPedido::getValorPago).sum())
                .qtdPedidos(pedidosSaved.size())
                .totalPagoFull(ordersFull.stream().mapToDouble(Order::getPaidAmount).sum())
                .totalFull(ordersFull.stream().mapToDouble(Order::getTotalAmount).sum())
                .qtdPedidosFull(ordersFull.size())
                .lote(lote)
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        return ResponseProcessamento.builder()
                .pedidos(pedidosSaved.stream().map(mapper::toDto).collect(Collectors.toList()))
                .infoProcessamento(infoProcessamento)
                .build();
    }

    private Optional<Shipments> getShipments(long orderId) {
        try {
            return Optional.ofNullable(meliService.getShipments(orderId)); // TODO- mapear log erros
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    public void gerarEnviarDanfe(List<PedidoDTO> pedidos) {
        pedidos.stream().filter(pedidoDTO -> pedidoDTO.getIsFull().equals(Boolean.FALSE)).forEach(pedidoDTO -> {
            String xml = null;
            try {
                Order order = findPedidoByIdMeli(pedidoDTO.getIdOrder());
                Billing billingInfo = findDadosFaturamentoById(pedidoDTO.getIdOrder());
                Shipment shipment = meliService.consultarEnvio(order.getShipping().getId());
                if (podeEnviar(shipment)) {
                    List<Product> productList = buscarNcm(order);
                    xml = nfeService.montarXML(order, billingInfo, shipment, productList, configuracaoService.buscarNumNfe());
                    InvoiceResponse invoiceResponse = meliService.enviarNfe(order.getShipping().getId(), xml);
                    configuracaoService.atualizarNumNfe();
                    atualizarPedido(mapper.toEntity(pedidoDTO.getId(), pedidoDTO), StatusProcessamento.NF_ENVIADA, billingInfo.getBillingInfo().getInfo(TipoInfo.STATE_NAME), billingInfo.getBillingInfo().getInfo(TipoInfo.FIRST_NAME) + " " + billingInfo.getBillingInfo().getInfo(TipoInfo.LAST_NAME));
                    logPedidosService.salvarLog(pedidoDTO.getId(), "Nota Fiscal enviada. Chave Fiscal: " + invoiceResponse.getFiscalKey());
                } else {
                    logPedidosService.salvarLog(pedidoDTO.getId(), "O pedido ainda não permite o envio da NFe.");
                    atualizarPedidoErro(mapper.toEntity(pedidoDTO.getId(), pedidoDTO), StatusProcessamento.EM_PROCESSAMENTO, null);
                }
            } catch (Exception e) {
                String desc = Objects.nonNull(e.getMessage()) ? e.getMessage() : e.getCause().toString();
                atualizarPedidoErro(mapper.toEntity(pedidoDTO.getId(), pedidoDTO), StatusProcessamento.NF_ERRO, xml);
                logPedidosService.salvarLog(pedidoDTO.getId(), desc);
            }
        });
    }

    public Boolean gerarEnviarDanfePorId(Long idPedido) {
        Optional<TbPedido> pedido = pedidoRepository.findById(idPedido);
        if (pedido.isPresent()) {
            Order order = findPedidoByIdMeli(pedido.get().getIdOrder());
            Shipment shipment = meliService.consultarEnvio(order.getShipping().getId());
            if (podeEnviar(shipment)) {
                String xml = null;
                try {
                    Billing billingInfo = findDadosFaturamentoById(pedido.get().getIdOrder());
                    List<Product> productList = buscarNcm(order);
                    xml = nfeService.montarXML(order, billingInfo, shipment, productList, configuracaoService.buscarNumNfe());
                    InvoiceResponse invoiceResponse = meliService.enviarNfe(order.getShipping().getId(), xml);
                    configuracaoService.atualizarNumNfe();
                    atualizarPedido(pedido.get(), StatusProcessamento.NF_ENVIADA, billingInfo.getBillingInfo().getInfo(TipoInfo.STATE_NAME), billingInfo.getBillingInfo().getInfo(TipoInfo.FIRST_NAME) + " " + billingInfo.getBillingInfo().getInfo(TipoInfo.LAST_NAME));
                    logPedidosService.salvarLog(pedido.get().getId(), "Nota Fiscal enviada. Chave Fiscal: " + invoiceResponse.getFiscalKey());
                    return Boolean.TRUE;
                } catch (Exception e) {
                    String desc = Objects.nonNull(e.getMessage()) ? e.getMessage() : e.getCause().toString();
                    atualizarPedidoErro(pedido.get(), StatusProcessamento.NF_ERRO, xml);
                    logPedidosService.salvarLog(pedido.get().getId(), desc);
                    throw new ZCException(e);
                }
            } else {
                String msg = "O pedido ainda não permite o envio da NFe.";
                logPedidosService.salvarLog(pedido.get().getId(), "O pedido ainda não permite o envio da NFe.");
                atualizarPedidoErro(pedido.get(), StatusProcessamento.EM_PROCESSAMENTO, null);
                throw new ZCException(msg);
            }
        } else {
            throw new ZCException("Pedido não encontrado!");
        }
    }

    public void gerarEnviarDanfeAll() {
        List<StatusProcessamento> statusProcessamentos = new ArrayList<>();
        statusProcessamentos.add(StatusProcessamento.NF_ERRO);
        statusProcessamentos.add(StatusProcessamento.EM_PROCESSAMENTO);
        List<TbPedido> pedidoList = pedidoRepository.findByStatus(statusProcessamentos);
        if (!pedidoList.isEmpty()) {
            List<PedidoDTO> listDto = pedidoList.stream().map(mapper::toDto).toList();
            gerarEnviarDanfe(listDto);
        }

    }

    private boolean podeEnviar(Shipment shipment) {
        return shipment.getStatus().equalsIgnoreCase("ready_to_ship") && shipment.getSubstatus().equalsIgnoreCase("invoice_pending");
    }

    private void atualizarPedido(TbPedido tbPedido, StatusProcessamento status, String ufCliente, String nomeCliente) {
        tbPedido.setStatus(status);
        tbPedido.setNomeCliente(nomeCliente);
        tbPedido.setUfCliente(EstadosBrasil.of(ufCliente));
        tbPedido.setDataAlteracao(LocalDateTime.now());
        pedidoRepository.save(tbPedido);
    }

    private void atualizarPedidoErro(TbPedido tbPedido, StatusProcessamento status, String xml) {
        tbPedido.setStatus(status);
        tbPedido.setXmlNfe(xml);
        tbPedido.setDataAlteracao(LocalDateTime.now());
        pedidoRepository.save(tbPedido);
    }

    private void salvarDescontoProcessado(TbPedido tbPedido) {
        tbPedido.setStatus(StatusProcessamento.DESCONTO_PROCESSADO);
        pedidoRepository.save(tbPedido);
    }

    private void alterarStatusPedidos(List<PedidoDTO> pedidoDTOS, StatusProcessamento status) {
        List<TbPedido> tbPedidoList = new ArrayList<>();
        pedidoDTOS.stream().map(p -> mapper.toEntity(p.getId(), p)).forEach(tbPedido -> {
            tbPedido.setStatus(status);
            tbPedido.setDataAlteracao(LocalDateTime.now());
            tbPedidoList.add(tbPedido);
        });
        pedidoRepository.saveAll(tbPedidoList);
    }

    private List<Product> buscarNcm(Order order) {
        List<Product> productList = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> {
            try {
                Product productBySku = null;
                if (Objects.nonNull(orderItem.getItem().getSellerSku()) && !orderItem.getItem().getSellerSku().equalsIgnoreCase("Não se aplica")) {
                    productBySku = recuperarDadosFiscaisPorSKU(orderItem.getItem().getSellerSku());
                }
                if (Objects.nonNull(productBySku)) {
                    productList.add(productBySku);
                } else {
                    productBySku = Product.builder()
                            .sku(orderItem.getItem().getSellerSku())
                            .title(orderItem.getItem().getTitle())
                            .build();
                    productList.add(productBySku);
                }
            } catch (Exception ignored) {
            }
        });
        return productList;
    }

    private Product recuperarDadosFiscaisPorItem(String item) {
        try {
            return meliService.recuperarDadosFiscaisPorItem(item);
        } catch (Exception ignored) {
        }
        return null;
    }

    private Product recuperarDadosFiscaisPorSKU(String sku) {
        try {
            return meliService.recuperarDadosFiscaisPorSKU(Integer.valueOf(sku));
        } catch (Exception ignored) {
        }
        return null;
    }

    public InputStreamResource gerarEtiquetas(List<PedidoDTO> pedidos, TipoImpressaoEtiqueta tipoImpressaoEtiqueta) {
        try {
            List<PedidoDTO> pedidosValidos = pedidos.stream().filter(p -> p.getIsFull().equals(Boolean.FALSE) && (p.getStatus().equals(StatusProcessamento.NF_ENVIADA)
                    || p.getStatus().equals(StatusProcessamento.ETIQUETA_GERADA))).collect(Collectors.toList());
            List<Long> shipmentIds = pedidosValidos.stream().map(PedidoDTO::getIdShipment).collect(Collectors.toList());
            // enviar a cada 50 registros TODO
            InputStreamResource result = meliService.gerarEtiqueta(shipmentIds, tipoImpressaoEtiqueta);
            if (Objects.nonNull(result)) {
                alterarStatusPedidos(pedidosValidos, StatusProcessamento.ETIQUETA_GERADA);
                return result;
            }
        } catch (Exception e) {
            throw new ZCException(e.getMessage());
        }
        return null;
    }

    public Page<TbPedido> listarPedidosNfeEnviada(PageRequestDTO pageRequestDTO) {
        String query = (StringUtils.isBlank(pageRequestDTO.getQuery()) ? "" : pageRequestDTO.getQuery() + ";").concat("status==" + StatusProcessamento.NF_ENVIADA);
        pageRequestDTO.setQuery(query);
        return getAll(pageRequestDTO);
    }

    public Page<TbPedido> getAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return pedidoRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbPedido> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(pedidoCustomRsqlVisitor);
        return pedidoRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }

    public Long buscarValorLote() {
        Optional<TbPedido> pedido = pedidoRepository.findByLoteMax();
        if (pedido.isPresent()) {
            return pedido.get().getLote();
        } else {
            return 0L;
        }
    }

    public List<TbPedido> processarDescontos(ProcessarAtualizarRequestDTO requestDTO) {
        List<StatusProcessamento> statusProcessamentoList = new ArrayList<>();
        statusProcessamentoList.add(StatusProcessamento.NF_ENVIADA);
        statusProcessamentoList.add(StatusProcessamento.ETIQUETA_GERADA);

        List<TbPedido> pedidosDescontoProcessado = new ArrayList<>();

        List<TbPedido> pedidoList = null;
        if (Objects.nonNull(requestDTO.getLote())) {
            pedidoList = pedidoRepository.findByLoteAndStatus(requestDTO.getLote(), statusProcessamentoList);
        } else if (Objects.nonNull(requestDTO.getDataCriacao())) {
            LocalDateTime startDate = LocalDateTime.of(requestDTO.getDataCriacao(), LocalTime.MIN);
            LocalDateTime endDate = LocalDateTime.of(requestDTO.getDataCriacao(), LocalTime.MAX);
            pedidoList = pedidoRepository.findByDataCriacaoBetweenAndStatus(startDate, endDate, statusProcessamentoList);
        }

        if (!pedidoList.isEmpty()) {
            pedidoList.forEach(pedido -> {
                try {
                    Order order = findPedidoByIdMeli(pedido.getIdOrder());


                    if (!order.getOrderItems().isEmpty()) {
                        order.getOrderItems().forEach(orderItem -> {

                            Optional<Produto> produto = produtoService.findById(Long.valueOf(orderItem.getItem().getSellerSku()));

                            if (produto.isPresent()) {
                                TipoPessoa tipoPessoa = Objects.nonNull(pedido.getDocumentoCliente()) && pedido.getDocumentoCliente().length() == 11 ? TipoPessoa.FISICA : TipoPessoa.JURIDICA;
                                double subtotal = orderItem.getQuantity() * orderItem.getUnitPrice();

                                TbPedidoItem tbPedidoItem = TbPedidoItem.builder()
                                        .meliItemId(orderItem.getItem().getId())
                                        .meliItemTitle(orderItem.getItem().getTitle())
                                        .sku(produto.get().getProCodigo().toString())
                                        .valorUnitario(orderItem.getUnitPrice())
                                        .quantidade((double) orderItem.getQuantity())
                                        .valorFinal(subtotal)
                                        .idPedido(pedido.getId())
                                        .desconto(0D)
                                        .dataInclusao(LocalDateTime.now())
                                        .loginInclusao(JwtService.getLogin())
                                        .build();

                                Optional<TbNcm> tbNcm = ncmService.findByNumero(produto.get().getProNcm());
                                if (tbNcm.isPresent()) {
                                    tbPedidoItem.setIdNcm(tbNcm.get().getId());
                                    Optional<TbConfigNcm> configNcm = configNcmService.buscarNcm(tbNcm.get().getId(), tipoPessoa, pedido.getUfCliente());
                                    if (configNcm.isPresent()) {
                                        tbPedidoItem.setDesconto(configNcm.get().getValorDesconto());
                                        tbPedidoItem.setValorFinal(subtotal - (subtotal * configNcm.get().getValorDesconto() / 100));
                                    }
                                }
                                pedido.getItens().add(tbPedidoItem);
                            }

                        });
                        if (!pedido.getItens().isEmpty()) {
                            salvarDescontoProcessado(pedido);
                            pedidosDescontoProcessado.add(pedido);
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        }

        return pedidosDescontoProcessado;
    }

    public void aplicarDescontos(ProcessarAtualizarRequestDTO requestDTO) {
        List<TbPedido> pedidoList = null;
        if (Objects.nonNull(requestDTO.getLote())) {
            pedidoList = pedidoRepository.findByLoteAndStatus(requestDTO.getLote(), List.of(StatusProcessamento.DESCONTO_PROCESSADO));
        } else if (Objects.nonNull(requestDTO.getDataCriacao())) {
            LocalDateTime startDate = LocalDateTime.of(requestDTO.getDataCriacao(), LocalTime.MIN);
            LocalDateTime endDate = LocalDateTime.of(requestDTO.getDataCriacao(), LocalTime.MAX);
            pedidoList = pedidoRepository.findByDataCriacaoBetweenAndStatus(startDate, endDate, List.of(StatusProcessamento.DESCONTO_PROCESSADO));
        }

        if (!pedidoList.isEmpty()) {
            pedidoList
                    .forEach(pedido -> {
                        pedido.getItens()
                                .forEach(pedidoItem -> vendaService.aplicarDescontos(pedido.getIdOrder().toString(), pedidoItem.getSku(), BigDecimal.valueOf(pedidoItem.getValorFinal())));

                        pedido.setStatus(StatusProcessamento.PROCESSADO);
                        pedidoRepository.save(pedido);
                    });
        }
    }

    @Scheduled(cron = "${application.cron-processamento}", zone = "America/Sao_Paulo")
    public void processarAutomaticamente() {
        if (Constants.APP_CONFIG.isProcessarAutomatico()) {
            System.out.println("Processando automaticamente...");
        }
    }
}
