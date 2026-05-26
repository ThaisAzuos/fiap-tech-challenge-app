package com.grupo37.oficinamecanica.seguranca.controller;

import com.grupo37.oficinamecanica.config.OpenApiConfig;
import com.grupo37.oficinamecanica.seguranca.service.LambdaAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = OpenApiConfig.TAG_01_LOGIN, description = "Autenticação via CPF")
public class LoginController {

    @Autowired
    private LambdaAuthService lambdaAuthService;

    @PostMapping
    @Operation(summary = "0. Login - autenticar e obter JWT",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "Mecanico (seed)",
                    value = "{\"cpf\": \"11144477735\"}"))))
    public ResponseEntity<TokenResponse> efetuarLogin(@RequestBody @Valid LoginRequest request) {
        LambdaAuthService.LambdaAuthResponse authResponse = lambdaAuthService.authenticate(request.cpf());
        return ResponseEntity.ok(new TokenResponse(authResponse.getToken()));
    }

    public record LoginRequest(String cpf) {}
    public record TokenResponse(String token) {}
}
