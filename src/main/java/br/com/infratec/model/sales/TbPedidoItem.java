package br.com.infratec.model.sales;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor

@Entity
@Table(name = "tb_pedido_item")
public class TbPedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_ncm")
    private Integer idNcm;

    @JoinColumn(name = "id_ncm", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TbNcm ncm;

    @Column(name = "meli_item_id", length = 20)
    private String meliItemId;

    @Column(name = "meli_item_title", length = 200)
    private String meliItemTitle;

    @Column(name = "sku", length = 15)
    private String sku;

    @Column(name = "valor_unitario")
    private Double valorUnitario;

    @Column(name = "quantidade")
    private Double quantidade;

    @Column(name = "desconto")
    private Double desconto;

    @Column(name = "valor_final")
    private Double valorFinal;

    @Column(name = "id_pedido")
    private Long idPedido;

    @JoinColumn(name = "id_pedido", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TbPedido pedido;

    @Column(name = "data_inclusao", nullable = false)
    private LocalDateTime dataInclusao;

    @Column(name = "login_inclusao", length = 30, nullable = false)
    private String loginInclusao;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "login_alteracao", length = 30)
    private String loginAlteracao;
}
