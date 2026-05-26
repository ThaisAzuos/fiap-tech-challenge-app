package com.grupo37.oficinamecanica.cadastro.controller;
import com.grupo37.oficinamecanica.cadastro.controller.dto.FuncionarioCadastroDTO;

import com.grupo37.oficinamecanica.cadastro.domain.model.Funcionario;
import com.grupo37.oficinamecanica.cadastro.service.FuncionarioService;
import com.grupo37.oficinamecanica.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
@RestController
@RequestMapping("/api/v1/funcionarios")
@Tag(name = OpenApiConfig.TAG_04_FUNCIONARIOS, description = "Cadastro de mecanicos e atendentes")
public class FuncionarioController {
    private final FuncionarioService service;
    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }
    @PostMapping
    @Operation(
            summary = "3/4. Cadastrar mecanico ou funcionario",
            description = "Cria mecanico ou atendente para usar no atendimento/agendamento." +
                    " Os exemplos estao prontos e voce pode alterar CPF, nome e especialidade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionario criado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"cpf\":\"71428793860\",\"nome\":\"Carlos Eduardo Souza\",\"cargo\":\"MECANICO\",\"especialidade\":\"MOTORES\",\"registroFuncional\":\"MF-010\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Funcionario> cadastrar(
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "3. Novo Mecanico",
                                            summary = "Use esse formato para mecanico",
                                            value = "{\"nome\":\"Carlos Eduardo Souza\",\"cpf\":\"71428793860\",\"senha\":\"Senha@2024\",\"cargo\":\"MECANICO\",\"especialidade\":\"MOTORES\",\"registroFuncional\":\"MF-010\"}"),
                                    @ExampleObject(
                                            name = "4. Novo Atendente",
                                            summary = "Use esse formato para atendente",
                                            value = "{\"nome\":\"Ana Paula Ferreira\",\"cpf\":\"87748248800\",\"senha\":\"Senha@2024\",\"cargo\":\"ATENDENTE\",\"especialidade\":null,\"registroFuncional\":\"AT-005\"}")
                            }))
            FuncionarioCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var funcionario = service.salvar(dados);
        var uri = uriBuilder.path("/api/v1/funcionarios/{cpf}").buildAndExpand(funcionario.getCpf()).toUri();
        return ResponseEntity.created(uri).body(funcionario);
    }
    @GetMapping
    @Operation(
            summary = "Listar todos os funcionarios",
            description = "Exibe seeds: Mestre Ioda (09151522037) e Atendente Solo (25390437021).")
    @ApiResponse(responseCode = "200", description = "Lista de funcionarios",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "seeds",
                            value = "[{\"cpf\":\"09151522037\",\"nome\":\"Mestre Ioda\",\"cargo\":\"MECANICO\",\"especialidade\":\"MOTORES\"},{\"cpf\":\"25390437021\",\"nome\":\"Atendente Solo\",\"cargo\":\"ATENDENTE\"}]")))
    public List<Funcionario> listar() {
        return service.listarTodos();
    }
}
