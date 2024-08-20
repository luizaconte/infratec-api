package br.com.infratec.model;

import br.com.infratec.enums.Prioridade;
import br.com.infratec.model.converters.PrioridadeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)

@Entity
@Table(name = "tb_chamado")
public class TbChamado extends Identifiable {

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
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario_criacao", nullable = false)
    private TbUsuario usuarioCriacao;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario_responsavel", nullable = false)
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
