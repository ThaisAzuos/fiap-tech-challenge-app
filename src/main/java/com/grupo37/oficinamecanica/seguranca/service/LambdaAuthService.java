package com.grupo37.oficinamecanica.seguranca.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class LambdaAuthService {

    private static final Logger logger = LoggerFactory.getLogger(LambdaAuthService.class);

    @Value("${lambda.auth.url:http://localhost:8080}")
    private String lambdaAuthUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LambdaAuthService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public LambdaAuthResponse authenticate(String cpf) {
        String correlationId = UUID.randomUUID().toString();

        try {
            logger.info("Iniciando autenticação via Lambda para CPF: {}", cpf.replaceAll("\\d(?=\\d{4})", "*"));

            // Preparar request para Lambda
            Map<String, String> requestBody = Map.of(
                "cpf", cpf,
                "correlation_id", correlationId
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Chamar Lambda via API Gateway
            String fullUrl = lambdaAuthUrl + "/authenticate";
            logger.debug("Chamando Lambda em: {}", fullUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            logger.info("Resposta do Lambda: status={}, correlationId={}", response.getStatusCode(), correlationId);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse successful response
                LambdaAuthResponse authResponse = objectMapper.readValue(response.getBody(), LambdaAuthResponse.class);
                logger.info("Autenticação bem-sucedida para user_id: {}", authResponse.getUserId());
                return authResponse;
            } else {
                // Parse error response
                Map<String, String> errorResponse = objectMapper.readValue(response.getBody(), Map.class);
                String error = errorResponse.get("error");
                logger.warn("Falha na autenticação: {}", error);
                throw new RuntimeException("Autenticação falhou: " + error);
            }

        } catch (Exception e) {
            logger.error("Erro ao chamar Lambda de autenticação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno na autenticação", e);
        }
    }

    public static class LambdaAuthResponse {
        private String token;
        private int expiry;
        private int userId;
        private String correlationId;

        public LambdaAuthResponse() {}

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpiry() {
            return expiry;
        }

        public void setExpiry(int expiry) {
            this.expiry = expiry;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }
    }
}
