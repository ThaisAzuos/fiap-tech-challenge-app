package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.controller.dto.FuncionarioCadastroDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cargo;
import com.grupo37.oficinamecanica.cadastro.domain.model.Especialidade;
import com.grupo37.oficinamecanica.cadastro.domain.model.Funcionario;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.FuncionarioEntity;
import com.grupo37.oficinamecanica.cadastro.repository.FuncionarioRepository;
import com.grupo37.oficinamecanica.seguranca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private FuncionarioService funcionarioService;

    @BeforeEach
    void setUp() {
        funcionarioService = new FuncionarioService(funcionarioRepository, usuarioRepository, passwordEncoder);
        org.mockito.Mockito.lenient().when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
    }

    @Test
    void deveSalvarFuncionarioMecanico() {
        FuncionarioCadastroDTO dto = new FuncionarioCadastroDTO(
                "João Silva", "12345678900", "senha123",
                Cargo.MECANICO, Especialidade.MOTOR, "RF001"
        );

        Funcionario resultado = funcionarioService.salvar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCargo()).isEqualTo(Cargo.MECANICO);
        verify(funcionarioRepository).save(any(FuncionarioEntity.class));
        verify(usuarioRepository).save(any());
    }

    @Test
    void deveSalvarFuncionarioAtendente() {
        FuncionarioCadastroDTO dto = new FuncionarioCadastroDTO(
                "Maria Souza", "98765432100", "senha456",
                Cargo.ATENDENTE, null, "RF002"
        );

        Funcionario resultado = funcionarioService.salvar(dto);

        assertThat(resultado.getCargo()).isEqualTo(Cargo.ATENDENTE);
        verify(funcionarioRepository).save(any(FuncionarioEntity.class));
    }

    @Test
    void deveSalvarFuncionarioGerente() {
        FuncionarioCadastroDTO dto = new FuncionarioCadastroDTO(
                "Carlos Admin", "11122233300", "admin123",
                Cargo.GERENTE, null, "RF003"
        );

        Funcionario resultado = funcionarioService.salvar(dto);

        assertThat(resultado.getCargo()).isEqualTo(Cargo.GERENTE);
    }

    @Test
    void deveListarTodosOsFuncionarios() {
        when(funcionarioRepository.findAll()).thenReturn(List.of());

        List<Funcionario> resultado = funcionarioService.listarTodos();

        assertThat(resultado).isEmpty();
        verify(funcionarioRepository).findAll();
    }
}

