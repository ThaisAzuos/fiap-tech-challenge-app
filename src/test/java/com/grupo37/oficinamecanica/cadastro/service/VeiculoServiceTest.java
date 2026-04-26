package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.model.dto.VeiculoDTO;
import com.grupo37.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo37.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    @Test
    void deveSalvarVeiculoQuandoDadosForemValidos() {
        VeiculoDTO dto = new VeiculoDTO("ABC1D23", "Civic", "Honda", 2024, "Preto", "12345678901");
        Cliente dono = org.mockito.Mockito.mock(Cliente.class);

        when(veiculoRepository.existsById(dto.placa())).thenReturn(false);
        when(clienteRepository.findById(dto.cpfDono())).thenReturn(Optional.of(dono));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Veiculo salvo = veiculoService.salvarVeiculo(dto);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getPlaca()).isEqualTo("ABC1D23");
        assertThat(salvo.getModelo()).isEqualTo("Civic");
        assertThat(salvo.getMarca()).isEqualTo("Honda");
        assertThat(salvo.getAno()).isEqualTo(2024);
        assertThat(salvo.getCor()).isEqualTo("Preto");
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    void deveLancarExcecaoQuandoPlacaJaExistir() {
        VeiculoDTO dto = new VeiculoDTO("ABC1D23", "Civic", "Honda", 2024, "Preto", "12345678901");
        when(veiculoRepository.existsById(dto.placa())).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.salvarVeiculo(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Veículo com esta placa já está cadastrado.");
    }

    @Test
    void deveLancarExcecaoQuandoDonoNaoForEncontrado() {
        VeiculoDTO dto = new VeiculoDTO("ABC1D23", "Civic", "Honda", 2024, "Preto", "12345678901");
        when(veiculoRepository.existsById(dto.placa())).thenReturn(false);
        when(clienteRepository.findById(dto.cpfDono())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.salvarVeiculo(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cliente (Dono) não encontrado com o CPF: 12345678901");
    }

    @Test
    void deveListarVeiculosPorCpfDoDono() {
        String cpf = "12345678901";
        List<Veiculo> veiculos = List.of(org.mockito.Mockito.mock(Veiculo.class));
        when(clienteRepository.existsById(cpf)).thenReturn(true);
        when(veiculoRepository.findByDonoCpf(cpf)).thenReturn(veiculos);

        List<Veiculo> resultado = veiculoService.listarPorCpfDono(cpf);

        assertThat(resultado).hasSize(1).isEqualTo(veiculos);
        verify(clienteRepository).existsById(cpf);
        verify(veiculoRepository).findByDonoCpf(cpf);
    }
}

