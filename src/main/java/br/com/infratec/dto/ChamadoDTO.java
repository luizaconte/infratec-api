package br.com.infratec.dto;

import br.com.infratec.enums.Prioridade;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class ChamadoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String telefone;
    private Prioridade prioridade;
    private Long usuarioCriacaoId;
    private Long usuarioResponsavelId;
    private Set<ComentarioDTO> comentarios;
    private Instant dataInclusao;
    private Instant dataAlteracao;

}
