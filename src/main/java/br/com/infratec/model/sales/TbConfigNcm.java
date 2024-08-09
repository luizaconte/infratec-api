package br.com.infratec.model.sales;

import br.com.infratec.enums.TipoPessoa;
import br.com.infratec.model.converters.TipoPessoaConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_config_ncm")
public class TbConfigNcm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_ncm")
    private Integer idNcm;

    @JoinColumn(name = "id_ncm", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private TbNcm ncm;

    @Size(max = 2)
    @Column(name = "uf", length = 2)
    private String uf;

    @Convert(converter = TipoPessoaConverter.class)
    @Column(name = "tipo_pessoa")
    private TipoPessoa tipoPessoa;

    @Column(name = "valor_desconto", nullable = false)
    private Double valorDesconto;

    @NotNull
    @Column(name = "data_inclusao", nullable = false, updatable = false)
    private Instant dataInclusao;

    @Size(max = 30)
    @NotNull
    @Column(name = "login_inclusao", nullable = false, length = 30, updatable = false)
    private String loginInclusao;

    @Column(name = "data_alteracao")
    private Instant dataAlteracao;

    @Size(max = 30)
    @Column(name = "login_alteracao", length = 30)
    private String loginAlteracao;


}
