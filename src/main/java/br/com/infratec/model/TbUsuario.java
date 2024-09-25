package br.com.infratec.model;

import br.com.infratec.enums.TipoUsuario;
import br.com.infratec.model.converters.TipoUsuarioConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_usuario")
public class TbUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Size(max = 50)
    @Column(name = "cargo", nullable = true, length = 50)
    private String cargo;

    @Size(max = 30)
    @NotNull
    @Column(name = "login", nullable = false, length = 30)
    private String login;

    @Size(max = 100)
    @NotNull
    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Convert(converter = TipoUsuarioConverter.class)
    @Column(name = "tipo", nullable = false)
    private TipoUsuario tipo;

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

    @NotNull
    @Column(name = "id_departamento", nullable = false)
    private Long idDepartamento;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_departamento", nullable = false, insertable = false, updatable = false)
    private TbDepartamento departamento;

}
