package it.unina.bugboard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtService {

    private static final String ISSUER = "bugboard-api";
    private static final Algorithm ALGO = Algorithm.HMAC256("very_secret_key_not_to_share");
    private static final JWTVerifier VERIFIER = JWT.require(ALGO).withIssuer(ISSUER).build();

    public String createToken(Long userId, String role) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(userId))     // chi è l’utente
                .withClaim("role", role)                 // "ADMIN" o "USER"
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))
                .withJWTId(UUID.randomUUID().toString())
                .sign(ALGO);
    }

    public DecodedJWT verify(String token) {
        return VERIFIER.verify(token); // se non valido/scaduto -> eccezione
    }
    
    public Long extractUserId(String token) {
        return Long.parseLong(
            JWT.require(ALGO)
               .withIssuer(ISSUER)
               .build()
               .verify(token)
               .getSubject()
        );
    }

    public String extractRole(String token) {
        return JWT.require(ALGO)
                  .withIssuer(ISSUER)
                  .build()
                  .verify(token)
                  .getClaim("role")
                  .asString(); // "ADMIN" o "USER"
    }

}
