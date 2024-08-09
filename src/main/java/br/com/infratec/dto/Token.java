package br.com.infratec.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@Jacksonized
public class Token {

    String accessToken;
    String refreshToken;
    String tokenType;
    Integer expiresIn;
    String scope;
    UsuarioDTO user;
}
