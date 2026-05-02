package com.grupo37.oficinamecanica.seguranca.controller;
import com.grupo37.oficinamecanica.config.OpenApiConfig;
import com.grupo37.oficinamecanica.seguranca.service.LambdaAuthService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Tag(name = OpenApiConfig.TAG_01_LOGIN, description = "Autenticacao via CPF + Lambda para gerar JWT")
public class LoginController {
    @Autowired
    private LambdaAuthService lambdaAuthService;
    @PostMapping("/authenticate")
    @Operation(
            summary = "0. Authenticate - validar CPF via Lambda e obter JWT",
            description = "Envie apenas o CPF. O sistema valida com o banco de dados via Lambda serverless e retorna JWT. " +
                    "Depois cole o token em Authorize como: Bearer <token>.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token JWT gerado - copie o campo token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"token\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...\",\"expiry\":3600,\"user_id\":123,\"correlation_id\":\"abc-123\"}"))),
            @ApiResponse(responseCode = "401", description = "CPF inválido ou cliente não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "erro",
                                    value = "{\"error\":\"Invalid CPF or customer not found\",\"correlation_id\":\"abc-123\"}"))),
            @ApiResponse(responseCode = "400", description = "CPF não fornecido",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Clique em Try it out, ajuste o CPF se desejar e clique em Execute",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "CPF válido",
                                            summary = "CPF de cliente existente",
                                            value = "{\"cpf\":\"52998224725\"}"),
                                    @ExampleObject(
                                            name = "CPF inválido",
                                            summary = "CPF com formato incorreto",
                                            value = "{\"cpf\":\"11111111111\"}")
                            }))
            AuthRequest request) {

        try {
            LambdaAuthService.LambdaAuthResponse lambdaResponse = lambdaAuthService.authenticate(request.cpf());
            AuthResponse response = new AuthResponse(
                lambdaResponse.getToken(),
                lambdaResponse.getExpiry(),
                lambdaResponse.getUserId(),
                lambdaResponse.getCorrelationId()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse(null, 0, 0, null));
        }
    }
}
@Schema(description = "Request de autenticação via CPF")
record AuthRequest(
        @Schema(description = "CPF do cliente (11 dígitos)", example = "52998224725")
        String cpf
) {}
@Schema(description = "Resposta da autenticação via Lambda")
record AuthResponse(
        @Schema(description = "Token JWT - cole no Authorize: Bearer <token>",
                example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        @Schema(description = "Tempo de expiração em segundos", example = "3600")
        int expiry,
        @Schema(description = "ID do usuário autenticado", example = "123")
        int userId,
        @Schema(description = "ID de correlação para rastreamento", example = "abc-123-def")
        String correlationId
) {}
