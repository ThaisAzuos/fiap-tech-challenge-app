package com.grupo37.oficinamecanica.seguranca.controller;

import com.grupo37.oficinamecanica.seguranca.service.LambdaAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LambdaAuthService lambdaAuthService;

    @PostMapping
    public ResponseEntity<TokenResponse> efetuarLogin(@RequestBody @Valid LoginRequest request) {
        LambdaAuthService.LambdaAuthResponse authResponse = lambdaAuthService.authenticate(request.cpf());
        return ResponseEntity.ok(new TokenResponse(authResponse.getToken()));
    }

    public record LoginRequest(String cpf) {}
    public record TokenResponse(String token) {}
}
