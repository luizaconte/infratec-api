package br.com.infratec.service.sales;

import br.com.infratec.client.MeliAuthClient;
import br.com.infratec.config.Constants;
import br.com.infratec.dto.TokenRequest;
import br.com.infratec.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Service
public class MeliAuthService {

    @Value("${application.services.meli-api-url}")
    private String meliApiUrl;

    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";
    private static final String GRANT_TYPE_REFRESH = "refresh_token";

    public TokenResponse getAccessToken() {
        return authClient().getAccessToken(
                TokenRequest.builder()
                        .code(Constants.APP_CONFIG.getCode())
                        .clientId(Constants.APP_CONFIG.getClientId())
                        .clientSecret(Constants.APP_CONFIG.getClientSecret())
                        .grantType(GRANT_TYPE_AUTHORIZATION)
                        .build()
        );
    }

    public TokenResponse refreshToken() {
        return authClient().refreshToken(
                TokenRequest.builder()
                        .grantType(GRANT_TYPE_REFRESH)
                        .clientId(Constants.APP_CONFIG.getClientId())
                        .clientSecret(Constants.APP_CONFIG.getClientSecret())
                        .refreshToken(Constants.APP_CONFIG.getRefreshToken())
                        .build()
        );
    }

    private WebClient webClientAuth() {
        return WebClient.builder()
                .baseUrl(meliApiUrl)
                .build();
    }

    private MeliAuthClient authClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClientAuth()))
                        .build();
        return httpServiceProxyFactory.createClient(MeliAuthClient.class);
    }


}
