package br.com.infratec.service.sales;

import br.com.infratec.dto.LoginRequestDTO;
import br.com.infratec.dto.Token;
import br.com.infratec.dto.TokenBearer;
import br.com.infratec.dto.UsuarioDTO;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.sales.TbUsuario;
import br.com.infratec.model.sales.TbUsuarioChave;
import br.com.infratec.repository.sales.UsuarioChaveRepository;
import br.com.infratec.repository.sales.UsuarioRepository;
import br.com.infratec.util.JwtService;
import cz.jirutka.rsql.parser.RSQLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AutenticacaoService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioChaveRepository usuarioChaveRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AutenticacaoService(UsuarioRepository usuarioRepository, UsuarioChaveRepository usuarioChaveRepository, RSQLParser rsqlParser, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioChaveRepository = usuarioChaveRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = new JwtService();
    }

    public TokenBearer login(LoginRequestDTO loginRequestDTO) throws ZCException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsuario(),
                        loginRequestDTO.getSenha()
                )
        );

        TbUsuario tbUsuario = usuarioRepository.findByLogin(loginRequestDTO.getUsuario()).orElseThrow();

        String privateKey = jwtService.generatePrivateKey(loginRequestDTO.getUsuario(), loginRequestDTO.getSenha());
        String publicKey = jwtService.generatePublicKey(loginRequestDTO.getUsuario());

        TbUsuarioChave grUsuarioKey = new TbUsuarioChave();
        grUsuarioKey.setLogin(loginRequestDTO.getUsuario());
        grUsuarioKey.setChavePrivada(privateKey);
        grUsuarioKey.setChavePublica(publicKey);
        grUsuarioKey.setValidade(Instant.now().toEpochMilli());
        grUsuarioKey.setDataInclusao(Instant.now());
        grUsuarioKey.setIdUsuario(tbUsuario);
        usuarioChaveRepository.save(grUsuarioKey);

        return TokenBearer.builder().bearer(publicKey).build();
    }

    public Token autorizar(String chavePublica) throws ZCException {
        TbUsuarioChave tbUsuarioChave = recuperarChave(chavePublica);
        if (tbUsuarioChave != null) {
            if (!jwtService.isExpired(tbUsuarioChave.getValidade())) {
                return gerar(tbUsuarioChave);
            }
        }
        throw new ZCException("Usuário e/ou senha inválidos.");
    }

    private Token gerar(TbUsuarioChave tbUsuarioChave) throws ZCException {
        TbUsuario tbUsuario = usuarioRepository.findByLogin(tbUsuarioChave.getLogin()).orElseThrow();

        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(tbUsuario.getId())
                .email(tbUsuario.getEmail())
                .nome(tbUsuario.getNome())
                .login(tbUsuario.getLogin())
                .accessKey(tbUsuarioChave.getChavePublica())
                .build();

        return Token.builder()
                .accessToken(jwtService.generateAccessToken(usuarioDTO, tbUsuarioChave.getChavePrivada()))
                .refreshToken(jwtService.generateRefreshToken(usuarioDTO, tbUsuarioChave.getChavePrivada()))
                .user(usuarioDTO)
                .build();
    }


    public void verificar(String key) throws ZCException {
        verificarToken(key);
    }

    public Token atualizar(String token) throws ZCException {
        String refreshKey = jwtService.extractRefreshKey(token);
        if (refreshKey != null) {
            TbUsuarioChave grUsuarioKey = recuperarChave(refreshKey);
            if (grUsuarioKey != null) {
                jwtService.verifyToken(token, grUsuarioKey.getChavePrivada());
                return gerar(grUsuarioKey);
            }
        }
        throw new ZCException("Token inválido");
    }

    public TbUsuarioChave recuperarChave(String chavePublica) {
        return usuarioChaveRepository.findByChavePublica(chavePublica);
    }

    public void verificarToken(String token) throws ZCException {
        String accessKey = jwtService.extractAccessKey(token);
        if (accessKey != null) {
            TbUsuarioChave grUsuarioKey = recuperarChave(accessKey);
            if (grUsuarioKey != null) {
                jwtService.verifyToken(token, grUsuarioKey.getChavePrivada());
                return;
            }
        }
        throw new ZCException("Token inválido");

    }

    @Transactional
    public void logout(String token) throws ZCException {
        String accessKey = jwtService.extractAccessKey(token);
        usuarioChaveRepository.deleteTbUsuarioChaveByChavePublica(accessKey);
    }
}
