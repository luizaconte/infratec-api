package br.com.infratec.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class UsuarioDTO implements Serializable {

    private Integer id;
    private String nome;
    private String cargo;
    private String login;
    private String email;
    private Instant dataInclusao;
    private String loginInclusao;
    private Instant dataAlteracao;
    private String loginAlteracao;
    private Long departamentoId;
    private String departamentoNome;
}
