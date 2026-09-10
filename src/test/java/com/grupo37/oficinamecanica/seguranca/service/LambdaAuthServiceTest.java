package com.grupo37.oficinamecanica.seguranca.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LambdaAuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private LambdaAuthService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new LambdaAuthService();
        setField(service, "lambdaAuthUrl", "http://localhost:3000");
        setField(service, "restTemplate", restTemplate);
        setField(service, "objectMapper", new ObjectMapper());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void deveAutenticarComSucessoQuandoLambdaRetorna200() {
        String responseBody = "{\"token\":\"jwt-token\",\"expiry\":3600,\"user_id\":\"52998224725\",\"correlation_id\":\"abc\"}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq("http://localhost:3000/authenticate"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        LambdaAuthService.LambdaAuthResponse authResponse = service.authenticate("52998224725");

        assertThat(authResponse.getToken()).isEqualTo("jwt-token");
        assertThat(authResponse.getUserId()).isEqualTo("52998224725");
        assertThat(authResponse.getExpiry()).isEqualTo(3600);
    }

    @Test
    void deveLancarErroQuandoLambdaRetornaBodyInvalido() {
        ResponseEntity<String> response = new ResponseEntity<>("not-json", HttpStatus.OK);

        when(restTemplate.exchange(eq("http://localhost:3000/authenticate"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        assertThatThrownBy(() -> service.authenticate("52998224725"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro interno na autenticação");
    }

    @Test
    void deveLancarErroQuandoRestTemplateFalha() {
        when(restTemplate.exchange(eq("http://localhost:3000/authenticate"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("connection refused"));

        assertThatThrownBy(() -> service.authenticate("52998224725"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro interno na autenticação");
    }
}

