package com.grupo37.oficinamecanica.seguranca.controller;

import com.grupo37.oficinamecanica.seguranca.service.LambdaAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private LambdaAuthService lambdaAuthService;

    private LoginController controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = new LoginController();
        var field = LoginController.class.getDeclaredField("lambdaAuthService");
        field.setAccessible(true);
        field.set(controller, lambdaAuthService);
    }

    @Test
    void deveEfetuarLoginERetornarToken() {
        LambdaAuthService.LambdaAuthResponse authResponse = new LambdaAuthService.LambdaAuthResponse();
        authResponse.setToken("jwt-token-123");
        when(lambdaAuthService.authenticate("52998224725")).thenReturn(authResponse);

        ResponseEntity<LoginController.TokenResponse> response =
                controller.efetuarLogin(new LoginController.LoginRequest("52998224725"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().token()).isEqualTo("jwt-token-123");
    }
}

