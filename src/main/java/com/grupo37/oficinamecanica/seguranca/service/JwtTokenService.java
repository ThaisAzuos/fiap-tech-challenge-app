package com.grupo37.oficinamecanica.seguranca.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.grupo37.oficinamecanica.seguranca.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-minutes:30}")
    private long expirationMinutes;

    public String gerarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Oficina Mecanica")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }

    public boolean validarToken(String token) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algoritmo)
                    .withIssuer("API Oficina Mecanica")
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException localException) {
            // Try validating as Lambda token
            try {
                var algoritmo = Algorithm.HMAC256(secret);
                var verifier = JWT.require(algoritmo)
                        .withIssuer("fiap-tech-challenge-lambda-auth")
                        .build();
                verifier.verify(token);
                return true;
            } catch (JWTVerificationException lambdaException) {
                return false;
            }
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algoritmo)
                    .withIssuer("API Oficina Mecanica")
                    .build();
            return verifier.verify(tokenJWT).getSubject();
        } catch (JWTVerificationException localException) {
            // Try getting subject from Lambda token
            try {
                var algoritmo = Algorithm.HMAC256(secret);
                var verifier = JWT.require(algoritmo)
                        .withIssuer("fiap-tech-challenge-lambda-auth")
                        .build();
                var decoded = verifier.verify(tokenJWT);
                // Lambda tokens use 'cpf' claim instead of 'sub'
                return decoded.getClaim("cpf").asString();
            } catch (JWTVerificationException lambdaException) {
                throw new RuntimeException("Token JWT inválido ou expirado!");
            }
        }
    }

    private Instant dataExpiracao() {
        return Instant.now().plusSeconds(expirationMinutes * 60);
    }
}
