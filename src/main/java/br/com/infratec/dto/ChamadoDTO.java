package br.com.infratec.dto;

import br.com.infratec.enums.Prioridade;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class ChamadoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String telefone;
    private Prioridade prioridade;
    private UsuarioDTO usuarioCriacao;
    private UsuarioDTO usuarioResponsavel;
    private Set<ComentarioDTO> comentarios;
    private Instant dataInclusao;
    private Instant dataAlteracao;

}
