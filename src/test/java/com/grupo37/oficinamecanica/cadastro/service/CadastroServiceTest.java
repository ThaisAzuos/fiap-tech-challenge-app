package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.controller.dto.ClienteDTO;
import com.grupo37.oficinamecanica.cadastro.controller.dto.EnderecoDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.ClienteEntity;
import com.grupo37.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private CadastroService cadastroService;

    private EnderecoDTO endereco;

    @BeforeEach
    void setUp() {
        cadastroService = new CadastroService();
        // Injeta via reflection porque usa @Autowired
        try {
            var field = CadastroService.class.getDeclaredField("clienteRepository");
            field.setAccessible(true);
            field.set(cadastroService, clienteRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        endereco = new EnderecoDTO("Av. Paulista", "1000", "Apto 201",
                "Bela Vista", "São Paulo", "SP", "01310100");
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        ClienteDTO dto = new ClienteDTO("João Silva", "52998224725",
                "joao@email.com", "11987654321", endereco);
        when(clienteRepository.existsById("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> cadastroService.salvarCliente(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já cadastrado");
    }

    @Test
    void deveSalvarClienteComSucesso() {
        ClienteDTO dto = new ClienteDTO("Maria Souza", "52998224725",
                "maria@email.com", "11987654321", endereco);
        when(clienteRepository.existsById("52998224725")).thenReturn(false);
        when(clienteRepository.save(any(ClienteEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = cadastroService.salvarCliente(dto);

        assertThat(resultado).isNotNull();
        verify(clienteRepository).save(any(ClienteEntity.class));
    }

    @Test
    void deveListarTodosOsClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        List<Cliente> resultado = cadastroService.listarTodos();

        assertThat(resultado).isEmpty();
        verify(clienteRepository).findAll();
    }
}

