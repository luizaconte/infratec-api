package br.com.infratec.model.sales;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tb_ncm")
public class TbNcm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 15)
    @Column(name = "codigo", length = 15)
    private String codigo;

    @Size(max = 8)
    @Column(name = "numero", length = 8)
    private String numero;

    @Size(max = 4000)
    @Column(name = "descricao", length = 4000)
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Size(max = 100)
    @Column(name = "ato_legal",length = 100)
    private String atoLegal;

    @Size(max = 10)
    @Column(name = "ato_numero",length = 10)
    private String atoNumero;

    @Size(max = 4)
    @Column(name = "ato_ano",length = 4)
    private String atoAno;

}
