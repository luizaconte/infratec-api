package br.com.infratec.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.Instant;

@Value
@Builder
@Jacksonized
public class ComentarioDTO implements Serializable {

    Long id;
    String descricao;
    Instant dataInclusao;
    Instant dataAlteracao;
}
