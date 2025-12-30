package it.unina.bugboard.security;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;

@Component
public class JwtService {

    private static final String ISSUER = "bugboard-api";

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Algorithm algo;
    private JWTVerifier verifier;

    @PostConstruct
    void init() {
        this.algo = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(algo).withIssuer(ISSUER).build();
    }

    public String createToken(Long userId, String role) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(userId))     // chi è l’utente
                .withClaim("role", role)                 // "ADMIN" o "USER"
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algo);
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token); // se non valido/scaduto -> eccezione
    }
    
    public Long extractUserId(String token) {
        return Long.parseLong(
            JWT.require(algo)
               .withIssuer(ISSUER)
               .build()
               .verify(token)
               .getSubject()
        );
    }

    public String extractRole(String token) {
        return JWT.require(algo)
                  .withIssuer(ISSUER)
                  .build()
                  .verify(token)
                  .getClaim("role")
                  .asString(); // "ADMIN" o "USER"
    }

}
