package br.com.infratec.model;

import br.com.infratec.enums.Prioridade;
import br.com.infratec.model.converters.PrioridadeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter

@Entity
@Table(name = "tb_chamado")
public class TbChamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Size(max = 500)
    @Column(name = "descricao", nullable = false, length = 500)
    private String descricao;

    @Size(max = 11)
    @Column(name = "telefone", nullable = false, length = 11)
    private String telefone;

    @Convert(converter = PrioridadeConverter.class)
    @Column(name = "prioridade", nullable = false)
    private Prioridade prioridade;

    @NotNull
    @Column(name = "id_usuario_criacao", nullable = false)
    private Integer idUsuarioCriacao;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario_criacao", nullable = false, insertable = false, updatable = false)
    private TbUsuario usuarioCriacao;

    @NotNull
    @Column(name = "id_usuario_responsavel", nullable = false)
    private Integer idUsuarioResponsavel;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario_responsavel", nullable = false, insertable = false, updatable = false)
    private TbUsuario usuarioResponsavel;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chamado", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<TbComentario> comentarios;

    @NotNull
    @Column(name = "data_inclusao", nullable = false, updatable = false)
    private Instant dataInclusao;

    @Column(name = "data_alteracao")
    private Instant dataAlteracao;
}
