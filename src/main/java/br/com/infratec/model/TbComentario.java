package br.com.infratec.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.Instant;


@Getter
@Setter

@Entity
@Table(name = "tb_comentario")
public class TbComentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 1000)
    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "id_chamado")
    private Long idChamado;

    @JoinColumn(name = "id_chamado", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TbChamado chamado;

    @NotNull
    @Column(name = "data_inclusao", nullable = false, updatable = false)
    private Instant dataInclusao;

    @Column(name = "data_alteracao")
    private Instant dataAlteracao;
}
