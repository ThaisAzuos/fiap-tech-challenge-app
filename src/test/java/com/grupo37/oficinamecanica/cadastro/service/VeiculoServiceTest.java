package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.controller.dto.VeiculoDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.ClienteEntity;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

        ClienteEntity donoEntity = mock(ClienteEntity.class);
        Cliente dono = mock(Cliente.class);
        when(donoEntity.toDomain()).thenReturn(dono);

        VeiculoEntity veiculoEntity = mock(VeiculoEntity.class);
        Veiculo veiculoSalvo = mock(Veiculo.class);
        when(veiculoEntity.toDomain()).thenReturn(veiculoSalvo);
        when(veiculoSalvo.getPlaca()).thenReturn("ABC1D23");
        when(veiculoSalvo.getModelo()).thenReturn("Civic");
        when(veiculoSalvo.getMarca()).thenReturn("Honda");
        when(veiculoSalvo.getAno()).thenReturn(2024);
        when(veiculoSalvo.getCor()).thenReturn("Preto");

        when(veiculoRepository.existsById(dto.placa())).thenReturn(false);
        when(clienteRepository.findById("12345678901")).thenReturn(Optional.of(donoEntity));
        when(veiculoRepository.save(any(VeiculoEntity.class))).thenReturn(veiculoEntity);

        Veiculo salvo = veiculoService.salvarVeiculo(dto);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getPlaca()).isEqualTo("ABC1D23");
        assertThat(salvo.getModelo()).isEqualTo("Civic");
        assertThat(salvo.getMarca()).isEqualTo("Honda");
        assertThat(salvo.getAno()).isEqualTo(2024);
        assertThat(salvo.getCor()).isEqualTo("Preto");
        verify(veiculoRepository).save(any(VeiculoEntity.class));
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
        when(clienteRepository.findById("12345678901")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.salvarVeiculo(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cliente (Dono) não encontrado com o CPF: 12345678901");
    }

    @Test
    void deveListarVeiculosPorCpfDoDono() {
        String cpf = "12345678901";

        VeiculoEntity veiculoEntity = mock(VeiculoEntity.class);
        Veiculo veiculo = mock(Veiculo.class);
        when(veiculoEntity.toDomain()).thenReturn(veiculo);

        when(clienteRepository.existsById(cpf)).thenReturn(true);
        when(veiculoRepository.findByDonoCpf(cpf)).thenReturn(List.of(veiculoEntity));

        List<Veiculo> resultado = veiculoService.listarPorCpfDono(cpf);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(veiculo);
        verify(clienteRepository).existsById(cpf);
        verify(veiculoRepository).findByDonoCpf(cpf);
    }
}