package com.grupo37.oficinamecanica.seguranca.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JwtTokenService {

    @Value("${api.security.token.public-key}")
    private String publicKeyPem;

    private RSAPublicKey publicKey;
    private Algorithm algorithm;

    public JwtTokenService(@Value("${api.security.token.public-key}") String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
        try {
            this.publicKey = loadPublicKey(publicKeyPem);
            this.algorithm = Algorithm.RSA256(this.publicKey, null); // RS256 com chave pública
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar a chave pública para JWT", e);
        }
    }

    private RSAPublicKey loadPublicKey(String publicKeyPem) throws Exception {
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // Remove espaços e quebras de linha
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public boolean validarToken(String token) {
        try {
            JWT.require(algorithm)
                    .withIssuer("fiap-tech-challenge-lambda-auth") // Espera o issuer da Lambda
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var verifier = JWT.require(algorithm)
                    .withIssuer("fiap-tech-challenge-lambda-auth") // Espera o issuer da Lambda
                    .build();
            var decodedJWT = verifier.verify(tokenJWT);
            // A Lambda envia o CPF no claim 'cpf'
            return decodedJWT.getClaim("cpf").asString();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!", exception);
        }
    }
}
