package br.com.infratec.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ComentarioDTO {

    private Long id;
    private String descricao;
    private Long chamadoId;
    private Instant dataInclusao;
    private Instant dataAlteracao;
}
