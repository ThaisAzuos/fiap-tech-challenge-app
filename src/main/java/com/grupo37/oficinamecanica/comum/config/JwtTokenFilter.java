package com.grupo37.oficinamecanica.comum.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.security.token.public-key}")
    @SuppressWarnings("unused")
    private String publicKey;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Pula validação para endpoints públicos
        if (path.equals("/login") || path.startsWith("/actuator") || path.contains("/api/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);

            if (token != null && !token.isEmpty()) {
                if (publicKey == null || publicKey.isBlank()) {
                    logger.warn("Chave pública JWT não configurada");
                } else {
                    try {
                        // Valida JWT com chave pública RSA (RS256)
                        PublicKey pub = getPublicKeyFromString(publicKey);
                        Claims claims = Jwts.parser()
                                .verifyWith(pub)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();

                        String cpf = claims.get("cpf", String.class);

                        var auth = new UsernamePasswordAuthenticationToken(
                                cpf, null, Collections.emptyList()
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } catch (Exception ex) {
                        logger.error("Erro ao validar/processar JWT", ex);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao processar requisição no filtro JWT", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private PublicKey getPublicKeyFromString(String key) throws Exception {
        // Remove headers/footers PEM e whitespace
        String pem = key.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");
        
        byte[] decoded = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}