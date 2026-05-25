package com.grupo37.oficinamecanica.seguranca.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JwtTokenService {

    private final RSAPublicKey publicKey;

    public JwtTokenService(@Value("${api.security.token.public-key}") String publicKeyPath) {
        try {
            // Lê o conteúdo do arquivo localizado no caminho passado pela variável de ambiente
            String keyContent = java.nio.file.Files.readString(java.nio.file.Paths.get(publicKeyPath));
            this.publicKey = loadPublicKey(keyContent);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler chave pública em: " + publicKeyPath, e);
        }
    }

    private RSAPublicKey loadPublicKey(String publicKeyPem) throws Exception {
        // 1. Remove os cabeçalhos e rodapés
        String publicKeyContent = publicKeyPem
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\r", "") // Remove retorno de carro
                .replaceAll("\\n", "") // Remove nova linha
                .replaceAll("\\s", ""); // Remove qualquer espaço extra

        // 2. Decodifica o Base64
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);

        // 3. Gera a chave pública
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException exception) {
            return false;
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var decodedJWT = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(tokenJWT)
                    .getPayload();
            // A Lambda envia o CPF no claim 'cpf'
            return decodedJWT.get("cpf", String.class);
        } catch (JwtException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!", exception);
        }
    }
}
