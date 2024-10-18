package br.com.infratec.dto;

import br.com.infratec.enums.Prioridade;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class ChamadoDTO implements Serializable {

    Long id;
    String nome;
    @NotNull
    String descricao;
    @NotNull
    String telefone;
    @NotNull
    Prioridade prioridade;
    UsuarioDTO usuarioCriacao;
    @NotNull
    UsuarioDTO usuarioResponsavel;
    Set<ComentarioDTO> comentarios;
    Instant dataInclusao;
    Instant dataAlteracao;

}
