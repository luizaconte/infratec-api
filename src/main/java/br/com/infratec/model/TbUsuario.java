package br.com.infratec.model;

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
