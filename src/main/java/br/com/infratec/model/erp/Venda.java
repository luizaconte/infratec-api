package br.com.infratec.model.erp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "VENDA")
public class Venda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VD_CODIGO", nullable = false)
    private Long vdCodigo;

    @Column(name = "VD_PEDIDOEXTERNO", length = 200)
    private String vdPedidoExterno;
}
