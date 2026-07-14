package com.grupo37.oficinamecanica.cadastro.controller;

import com.grupo37.oficinamecanica.cadastro.controller.dto.ClienteDTO;
import com.grupo37.oficinamecanica.cadastro.controller.dto.EnderecoDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.service.CadastroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private CadastroService cadastroService;

    @Mock
    private Cliente clienteMock;

    private ClienteController controller;

    @BeforeEach
    void setUp() {
        controller = new ClienteController(cadastroService);
    }

    @Test
    void deveCriarClienteERetornar201() {
        when(clienteMock.getCpf()).thenReturn("52998224725");
        EnderecoDTO endereco = new EnderecoDTO("Av. Paulista", "1000", null,
                "Bela Vista", "São Paulo", "SP", "01310100");
        ClienteDTO dto = new ClienteDTO("Marina Oliveira", "52998224725",
                "marina@email.com", "11987654321", endereco);

        when(cadastroService.salvarCliente(any(ClienteDTO.class))).thenReturn(clienteMock);

        ResponseEntity<Cliente> response = controller.criarCliente(dto, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(cadastroService).salvarCliente(dto);
    }

    @Test
    void deveListarClientesERetornar200() {
        when(cadastroService.listarTodos()).thenReturn(List.of(clienteMock));

        ResponseEntity<List<Cliente>> response = controller.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(cadastroService).listarTodos();
    }

    @Test
    void deveRetornarListaVaziaQuandoSemClientes() {
        when(cadastroService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<Cliente>> response = controller.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}

