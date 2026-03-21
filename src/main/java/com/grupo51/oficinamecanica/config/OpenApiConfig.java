package com.grupo51.oficinamecanica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String TAG_01_LOGIN = "01. Login";
    public static final String TAG_02_CLIENTES = "02. Clientes";
    public static final String TAG_03_VEICULOS = "03. Veiculos";
    public static final String TAG_04_FUNCIONARIOS = "04. Funcionarios";
    public static final String TAG_05_AGENDAMENTOS = "05. Agendamentos";
    public static final String TAG_06_PECAS = "06. Pecas";
    public static final String TAG_07_ATENDIMENTO = "07. Atendimento (O.S.)";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Oficina Mecanica API")
                        .version("1.0.0")
                        .description("""
                                API REST para gestao de oficina mecanica com fluxo guiado para teste real no Swagger.

                                Fluxo recomendado (mesma ordem da colecao Postman):
                                0. Login
                                1. Cadastrar cliente
                                2. Cadastrar veiculo
                                3. Cadastrar mecanico
                                4. Cadastrar funcionario
                                5. Realizar agendamento
                                6. Cadastrar peca
                                7. Abrir O.S.
                                8. Avancar status da O.S.
                                9. Consultar detalhes da O.S.
                                10. Incluir peca na O.S.
                                11. Aprovar orcamento
                                12. Listar O.S.
                                13. Listar clientes
                                14. Listar veiculos por dono

                                Como usar:
                                1) Execute o endpoint de login e copie o token.
                                2) Clique em Authorize e informe: Bearer <token>.
                                3) Use os exemplos pre-preenchidos e altere os campos necessarios antes de executar.

                                Dados seed disponiveis apos subir a aplicacao:
                                - Cliente: CPF 73383053036 (Joao da Silva)
                                - Veiculo: Placa ABC1D23 (dono 73383053036)
                                - Login mecanico: CPF 09151522037 | Senha Senh@316497
                                - Login atendente: CPF 25390437021 | Senha Senh@316497
                                """)
                        .contact(new Contact()
                                .name("Tech Challenge FIAP - Grupo 51")
                                .url("https://github.com/celio-vetrano/fiap-tech-challenge-oficina"))
                        .license(new License().name("MIT")))
                .tags(List.of(
                        new Tag().name(TAG_01_LOGIN).description("Passo 0 - Obter JWT"),
                        new Tag().name(TAG_02_CLIENTES).description("Passos 1 e 13 - Criacao e listagem de clientes"),
                        new Tag().name(TAG_03_VEICULOS).description("Passos 2 e 14 - Criacao e listagem de veiculos"),
                        new Tag().name(TAG_04_FUNCIONARIOS).description("Passos 3 e 4 - Cadastro de mecanicos e demais funcionarios"),
                        new Tag().name(TAG_05_AGENDAMENTOS).description("Passo 5 - Criacao e consulta de agendamentos"),
                        new Tag().name(TAG_06_PECAS).description("Passo 6 - Cadastro e listagem de pecas"),
                        new Tag().name(TAG_07_ATENDIMENTO).description("Passos 7 a 12 - Fluxo completo da Ordem de Servico")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Cole o token retornado pelo POST /login no formato: Bearer eyJ...")));
    }
}
