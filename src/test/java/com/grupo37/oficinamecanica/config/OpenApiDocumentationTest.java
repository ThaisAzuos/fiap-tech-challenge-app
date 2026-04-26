package com.grupo37.oficinamecanica.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void shouldExposeOrderedTagsWithExamplesAndCorrectMetadata() throws Exception {
        var body = mockMvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(body);

        // --- info ---
        assertThat(root.path("info").path("version").asText()).isEqualTo("1.0.0");
        assertThat(root.path("info").path("description").asText())
                .contains("Fluxo recomendado")
                .contains("Dados seed disponiveis");

        // --- tags documentadas ---
        JsonNode tags = root.path("tags");
        assertThat(tags.isArray()).isTrue();

        var tagNames = StreamSupport.stream(tags.spliterator(), false)
                .map(tag -> tag.path("name").asText())
                .toList();
        assertThat(tagNames).contains(
                "01. Login",
                "02. Clientes",
                "03. Veiculos",
                "04. Funcionarios",
                "05. Agendamentos",
                "06. Pecas",
                "07. Atendimento (O.S.)"
        );

        // --- login: summary, security vazia e examples ---
        JsonNode loginPost = root.path("paths").path("/login").path("post");
        assertThat(loginPost.path("summary").asText()).isEqualTo("0. Login - autenticar e obter JWT");
        assertThat(loginPost.path("requestBody").path("content")
                .path("application/json").path("examples").has("Mecanico (seed)")).isTrue();

        // --- clientes: summary e example ---
        JsonNode clientesPost = root.path("paths").path("/api/v1/clientes").path("post");
        assertThat(clientesPost.path("summary").asText()).isEqualTo("1. Cadastrar cliente");
        assertThat(clientesPost.path("requestBody").path("content")
                .path("application/json").path("examples").has("Novo cliente")).isTrue();

        // --- veículos: dois examples ---
        JsonNode veiculosPost = root.path("paths").path("/api/v1/veiculos").path("post");
        JsonNode veiculoExamples = veiculosPost.path("requestBody").path("content")
                .path("application/json").path("examples");
        assertThat(veiculoExamples.has("Novo veiculo (cliente seed)")).isTrue();

        // --- OS: summary e example atualizados ---
        JsonNode osPost = root.path("paths").path("/api/v1/atendimento/os").path("post");
        assertThat(osPost.path("summary").asText()).isEqualTo("7. Abrir Ordem de Servico");
        assertThat(osPost.path("requestBody").path("content")
                .path("application/json").path("examples").has("Veiculo seed (ABC1D23)")).isTrue();

        // --- status de O.S. exposto com enum correto ---
        JsonNode statusPatch = root.path("paths").path("/api/v1/atendimento/os/{osId}/status").path("patch");
        assertThat(statusPatch.path("summary").asText()).isEqualTo("8. Avancar status da O.S.");
        JsonNode parameters = statusPatch.path("parameters");
        JsonNode novoStatusSchema = parameters.get(1).path("schema");
        assertThat(novoStatusSchema.path("enum").toString())
                .contains("RECEBIDA")
                .contains("AGUARDANDO_APROVACAO")
                .contains("CANCELADA");
    }
}

