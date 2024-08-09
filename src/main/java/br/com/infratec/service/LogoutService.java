package br.com.infratec.service;

import br.com.infratec.repository.UsuarioChaveRepository;
import br.com.infratec.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final UsuarioChaveRepository usuarioChaveRepository;
    private final JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        String accessKey = jwtService.extractAccessKey(jwt);
        usuarioChaveRepository.deleteTbUsuarioChaveByChavePublica(accessKey);
        SecurityContextHolder.clearContext();
    }
}
