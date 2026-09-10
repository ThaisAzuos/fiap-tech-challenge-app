package com.grupo37.oficinamecanica.atendimento.infrastructure.controller;

import com.grupo37.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.AberturaOSResponseDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.IncluirPecaDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.IncluirServicoDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.MensagemResponse;
import com.grupo37.oficinamecanica.atendimento.application.dto.OrdemServicoDetalhesDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.OrdemServicoListDTO;
import com.grupo37.oficinamecanica.atendimento.application.usecase.AtendimentoService;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.domain.model.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtendimentoControllerTest {

    @Mock
    private AtendimentoService atendimentoService;

    @Mock
    private OrdemServico ordemServico;

    private AtendimentoController controller;

    @BeforeEach
    void setUp() {
        controller = new AtendimentoController(atendimentoService);
    }

    @Test
    void deveAbrirOSERetornar201() {
        UUID osId = UUID.randomUUID();
        AberturaOSDTO dto = new AberturaOSDTO("ABC1D23", "Barulho na transmissão", List.of(), List.of());

        when(ordemServico.getId()).thenReturn(osId);
        when(ordemServico.getStatus()).thenReturn(StatusOS.RECEBIDA);
        when(ordemServico.getDataAbertura()).thenReturn(LocalDateTime.now());
        when(atendimentoService.abrirOrdem(any(AberturaOSDTO.class))).thenReturn(ordemServico);

        ResponseEntity<AberturaOSResponseDTO> response = controller.abrirOS(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(osId);
        assertThat(response.getBody().status()).isEqualTo("RECEBIDA");
        verify(atendimentoService).abrirOrdem(dto);
    }

    @Test
    void deveMudarStatusParaCancelada() {
        UUID osId = UUID.randomUUID();

        ResponseEntity<MensagemResponse> response = controller.mudarStatus(osId, StatusOS.CANCELADA);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().mensagem()).contains("cancelada");
        verify(atendimentoService).atualizarStatus(osId, StatusOS.CANCELADA);
    }

    @Test
    void deveMudarStatusParaOutro() {
        UUID osId = UUID.randomUUID();

        ResponseEntity<MensagemResponse> response = controller.mudarStatus(osId, StatusOS.EM_DIAGNOSTICO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().mensagem()).contains("EM_DIAGNOSTICO");
        verify(atendimentoService).atualizarStatus(osId, StatusOS.EM_DIAGNOSTICO);
    }

    @Test
    void deveBuscarDetalhesDaOS() {
        UUID osId = UUID.randomUUID();
        OrdemServicoDetalhesDTO dto = new OrdemServicoDetalhesDTO(
                osId,
                "RECEBIDA",
                "ABC1D23",
                "Civic",
                "Joao",
                "Problema no freio",
                List.of(),
                List.of(),
                BigDecimal.ZERO
        );
        when(atendimentoService.consultarDetalhes(osId)).thenReturn(dto);

        ResponseEntity<OrdemServicoDetalhesDTO> response = controller.buscarDetalhes(osId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(osId);
        verify(atendimentoService).consultarDetalhes(osId);
    }

    @Test
    void deveIncluirPecaNaOS() {
        UUID osId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        IncluirPecaDTO dto = new IncluirPecaDTO(pecaId, 2);

        ResponseEntity<Void> response = controller.incluirPeca(osId, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(atendimentoService).incluirPecaNaOS(osId, pecaId, 2);
    }

    @Test
    void deveIncluirServicoNaOS() {
        UUID osId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        IncluirServicoDTO dto = new IncluirServicoDTO(servicoId);

        ResponseEntity<Void> response = controller.incluirServico(osId, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(atendimentoService).incluirServicoNaOS(osId, servicoId);
    }

    @Test
    void deveAprovarOrcamento() {
        UUID osId = UUID.randomUUID();

        ResponseEntity<Void> response = controller.aprovarOrcamento(osId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(atendimentoService).aprovarOrcamento(osId);
    }

    @Test
    void deveListarOrdensServicoPaginadas() {
        PageRequest pageable = PageRequest.of(0, 10);
        OrdemServicoListDTO item = new OrdemServicoListDTO(
                UUID.randomUUID(),
                "RECEBIDA",
                LocalDateTime.now(),
                BigDecimal.ZERO,
                "ABC1D23",
                "Civic",
                "Joao"
        );
        Page<OrdemServicoListDTO> page = new PageImpl<>(List.of(item), pageable, 1);
        when(atendimentoService.listarOrdensServico(pageable)).thenReturn(page);

        ResponseEntity<Page<OrdemServicoListDTO>> response = controller.listarOS(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(atendimentoService).listarOrdensServico(pageable);
    }
}

