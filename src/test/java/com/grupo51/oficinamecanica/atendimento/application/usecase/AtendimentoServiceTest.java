package com.grupo51.oficinamecanica.atendimento.application.usecase;

import com.grupo51.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo51.oficinamecanica.atendimento.application.dto.OrdemServicoDetalhesDTO;
import com.grupo51.oficinamecanica.atendimento.application.dto.OrdemServicoListDTO;
import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo51.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo51.oficinamecanica.atendimento.infrastructure.repository.OrdemServicoRepository;
import com.grupo51.oficinamecanica.cadastro.model.Cliente;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import com.grupo51.oficinamecanica.estoque.repository.PecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private OrdemServicoRepository osRepository;

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private VeiculoRepository veiculoRepository;


    private AtendimentoService atendimentoService;

    private Veiculo veiculo;
    private Cliente cliente;
    private Peca peca;
    private OrdemServico ordemServico;
    private UUID osId;
    private UUID pecaId;

    @BeforeEach
    void setUp() {
        atendimentoService = new AtendimentoService(osRepository, pecaRepository, veiculoRepository, Optional.empty());

        // Criação de mocks das dependências
        cliente = mock(Cliente.class);
        lenient().when(cliente.getNome()).thenReturn("João Silva");

        veiculo = mock(Veiculo.class);
        lenient().when(veiculo.getPlaca()).thenReturn("ABC1234");
        lenient().when(veiculo.getModelo()).thenReturn("Fiat Uno");
        lenient().when(veiculo.getDono()).thenReturn(cliente);

        peca = mock(Peca.class);
        lenient().when(peca.getId()).thenReturn(UUID.randomUUID());
        lenient().when(peca.getNome()).thenReturn("Pastilha de Freio");
        lenient().when(peca.getPreco()).thenReturn(BigDecimal.valueOf(150.00));

        osId = UUID.randomUUID();
        pecaId = peca.getId();

        ordemServico = new OrdemServico(veiculo, "Problema no freio");
        ordemServico.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
    }

    @Test
    void deveAbrirOrdemServicoComSucesso() {
        // Dado
        AberturaOSDTO dto = new AberturaOSDTO("ABC1234", "Problema no freio");
        when(veiculoRepository.findById("ABC1234")).thenReturn(Optional.of(veiculo));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Quando
        OrdemServico result = atendimentoService.abrirOrdem(dto);

        // Então
        assertThat(result).isNotNull();
        assertThat(result.getVeiculo()).isEqualTo(veiculo);
        assertThat(result.getDescricaoProblema()).isEqualTo("Problema no freio");
        assertThat(result.getStatus()).isEqualTo(StatusOS.RECEBIDA);
        verify(osRepository).save(any(OrdemServico.class));
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Dado
        AberturaOSDTO dto = new AberturaOSDTO("PLACA_INVALIDA", "Problema");
        when(veiculoRepository.findById("PLACA_INVALIDA")).thenReturn(Optional.empty());

        // Quando/Então
        assertThatThrownBy(() -> atendimentoService.abrirOrdem(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Veículo não encontrado para abertura de OS.");
    }

    @Test
    void deveIncluirPecaNaOrdemServicoComSucesso() {
        // Dado
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Quando
        atendimentoService.incluirPecaNaOS(osId, pecaId, 2);

        // Então
        verify(osRepository).save(ordemServico);
        assertThat(ordemServico.getValorTotal()).isEqualTo(BigDecimal.valueOf(300.00)); // 150 * 2
    }

    @Test
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontradaParaIncluirPeca() {
        // Dado
        when(osRepository.findById(osId)).thenReturn(Optional.empty());

        // Quando/Então
        assertThatThrownBy(() -> atendimentoService.incluirPecaNaOS(osId, pecaId, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("OS não encontrada com o ID: " + osId);
    }

    @Test
    void deveLancarExcecaoQuandoPecaNaoEncontrada() {
        // Dado
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.empty());

        // Quando/Então
        assertThatThrownBy(() -> atendimentoService.incluirPecaNaOS(osId, pecaId, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Peça não encontrada no estoque com o ID: " + pecaId);
    }

    @Test
    void deveConsultarDetalhesDaOrdemServico() {
        // Dado
        ordemServico.adicionarPeca(peca, 1);
        when(osRepository.findByIdWithDetails(osId)).thenReturn(Optional.of(ordemServico));

        // Quando
        OrdemServicoDetalhesDTO result = atendimentoService.consultarDetalhes(osId);

        // Então
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ordemServico.getId());
        assertThat(result.status()).isEqualTo("EM_DIAGNOSTICO");
        assertThat(result.placaVeiculo()).isEqualTo("ABC1234");
        assertThat(result.modeloVeiculo()).isEqualTo("Fiat Uno");
        assertThat(result.nomeCliente()).isEqualTo("João Silva");
        assertThat(result.descricaoProblema()).isEqualTo("Problema no freio");
        assertThat(result.itens()).hasSize(1);
        assertThat(result.valorTotal()).isEqualTo(BigDecimal.valueOf(150.00));
    }

    @Test
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontradaParaConsulta() {
        // Dado
        when(osRepository.findByIdWithDetails(osId)).thenReturn(Optional.empty());

        // Quando/Então
        assertThatThrownBy(() -> atendimentoService.consultarDetalhes(osId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ordem de Serviço não encontrada.");
    }

    @Test
    void deveListarOrdensServicoAtivasComPaginacao() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);
        List<OrdemServico> ordens = List.of(ordemServico);
        Page<OrdemServico> pageOrdens = new PageImpl<>(ordens, pageable, 1);

        when(osRepository.findAllAtivas(pageable)).thenReturn(pageOrdens);

        // Quando
        Page<OrdemServicoListDTO> result = atendimentoService.listarOrdensServico(pageable);

        // Então
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        OrdemServicoListDTO dto = result.getContent().get(0);
        assertThat(dto.id()).isEqualTo(ordemServico.getId());
        assertThat(dto.status()).isEqualTo("EM_DIAGNOSTICO");
        assertThat(dto.placaVeiculo()).isEqualTo("ABC1234");
        assertThat(dto.modeloVeiculo()).isEqualTo("Fiat Uno");
        assertThat(dto.nomeCliente()).isEqualTo("João Silva");
    }

    @Test
    void deveAprovarOrcamentoComSucesso() {
        // Dado
        ordemServico.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Quando
        atendimentoService.aprovarOrcamento(osId);

        // Então
        assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.EM_EXECUCAO);
        verify(osRepository).save(ordemServico);
    }

    @Test
    void deveLancarExcecaoQuandoTentarAprovarOrcamentoForaDoStatusCorreto() {
        // Dado - OS ainda em EM_DIAGNOSTICO
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));

        // Quando/Então
        assertThatThrownBy(() -> atendimentoService.aprovarOrcamento(osId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A OS deve estar aguardando aprovação para ser aprovada.");
    }

    @Test
    void deveAtualizarStatusDaOrdemServico() {
        // Dado
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Quando
        atendimentoService.atualizarStatus(osId, StatusOS.AGUARDANDO_APROVACAO);

        // Então
        assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);
        verify(osRepository).save(ordemServico);
    }
}
