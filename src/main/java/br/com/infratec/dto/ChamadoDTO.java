package br.com.infratec.dto;

import br.com.infratec.enums.Prioridade;
import lombok.Builder;
import lombok.Data;
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
    String descricao;
    String telefone;
    Prioridade prioridade;
    UsuarioDTO usuarioCriacao;
    UsuarioDTO usuarioResponsavel;
    Set<ComentarioDTO> comentarios;
    Instant dataInclusao;
    Instant dataAlteracao;

}
