package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
import com.grupo37.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoJpaAdapterTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrdemServicoEntity ordemServicoEntity;

    @Mock
    private OrdemServico ordemServico;

    @Mock
    private Veiculo veiculo;

    @Mock
    private VeiculoEntity veiculoEntity;

    private OrdemServicoJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OrdemServicoJpaAdapter(ordemServicoRepository, veiculoRepository);
    }

    @Test
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        when(ordemServicoRepository.findById(id)).thenReturn(Optional.of(ordemServicoEntity));
        when(ordemServicoEntity.toDomain()).thenReturn(ordemServico);

        Optional<OrdemServico> resultado = adapter.findById(id);

        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isSameAs(ordemServico);
    }

    @Test
    void deveBuscarPorIdComDetalhes() {
        UUID id = UUID.randomUUID();
        when(ordemServicoRepository.findByIdWithDetails(id)).thenReturn(Optional.of(ordemServicoEntity));
        when(ordemServicoEntity.toDomain()).thenReturn(ordemServico);

        Optional<OrdemServico> resultado = adapter.findByIdWithDetails(id);

        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isSameAs(ordemServico);
    }

    @Test
    void deveListarAtivasPaginado() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<OrdemServicoEntity> pageEntity = new PageImpl<>(List.of(ordemServicoEntity), pageable, 1);
        when(ordemServicoRepository.findAllAtivas(pageable)).thenReturn(pageEntity);
        when(ordemServicoEntity.toDomain()).thenReturn(ordemServico);

        Page<OrdemServico> resultado = adapter.findAllAtivas(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isSameAs(ordemServico);
    }

    @Test
    void deveSalvarOrdemServico() {
        when(ordemServico.getVeiculo()).thenReturn(veiculo);
        when(veiculo.getPlaca()).thenReturn("ABC1D23");
        when(veiculoRepository.findById("ABC1D23")).thenReturn(Optional.of(veiculoEntity));
        when(ordemServicoRepository.save(any(OrdemServicoEntity.class))).thenReturn(ordemServicoEntity);
        when(ordemServicoEntity.toDomain()).thenReturn(ordemServico);

        OrdemServico resultado = adapter.save(ordemServico);

        assertThat(resultado).isSameAs(ordemServico);
        verify(veiculoRepository).findById("ABC1D23");
        verify(ordemServicoRepository).save(any(OrdemServicoEntity.class));
    }

    @Test
    void deveLancarErroQuandoVeiculoNaoExisteAoSalvar() {
        when(ordemServico.getVeiculo()).thenReturn(veiculo);
        when(veiculo.getPlaca()).thenReturn("AAA0000");
        when(veiculoRepository.findById("AAA0000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(ordemServico))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Veículo não encontrado");
    }
}

