package br.com.infratec.model.erp;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "VENDA_ITEM")
public class VendaItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VI_CODIGO", nullable = false)
    private Long viCodigo;

    @Column(name = "VD_CODIGO", nullable = false)
    private Long vdCodigo;

    @Column(name = "PRO_CODIGO")
    private Integer proCodigo;

    @Column(name = "VI_PRO_VL_LIQ")
    private BigDecimal viProVlLiq;

    @Column(name = "VI_PRO_VL_UNIT")
    private BigDecimal viProVlUnit;

    @Column(name = "VI_PRO_VL_BRUTO")
    private BigDecimal viProVlBruto;

    @JoinColumn(name = "VD_CODIGO", referencedColumnName = "VD_CODIGO", insertable = false, updatable = false)
    @ManyToOne
    private Venda venda;

    @JoinColumn(name = "PRO_CODIGO", referencedColumnName = "PRO_CODIGO", insertable = false, updatable = false)
    @ManyToOne
    private Produto produto;
}
