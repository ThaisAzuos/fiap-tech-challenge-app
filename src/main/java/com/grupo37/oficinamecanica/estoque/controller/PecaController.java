package com.grupo37.oficinamecanica.estoque.controller;
import com.grupo37.oficinamecanica.config.OpenApiConfig;
import com.grupo37.oficinamecanica.estoque.model.Peca;
import com.grupo37.oficinamecanica.estoque.service.PecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/pecas")
@Tag(name = OpenApiConfig.TAG_06_PECAS, description = "Estoque de pecas")
public class PecaController {
    private final PecaService pecaService;
    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }
    @PostMapping
    @Operation(
            summary = "6. Cadastrar peca",
            description = "Cadastra peca para uso no passo 10 (incluir peca na O.S.). Anote o UUID retornado no campo id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Peca criada - copie o campo id (UUID)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"nome\":\"Filtro de Oleo Mann W712\",\"preco\":48.90,\"quantidadeEstoque\":100}"))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Peca> criarPeca(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Filtro de Oleo",
                                            summary = "Peca comum",
                                            value = "{\"nome\":\"Filtro de Oleo Mann W712\",\"preco\":48.90,\"quantidadeEstoque\":100}"),
                                    @ExampleObject(
                                            name = "Pastilha de Freio",
                                            summary = "Outro exemplo editavel",
                                            value = "{\"nome\":\"Pastilha de Freio Dianteira\",\"preco\":85.00,\"quantidadeEstoque\":40}"),
                                    @ExampleObject(
                                            name = "Bateria",
                                            summary = "Bateria automotiva",
                                            value = "{\"nome\":\"Bateria 60Ah Moura\",\"preco\":430.00,\"quantidadeEstoque\":12}")
                            }))
            PecaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.salvarPeca(dto));
    }
    @GetMapping
    @Operation(
            summary = "Listar pecas",
            description = "Use esta listagem para copiar um UUID de peca antes de chamar o passo 10 da O.S.")
    @ApiResponse(responseCode = "200", description = "Lista de pecas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "exemplo",
                            value = "[{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"nome\":\"Filtro de Oleo Mann W712\",\"preco\":48.90,\"quantidadeEstoque\":100}]")))
    public ResponseEntity<List<Peca>> listar() {
        return ResponseEntity.ok(pecaService.listarTodas());
    }
}
