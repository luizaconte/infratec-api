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
@Table(name = "tb_usuario_chave")
public class TbUsuarioChave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private TbUsuario idUsuario;

    @Size(max = 128)
    @NotNull
    @Column(name = "chave_publica", nullable = false, length = 128)
    private String chavePublica;

    @Size(max = 128)
    @NotNull
    @Column(name = "chave_privada", nullable = false, length = 128)
    private String chavePrivada;

    @Size(max = 30)
    @NotNull
    @Column(name = "login", nullable = false, length = 30)
    private String login;

    @NotNull
    @Column(name = "data_inclusao", nullable = false)
    private Instant dataInclusao;

    @NotNull
    @Column(name = "validade", nullable = false)
    private Long validade;

}
