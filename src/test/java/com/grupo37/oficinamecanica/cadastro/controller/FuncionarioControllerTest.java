package com.grupo37.oficinamecanica.cadastro.controller;

import com.grupo37.oficinamecanica.cadastro.controller.dto.FuncionarioCadastroDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cargo;
import com.grupo37.oficinamecanica.cadastro.domain.model.Especialidade;
import com.grupo37.oficinamecanica.cadastro.domain.model.Funcionario;
import com.grupo37.oficinamecanica.cadastro.service.FuncionarioService;
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
class FuncionarioControllerTest {

    @Mock
    private FuncionarioService service;

    @Mock
    private Funcionario funcionarioMock;

    private FuncionarioController controller;

    @BeforeEach
    void setUp() {
        controller = new FuncionarioController(service);
    }

    private Funcionario criarFuncionarioReal() {
        return new Funcionario(
                "Carlos Silva",
                new com.grupo37.oficinamecanica.cadastro.domain.model.Cpf("71428793860"),
                new com.grupo37.oficinamecanica.cadastro.domain.model.Email("funcionario.71428793860@oficina.com"),
                Cargo.MECANICO,
                "senha_enc",
                Especialidade.MOTOR,
                "RF-010"
        );
    }

    @Test
    void deveCadastrarFuncionarioERetornar201() {
        FuncionarioCadastroDTO dto = new FuncionarioCadastroDTO(
                "Carlos Silva", "71428793860", "senha123",
                Cargo.MECANICO, Especialidade.MOTOR, "RF-010"
        );
        when(service.salvar(any(FuncionarioCadastroDTO.class))).thenReturn(funcionarioMock);

        ResponseEntity<Funcionario> response = controller.cadastrar(dto, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(service).salvar(dto);
    }

    @Test
    void deveListarFuncionarios() {
        when(service.listarTodos()).thenReturn(List.of(funcionarioMock));

        List<Funcionario> resultado = controller.listar();

        assertThat(resultado).hasSize(1);
        verify(service).listarTodos();
    }

    @Test
    void deveRetornarListaVaziaQuandoSemFuncionarios() {
        when(service.listarTodos()).thenReturn(List.of());

        List<Funcionario> resultado = controller.listar();

        assertThat(resultado).isEmpty();
    }
}

