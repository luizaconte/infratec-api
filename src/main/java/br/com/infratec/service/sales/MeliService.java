package br.com.infratec.service.sales;

import br.com.infratec.client.*;
import br.com.infratec.config.Constants;
import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.dto.meli.*;
import br.com.infratec.enums.meli.TipoImpressaoEtiqueta;
import br.com.infratec.exception.ZCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class MeliService {

    private final PedidosClient pedidosClient;

    private final DadosFiscaisClient dadosFiscaisClient;

    private final EtiquetasClient etiquetasClient;

    private final NfeClient nfeClient;

    @Autowired
    public MeliService(PedidosClient pedidosClient, DadosFiscaisClient dadosFiscaisClient,
                       EtiquetasClient etiquetasClient, NfeClient nfeClient) {
        this.pedidosClient = pedidosClient;
        this.dadosFiscaisClient = dadosFiscaisClient;
        this.etiquetasClient = etiquetasClient;
        this.nfeClient = nfeClient;
    }

    public PageResult<Order> search(PageRequestDTO pageRequestDTO) {
        String status = null;
        String id = null;
        String dataInicio = null;
        String dataFim = null;
        if (Objects.nonNull(pageRequestDTO.getQuery())) {
            String[] params = pageRequestDTO.getQuery().split(";");
            for (String p : params) {
                if (p.contains("id==")) {
                    id = p.substring(4);
                }
                if (p.contains("status==")) {
                    status = p.substring(8);
                }
                if (p.contains("dateCreated=bt=")) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String startDateTimeStr = p.substring(16, 35);
                    String endDateTimeStr = p.substring(36, 55);

                    LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, dateTimeFormatter);
                    LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr, dateTimeFormatter);

                    dataInicio = startDateTime.atOffset(ZoneOffset.of("-03:00")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                    dataFim = endDateTime.atOffset(ZoneOffset.of("-03:00")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                }
            }
        }
        return pedidosClient.search(Constants.APP_CONFIG.getSellerId(), pageRequestDTO.getPageSize(), pageRequestDTO.getPageIndex() * pageRequestDTO.getPageSize(), id, status, dataInicio, dataFim);
    }

    public Product recuperarDadosFiscaisPorSKU(Integer sku) {
        return dadosFiscaisClient.getInformacoesFiscaisPorSKU(sku);
    }

    public Product recuperarDadosFiscaisPorItem(String itemId) {
        return dadosFiscaisClient.getInformacoesFiscaisPorItem(itemId);
    }

    public Order getPedido(Long id) {
        return pedidosClient.get(id);
    }

    public Billing getDadosFaturamentoPedido(Long id) {
        return pedidosClient.getDadosFaturamento(id);
    }

    public Discounts getDescontosPedido(Long id) {
        return pedidosClient.getDescontos(id);
    }

    public Shipments getShipments(Long id) {
        return pedidosClient.getShipments(id);
    }

    public InputStreamResource gerarEtiqueta(List<Long> shipmentIds, TipoImpressaoEtiqueta tipoImpressaoEtiqueta) {
        ResponseEntity<byte[]> response = etiquetasClient.gerar(shipmentIds, tipoImpressaoEtiqueta.getTipo());
        if (response.getStatusCode().is2xxSuccessful()) {
            return new InputStreamResource(new ByteArrayInputStream(response.getBody()));
        }
        throw new ZCException("Erro ao gerar Etiqueta!");
    }

    public InvoiceResponse enviarNfe(Long shipmentId, String xml) {
        return nfeClient.enviarNfe(shipmentId, "MLB", xml).getBody();
    }

    public Shipment consultarEnvio(Long shipmentId) {
        return nfeClient.consultarEnvio(shipmentId).getBody();
    }
}
