package com.grupo51.oficinamecanica.seguranca.controller;

import com.grupo51.oficinamecanica.seguranca.model.Usuario;
import com.grupo51.oficinamecanica.seguranca.service.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenJwtDto> login(@RequestBody @Valid LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.login(), request.senha());
        var auth = authenticationManager.authenticate(token);

        var usuario = (Usuario) auth.getPrincipal();
        var tokenJwt = jwtTokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenJwtDto(tokenJwt));
    }
}

record LoginRequest(String login, String senha) {}
record TokenJwtDto(String token) {}
