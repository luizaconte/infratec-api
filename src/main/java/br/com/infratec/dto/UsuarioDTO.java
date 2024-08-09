package br.com.infratec.dto;

import br.com.infratec.model.TbUsuario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link TbUsuario}
 */
@Value
@Builder
@Jacksonized
public class UsuarioDTO implements Serializable {

    Integer id;

    @NotNull
    @Size(max = 100)
    String nome;

    @NotNull
    @Size(max = 30)
    String login;

    @Size(max = 100)
    String senha;

    @NotNull
    @Size(max = 100)
    String email;

    Instant dataInclusao;

    @Size(max = 30)
    String loginInclusao;

    Instant dataAlteracao;

    @Size(max = 30)
    String loginAlteracao;

    @Size(max = 128)
    String accessKey;

}
