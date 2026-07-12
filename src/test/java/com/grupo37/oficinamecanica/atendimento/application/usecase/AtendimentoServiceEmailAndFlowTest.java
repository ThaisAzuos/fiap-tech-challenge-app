package com.grupo37.oficinamecanica.atendimento.application.usecase;

import com.grupo37.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.IncluirPecaDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.IncluirServicoDTO;
import com.grupo37.oficinamecanica.atendimento.application.port.out.OrdemServicoPort;
import com.grupo37.oficinamecanica.atendimento.application.port.out.PecaPort;
import com.grupo37.oficinamecanica.atendimento.application.port.out.ServicoPort;
import com.grupo37.oficinamecanica.atendimento.application.port.out.VeiculoPort;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.domain.model.Servico;
import com.grupo37.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.OrdemServicoEventPublisher;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.comum.email.service.EmailService;
import com.grupo37.oficinamecanica.estoque.model.Peca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceEmailAndFlowTest {

    @Mock
    private OrdemServicoPort ordemServicoPort;
    @Mock
    private PecaPort pecaPort;
    @Mock
    private VeiculoPort veiculoPort;
    @Mock
    private ServicoPort servicoPort;
    @Mock
    private EmailService emailService;
    @Mock
    private OrdemServicoEventPublisher eventPublisher;

    private AtendimentoService atendimentoService;

    private Veiculo veiculo;
    private Cliente cliente;
    private Peca peca;
    private Servico servico;

    @BeforeEach
    void setUp() {
        atendimentoService = new AtendimentoService(
                ordemServicoPort,
                pecaPort,
                veiculoPort,
                servicoPort,
                Optional.of(emailService),
                eventPublisher
        );

        cliente = org.mockito.Mockito.mock(Cliente.class);
        lenient().when(cliente.getNome()).thenReturn("Cliente Teste");
        lenient().when(cliente.getEmail()).thenReturn("cliente@test.com");

        veiculo = org.mockito.Mockito.mock(Veiculo.class);
        lenient().when(veiculo.getPlaca()).thenReturn("ABC1D23");
        lenient().when(veiculo.getModelo()).thenReturn("Civic");
        lenient().when(veiculo.getMarca()).thenReturn("Honda");
        lenient().when(veiculo.getDono()).thenReturn(cliente);

        peca = org.mockito.Mockito.mock(Peca.class);
        lenient().when(peca.getId()).thenReturn(UUID.randomUUID());
        lenient().when(peca.getNome()).thenReturn("Filtro");
        lenient().when(peca.getPreco()).thenReturn(BigDecimal.valueOf(50));

        servico = org.mockito.Mockito.mock(Servico.class);
        lenient().when(servico.getId()).thenReturn(UUID.randomUUID());
        lenient().when(servico.getNome()).thenReturn("Revisão");
        lenient().when(servico.getPreco()).thenReturn(BigDecimal.valueOf(200));

        lenient().when(emailService.isEmailEnabled()).thenReturn(true);
        lenient().when(emailService.sendEmail(any())).thenReturn(true);
    }

    private OrdemServico ordemComId(UUID id, StatusOS statusInicial, String descricao) {
        return new OrdemServico(
                id,
                veiculo,
                descricao,
                LocalDateTime.now(),
                statusInicial,
                new ArrayList<>(),
                new ArrayList<>(),
                BigDecimal.ZERO,
                null,
                null,
                null
        );
    }

    @Test
    void deveAbrirOrdemComPecasEServicosEnviarEmailEPublicarEvento() {
        UUID pecaId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        when(peca.getId()).thenReturn(pecaId);
        when(servico.getId()).thenReturn(servicoId);

        AberturaOSDTO dto = new AberturaOSDTO(
                "ABC1D23",
                "Problema no veículo em teste",
                List.of(new IncluirPecaDTO(pecaId, 2)),
                List.of(new IncluirServicoDTO(servicoId))
        );

        when(veiculoPort.findById("ABC1D23")).thenReturn(Optional.of(veiculo));
        when(pecaPort.findById(pecaId)).thenReturn(Optional.of(peca));
        when(servicoPort.findById(servicoId)).thenReturn(Optional.of(servico));
        OrdemServico osPersistida = ordemComId(UUID.randomUUID(), StatusOS.RECEBIDA, "Problema no veículo em teste");
        when(ordemServicoPort.save(any(OrdemServico.class))).thenReturn(osPersistida);

        atendimentoService.abrirOrdem(dto);

        verify(emailService).sendEmail(any());
        verify(eventPublisher).publicarOrdemServicoCriada(any(OrdemServico.class));
        verify(ordemServicoPort, org.mockito.Mockito.atLeast(2)).save(any(OrdemServico.class));
    }

    @Test
    void deveAtualizarStatusParaAguardandoAprovacaoEEnviarEmail() {
        UUID osId = UUID.randomUUID();
        OrdemServico os = ordemComId(osId, StatusOS.EM_DIAGNOSTICO, "Diagnóstico");

        when(ordemServicoPort.findById(osId)).thenReturn(Optional.of(os));
        when(ordemServicoPort.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        atendimentoService.atualizarStatus(osId, StatusOS.AGUARDANDO_APROVACAO);

        verify(emailService).sendEmail(any());
    }

    @Test
    void deveAtualizarStatusParaFinalizadaEEnviarEmail() {
        UUID osId = UUID.randomUUID();
        OrdemServico os = ordemComId(osId, StatusOS.EM_DIAGNOSTICO, "Execução");
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);

        when(ordemServicoPort.findById(osId)).thenReturn(Optional.of(os));
        when(ordemServicoPort.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        atendimentoService.atualizarStatus(osId, StatusOS.FINALIZADA);

        verify(emailService).sendEmail(any());
    }

    @Test
    void deveAtualizarStatusParaCanceladaEEnviarEmail() {
        UUID osId = UUID.randomUUID();
        OrdemServico os = ordemComId(osId, StatusOS.RECEBIDA, "Cancelamento");

        when(ordemServicoPort.findById(osId)).thenReturn(Optional.of(os));
        when(ordemServicoPort.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        atendimentoService.atualizarStatus(osId, StatusOS.CANCELADA);

        verify(emailService).sendEmail(any());
    }
}

