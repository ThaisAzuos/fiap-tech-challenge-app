package com.grupo37.oficinamecanica.seguranca.service;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenServiceTest {

    private static class KeysAndService {
        JwtTokenService service;
        KeyPair keyPair;
    }

    private KeysAndService createServiceWithTempPublicKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----\n";

        Path temp = Files.createTempFile("public-key", ".pem");
        Files.writeString(temp, pem);

        KeysAndService result = new KeysAndService();
        result.service = new JwtTokenService(temp.toString());
        result.keyPair = keyPair;
        return result;
    }

    @Test
    void deveValidarTokenValido() throws Exception {
        KeysAndService setup = createServiceWithTempPublicKey();

        String token = Jwts.builder()
                .claim("cpf", "52998224725")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(setup.keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();

        assertThat(setup.service.validarToken(token)).isTrue();
    }

    @Test
    void deveInvalidarTokenMalformado() throws Exception {
        KeysAndService setup = createServiceWithTempPublicKey();

        assertThat(setup.service.validarToken("token-invalido")).isFalse();
    }

    @Test
    void deveExtrairSubjectCpf() throws Exception {
        KeysAndService setup = createServiceWithTempPublicKey();

        String token = Jwts.builder()
                .claim("cpf", "12345678900")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(setup.keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();

        assertThat(setup.service.getSubject(token)).isEqualTo("12345678900");
    }

    @Test
    void deveLancarErroAoExtrairSubjectDeTokenInvalido() throws Exception {
        KeysAndService setup = createServiceWithTempPublicKey();

        assertThatThrownBy(() -> setup.service.getSubject("token-invalido"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token JWT inválido ou expirado");
    }

    @Test
    void deveFalharQuandoArquivoDeChaveNaoExiste() {
        assertThatThrownBy(() -> new JwtTokenService("C:\\nao-existe\\public_key.pem"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao ler chave pública");
    }
}

