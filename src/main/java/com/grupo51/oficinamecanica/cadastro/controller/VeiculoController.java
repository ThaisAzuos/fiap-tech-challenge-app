package com.grupo51.oficinamecanica.cadastro.controller;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.model.dto.VeiculoDTO;
import com.grupo51.oficinamecanica.cadastro.service.VeiculoService;
import com.grupo51.oficinamecanica.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/veiculos")
@Tag(name = OpenApiConfig.TAG_03_VEICULOS, description = "Cadastro e consulta de veiculos")
public class VeiculoController {
    private final VeiculoService veiculoService;
    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }
    @PostMapping
    @Operation(
            summary = "2. Cadastrar veiculo",
            description = "Vincula veiculo ao cliente via cpfDono. O exemplo principal ja funciona com cliente seed 73383053036.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veiculo criado - anote a placa",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"placa\":\"DEF4G56\",\"modelo\":\"Corolla\",\"marca\":\"Toyota\",\"ano\":2023,\"cor\":\"Preto\",\"cpfDono\":\"73383053036\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Veiculo> criarVeiculo(
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Novo veiculo (cliente seed)",
                                            summary = "Executa sem precisar criar cliente antes",
                                            value = "{\"placa\":\"DEF4G56\",\"modelo\":\"Corolla\",\"marca\":\"Toyota\",\"ano\":2023,\"cor\":\"Preto\",\"cpfDono\":\"73383053036\"}"),
                                    @ExampleObject(
                                            name = "Novo veiculo (cliente criado no passo 1)",
                                            summary = "Use o cpf criado em 01. Clientes",
                                            value = "{\"placa\":\"GHI7J89\",\"modelo\":\"HB20\",\"marca\":\"Hyundai\",\"ano\":2021,\"cor\":\"Branco\",\"cpfDono\":\"52998224725\"}")
                            }))
            VeiculoDTO dto, UriComponentsBuilder uriBuilder) {
        Veiculo novoVeiculo = veiculoService.salvarVeiculo(dto);
        var uri = uriBuilder.path("/api/v1/veiculos/{placa}").buildAndExpand(novoVeiculo.getPlaca()).toUri();
        return ResponseEntity.created(uri).body(novoVeiculo);
    }
    @GetMapping("/dono/{cpf}")
    @Operation(
            summary = "14. Listar veiculos por dono",
            description = "Retorna todos os veiculos do cliente informado. Use 73383053036 para testar com seed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veiculos do cliente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "seed",
                                    value = "[{\"placa\":\"ABC1D23\",\"modelo\":\"Civic\",\"marca\":\"Honda\",\"ano\":2022,\"cor\":\"Prata\",\"cpfDono\":\"73383053036\"}]"))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<List<Veiculo>> listarPorDono(
            @Parameter(description = "CPF do proprietario (11 digitos)", example = "73383053036")
            @PathVariable String cpf) {
        return ResponseEntity.ok(veiculoService.listarPorCpfDono(cpf));
    }
}
