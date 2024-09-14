package br.com.infratec.controller;

import br.com.infratec.dto.LoginRequestDTO;
import br.com.infratec.dto.Token;
import br.com.infratec.dto.TokenBearer;
import br.com.infratec.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação")
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;

    @Autowired
    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticação com usuário e senha")
    public ResponseEntity<TokenBearer> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(autenticacaoService.login(loginRequestDTO));
    }

    @GetMapping("/autorizar")
    @Operation(summary = "Autorização da chave de acesso")
    public ResponseEntity<Token> autorizar(@RequestHeader("Authorization") String bearer) {
        return ResponseEntity.ok(autenticacaoService.autorizar(bearer));
    }

    @GetMapping("/atualizar")
    @Operation(summary = "Atualização do Token de Acesso")
    public ResponseEntity<Token> atualizar(@RequestHeader("Authorization") String token) {
        String key = token;
        if (token.contains("Bearer")) {
            key = token.substring(0, token.indexOf("Bearer") - 1);
        }
        return ResponseEntity.ok(autenticacaoService.atualizar(key));
    }

    @GetMapping("/verificar")
    @Operation(summary = "Validação do Token de Acesso")
    public ResponseEntity<Void> validar(@RequestHeader("Authorization") String token) {
        autenticacaoService.verificar(token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/logout")
    @Operation(summary = "Logout do sistema")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        autenticacaoService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
