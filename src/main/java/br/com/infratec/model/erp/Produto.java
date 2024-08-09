package br.com.infratec.model.erp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "PRODUTO")
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PRO_CODIGO")
    private Long proCodigo;

    @Size(max = 200)
    @Column(name = "PRO_NOME")
    private String proNome;

    @Size(max = 20)
    @Column(name = "PRO_BARRA")
    private String proBarra;

    @Size(max = 10)
    @Column(name = "PRO_NCM")
    private String proNcm;

    @Column(name = "PRO_PRECO_SITE")
    private BigDecimal proPrecoSite;

    @Column(name = "CFOP_CODIGO")
    private Integer cfopCodigo;

    @Column(name = "PRO_PESO")
    private BigDecimal proPeso;

    @Column(name = "PRO_METATITLE")
    private String proMetaTitle;

    @Column(name = "PRO_LARGURA")
    private BigDecimal proLargura;

    @Column(name = "PRO_ALTURA")
    private BigDecimal proAltura;

    @Column(name = "PRO_PROFUNDIDADE")
    private BigDecimal proProfundidade;

}
