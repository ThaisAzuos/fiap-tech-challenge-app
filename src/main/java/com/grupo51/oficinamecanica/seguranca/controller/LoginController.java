package com.grupo51.oficinamecanica.seguranca.controller;
import com.grupo51.oficinamecanica.config.OpenApiConfig;
import com.grupo51.oficinamecanica.seguranca.model.Usuario;
import com.grupo51.oficinamecanica.seguranca.service.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Tag(name = OpenApiConfig.TAG_01_LOGIN, description = "Autenticacao via CPF + senha para gerar JWT")
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService jwtTokenService;
    @PostMapping("/login")
    @Operation(
            summary = "0. Login - autenticar e obter JWT",
            description = "Envie CPF e senha. Depois cole o token em Authorize como: Bearer <token>." +
                    " Os exemplos abaixo ja funcionam e podem ser alterados antes da execucao.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token JWT gerado - copie o campo token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenJwtDto.class),
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9.xxxx.xxxx\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenciais invalidas",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TokenJwtDto> login(
            @RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Clique em Try it out, ajuste o payload se desejar e clique em Execute",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Mecanico (seed)",
                                            summary = "Usuario seed com perfil mecanico",
                                            value = "{\"login\":\"09151522037\",\"senha\":\"Senh@316497\"}"),
                                    @ExampleObject(
                                            name = "Atendente (seed)",
                                            summary = "Usuario seed com perfil atendente",
                                            value = "{\"login\":\"25390437021\",\"senha\":\"Senh@316497\"}")
                            }))
            LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.login(), request.senha());
        var auth = authenticationManager.authenticate(token);
        var usuario = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(new TokenJwtDto(jwtTokenService.gerarToken(usuario)));
    }
}
@Schema(description = "Credenciais de login")
record LoginRequest(
        @Schema(description = "CPF do funcionario (11 digitos)", example = "09151522037")
        String login,
        @Schema(description = "Senha", example = "Senh@316497")
        String senha
) {}
@Schema(description = "JWT retornado apos login")
record TokenJwtDto(
        @Schema(description = "Cole no Authorize: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiJ9.xxxx.xxxx")
        String token
) {}
