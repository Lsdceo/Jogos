package armazem.jogos.security;

import armazem.jogos.entities.Usuario; // Seu Usuario entity
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final String ISSUER = "armazem-jogos-api";
    private final long EXPIRATION_HOURS = 2; // Token expira em 2 horas

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getUsername()) // Usa o username do seu Usuario entity
                    .withClaim("userId", usuario.getId()) // Opcional: Adicionar ID do usuário como claim
                    // Adicionar roles como claim se necessário (requer conversão para List<String>)
                    // .withClaim("roles", usuario.getRoles().stream().map(Role::getNome).collect(Collectors.toList()))
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject(); // Retorna o username (subject)
        } catch (JWTVerificationException exception) {
            // Pode logar a exceção aqui se quiser mais detalhes sobre a falha na validação
            // ex: logger.warn("Validação de token JWT falhou: {}", exception.getMessage());
            return null; // Retorna null se a validação falhar
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(EXPIRATION_HOURS).toInstant(ZoneOffset.of("-03:00")); // Ajuste o ZoneOffset se necessário
    }

    public long getExpirationInMillis() {
        return EXPIRATION_HOURS * 60 * 60 * 1000;
    }
}