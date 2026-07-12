package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.Servico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoJpaAdapterTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoEntity servicoEntity;

    @Mock
    private Servico servico;

    private ServicoJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ServicoJpaAdapter(servicoRepository);
    }

    @Test
    void deveBuscarServicoPorId() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.findById(id)).thenReturn(Optional.of(servicoEntity));
        when(servicoEntity.toDomain()).thenReturn(servico);

        Optional<Servico> resultado = adapter.findById(id);

        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isSameAs(servico);
        verify(servicoRepository).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoServicoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Servico> resultado = adapter.findById(id);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveSalvarServico() {
        when(servicoRepository.save(any(ServicoEntity.class))).thenReturn(servicoEntity);
        when(servicoEntity.toDomain()).thenReturn(servico);

        Servico resultado = adapter.save(servico);

        assertThat(resultado).isSameAs(servico);
        verify(servicoRepository).save(any(ServicoEntity.class));
    }
}

