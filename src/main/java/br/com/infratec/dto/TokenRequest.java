package br.com.infratec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@Jacksonized
public class TokenRequest {

    @JsonProperty("grant_type")
    String grantType;

    @JsonProperty("client_id")
    String clientId;

    @JsonProperty("client_secret")
    String clientSecret;

    String code;

    @JsonProperty("redirect_uri")
    String redirectUri;

    @JsonProperty("refresh_token")
    String refreshToken;
}
