package br.com.infratec.model.sales;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = TbLogPedidos.FETCH_ALL,
                attributeNodes = {
                        @NamedAttributeNode(value = "pedido", subgraph = "Pedido.itens")
                },

                subgraphs = {
                        @NamedSubgraph(
                                name = "Pedido.itens",
                                attributeNodes = {
                                        @NamedAttributeNode(value = "itens")
                                }
                        ),
                }
        )
})
@Entity
@Table(name = "tb_log_pedidos")
public class TbLogPedidos extends Identifiable {
    public static final String FETCH_ALL = "TbLogPedidos.fetchAll";

    @Column(name = "id_pedido")
    private Long idPedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido", referencedColumnName = "id", insertable = false, updatable = false)
    private TbPedido pedido;

    @Column(name = "descricao", length = 1024)
    private String descricao;

    @Column(name = "data_inclusao", nullable = false)
    private LocalDateTime dataInclusao;
}

