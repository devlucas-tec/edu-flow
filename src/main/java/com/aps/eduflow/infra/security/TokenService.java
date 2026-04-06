package com.aps.eduflow.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.aps.eduflow.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret:eduflow-jwt-secret}")
    private String tokenSecret;

    @Value("${api.security.token.expiration-hours:2}")
    private int expirationHours;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);

            return JWT.create()
                    .withIssuer("EduFlow")
                    .withSubject(usuario.getEmail())
                    .withClaim("roles", usuario.getRole().name())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String getSubjectFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);

            return JWT.require(algorithm)
                    .withIssuer("EduFlow")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        return getSubjectFromToken(token) != null;
    }

    private Instant expirationDate() {
        return LocalDateTime.now()
                .plusHours(expirationHours)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
