package br.com.infratec.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ComentarioDTO {

    private Long id;
    private String descricao;
    private Instant dataInclusao;
    private Instant dataAlteracao;
}
