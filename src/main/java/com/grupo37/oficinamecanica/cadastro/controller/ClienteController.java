package com.grupo37.oficinamecanica.cadastro.controller;
import com.grupo37.oficinamecanica.cadastro.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.model.dto.ClienteDTO;
import com.grupo37.oficinamecanica.cadastro.service.CadastroService;
import com.grupo37.oficinamecanica.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = OpenApiConfig.TAG_02_CLIENTES, description = "Cadastro e consulta de clientes")
public class ClienteController {
    private final CadastroService cadastroService;
    public ClienteController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }
    @PostMapping
    @Operation(
            summary = "1. Cadastrar cliente",
            description = "Cria cliente para os proximos passos. O exemplo ja e valido e pode ser editado." +
                    " Anote o CPF retornado para usar no cadastro de veiculo e agendamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado - anote o cpf",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"cpf\":\"52998224725\",\"nome\":\"Marina Oliveira\",\"email\":\"marina.oliveira@email.com.br\",\"telefone\":\"11987654321\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Cliente> criarCliente(
            @RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Novo cliente",
                                    summary = "Payload pronto para executar e editar",
                                    value = "{\"nome\":\"Marina Oliveira\",\"cpf\":\"52998224725\",\"email\":\"marina.oliveira@email.com.br\",\"telefone\":\"11987654321\",\"endereco\":{\"logradouro\":\"Avenida Paulista\",\"numero\":\"1000\",\"complemento\":\"Apto 201\",\"bairro\":\"Bela Vista\",\"cidade\":\"Sao Paulo\",\"uf\":\"SP\",\"cep\":\"01310100\"}}"
                            )))
            ClienteDTO dto, UriComponentsBuilder uriBuilder) {
        Cliente novoCliente = cadastroService.salvarCliente(dto);
        var uri = uriBuilder.path("/api/v1/clientes/{cpf}").buildAndExpand(novoCliente.getCpf()).toUri();
        return ResponseEntity.created(uri).body(novoCliente);
    }
    @GetMapping
    @Operation(
            summary = "13. Listar clientes",
            description = "Retorna todos os clientes cadastrados, incluindo o seed de CPF 73383053036.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "seed",
                            value = "[{\"cpf\":\"73383053036\",\"nome\":\"Joao da Silva\",\"email\":\"joao@email.com\",\"telefone\":\"11999998888\"}]")))
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(cadastroService.listarTodos());
    }
}
