package br.com.infratec.util;

import br.com.infratec.dto.UsuarioDTO;
import br.com.infratec.exception.InfratecException;
import br.com.infratec.security.InfraTecAuthentication;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

@Service
public class JwtService {

    private static final String ISSUER = "INFRATEC";
    private static final int REQUEST_SECONDS_LIMIT = 60;

    private long jwtExpiration = 86400; //1 dia

    private long refreshExpiration = 604800; //7 dias

    private long notBefore = 300; //5 minutos

    public String generateAccessToken(UsuarioDTO usuario, String privateKey) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(generateInMillis(jwtExpiration))
                //.withNotBefore(new Date())
                .withSubject(usuario.getLogin())
                .withClaim("access_key", usuario.getAccessKey())
                .withClaim("userId", usuario.getId())
                .withClaim("type", usuario.getTipo().getDescricao())
                .sign(algorithm);
    }

    public String generateRefreshToken(UsuarioDTO usuario, String privateKey) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(generateInMillis(refreshExpiration))
                //.withNotBefore(generateInMillis(notBefore))
                .withSubject(usuario.getLogin())
                .withClaim("refresh_key", usuario.getAccessKey())
                .sign(algorithm);
    }

    public boolean isExpired(Date expiresAt) {
        return expiresAt.before(new Date());
    }

    public boolean isExpired(long timestamp) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime keyTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
        long seconds = ChronoUnit.SECONDS.between(keyTime, now);
        return (int) seconds > REQUEST_SECONDS_LIMIT;
    }

    public void verifyToken(String token, String privateKey) throws InfratecException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new InfratecException(exception);
        }
    }

    public String extractAccessKey(String token) {
        return JWT.decode(token).getClaim("access_key").asString();
    }

    public String extractLogin(String token) {
        return JWT.decode(token).getSubject();
    }

    public String extractRefreshKey(String token) {
        return JWT.decode(token).getClaim("refresh_key").asString();
    }

    private Date generateInMillis(long value) {
        return new Date(System.currentTimeMillis() + value * 1000);
    }

    public String generatePrivateKey(String username, String password) {
        Random random = new Random();
        return DigestUtils.sha1Hex(String.format("%s:%s:%s:%s", new Date().getTime(), username, password, random.nextInt()));
    }

    public String generatePublicKey(String username) {
        return DigestUtils.sha1Hex(String.format("%s:%s", new Date().getTime(), username));
    }

    public static String getLogin() {
        InfraTecAuthentication authentication = (InfraTecAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().getName();
    }

    public static Integer getId() {
        InfraTecAuthentication authentication = (InfraTecAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().getUserId();
    }

    public static String getType() {
        InfraTecAuthentication authentication = (InfraTecAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().getType();
    }
}
