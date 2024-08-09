package br.com.infratec.filter;

import br.com.infratec.config.Constants;
import br.com.infratec.dto.TokenResponse;
import br.com.infratec.service.sales.ConfiguracaoService;
import br.com.infratec.service.sales.MeliAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class AuthFilter implements ExchangeFilterFunction {

    private static TokenResponse token;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";
    private final ConfiguracaoService configuracaoService;
    private final MeliAuthService meliAuthService;

    @Autowired
    public AuthFilter(ConfiguracaoService configuracaoService, MeliAuthService meliAuthService) {
        this.configuracaoService = configuracaoService;
        this.meliAuthService = meliAuthService;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction nextFilter) {
        checkToken();
        ClientRequest modifiedRequest = ClientRequest
                .from(clientRequest)
                .header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, token.getAccessToken()))
                .build();

        return nextFilter.exchange(modifiedRequest);
    }

    private void checkToken() {

        if (StringUtils.isNotBlank(Constants.APP_CONFIG.getAccessToken())) {
            token = TokenResponse.builder()
                    .accessToken(Constants.APP_CONFIG.getAccessToken())
                    .refreshToken(Constants.APP_CONFIG.getRefreshToken())
                    .build();
        }

        if (token == null) {
            token = meliAuthService.getAccessToken();
            configuracaoService.atualizarToken(token);
        } else {
            if (LocalDateTime.now().isAfter(Constants.APP_CONFIG.getTokenExpiration())) {
                token = meliAuthService.refreshToken();
                configuracaoService.atualizarToken(token);
            }

        }
    }


}
