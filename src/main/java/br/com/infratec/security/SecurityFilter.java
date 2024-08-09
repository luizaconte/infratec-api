package br.com.infratec.security;

import br.com.infratec.exception.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class SecurityFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<String> excludeUrlPatterns = List.of(
                "/",
                "/error",
                "/images/**",
                "/index.html",
                "/favicon.ico",
                "/webjars/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/configuracao/**",
                "/api/v1/auth/**"
        );

        return excludeUrlPatterns
                .stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            TubaraoAuthentication authentication = new TubaraoAuthentication(extractPrincipal(token));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getOutputStream().println(new ObjectMapper().writeValueAsString(new UnauthorizedException(e.getMessage())));
        }
    }

    private CustomPrincipal extractPrincipal(String token) {
        DecodedJWT jwt = JWT.decode(token.substring(AUTHENTICATION_SCHEME.length()).trim());
        return CustomPrincipal.builder()
                .userId(Integer.parseInt(String.valueOf(jwt.getClaims().get("userId"))))
                .name(jwt.getSubject())
                .build();
    }

}
