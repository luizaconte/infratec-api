package br.com.infratec.model.sales;

import br.com.infratec.enums.StatusProcessamento;
import br.com.infratec.model.converters.StatusProcessamentoConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter

@NamedEntityGraphs({
        @NamedEntityGraph(
                name = TbPedido.FETCH_ALL,
                attributeNodes = {
                        @NamedAttributeNode(value = "itens")
                }
        )
})
@Entity
@Table(name = "tb_pedido")
public class TbPedido {

    public static final String FETCH_ALL = "TbPedido.fetchAll";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_order", nullable = false)
    private Long idOrder;

    @Convert(converter = StatusProcessamentoConverter.class)
    @Column(name = "status", nullable = false)
    private StatusProcessamento status;

    @Column(name = "lote", nullable = false)
    private Long lote;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @Column(name = "valor_pago", nullable = false)
    private Double valorPago;

    @Column(name = "id_shipment")
    private Long idShipment;

    @Column(name = "is_full")
    private Boolean isFull;

    @Column(name = "nome_cliente", length = 150)
    private String nomeCliente;

    @Column(name = "uf_cliente", length = 2)
    private String ufCliente;

    @Column(name = "documento_cliente", length = 14)
    private String documentoCliente;

    @Column(name = "xml_nfe")
    private String xmlNfe;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<TbPedidoItem> itens;

    @Column(name = "data_inclusao", nullable = false)
    private LocalDateTime dataInclusao;

    @Column(name = "login_inclusao", nullable = false, length = 30)
    private String loginInclusao;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "login_alteracao", length = 30)
    private String loginAlteracao;

}

