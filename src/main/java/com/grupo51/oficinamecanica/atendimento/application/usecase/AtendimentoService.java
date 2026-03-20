package com.grupo51.oficinamecanica.atendimento.application.usecase;

import com.grupo51.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo51.oficinamecanica.atendimento.application.dto.OrdemServicoDetalhesDTO;
import com.grupo51.oficinamecanica.atendimento.application.dto.OrdemServicoListDTO;
import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo51.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo51.oficinamecanica.atendimento.infrastructure.repository.OrdemServicoRepository;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo51.oficinamecanica.comum.email.model.EmailRequest;
import com.grupo51.oficinamecanica.comum.email.service.EmailService;
import com.grupo51.oficinamecanica.estoque.repository.PecaRepository;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@Service
public class AtendimentoService {

    private final OrdemServicoRepository osRepository;
    private final PecaRepository pecaRepository;
    private final VeiculoRepository veiculoRepository;
    private final EmailService emailService;

    // Construtor com injeção de dependências
    public AtendimentoService(OrdemServicoRepository osRepository, 
                            PecaRepository pecaRepository, 
                            VeiculoRepository veiculoRepository,
                            Optional<EmailService> emailService) {
        this.osRepository = osRepository;
        this.pecaRepository = pecaRepository;
        this.veiculoRepository = veiculoRepository;
        this.emailService = emailService.orElse(null);
    }

    @Transactional
    public OrdemServico abrirOrdem(AberturaOSDTO dto) {
        // 1. Busca o veículo pela placa
        Veiculo veiculo = veiculoRepository.findById(dto.placa())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado para abertura de OS."));

        // 2. Cria a nova OS usando construtor
        OrdemServico novaOs = new OrdemServico(veiculo, dto.descricaoProblema());

        // 3. Salva a OS
        OrdemServico osSalva = osRepository.save(novaOs);

        // 4. Enviar email de criação
        enviarEmailCriacaoOS(osSalva);

        return osSalva;
    }

    // ...existing code...

    @Transactional
    public void incluirPecaNaOS(UUID osId, UUID pecaId, int quantidade) {
        // 1. Busca a OS
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada com o ID: " + osId));

        // 2. Busca a peça no estoque
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new BusinessException("Peça não encontrada no estoque com o ID: " + pecaId));

        // 3. Adiciona na OS (a lógica de negócio está na entidade OS)
        os.adicionarPeca(peca, quantidade);

        // 4. Salva a alteração
        osRepository.save(os);
    }

    @Transactional(readOnly = true)
    public OrdemServicoDetalhesDTO consultarDetalhes(UUID osId) {
        OrdemServico os = osRepository.findByIdWithDetails(osId)
                .orElseThrow(() -> new BusinessException("Ordem de Serviço não encontrada."));

        List<OrdemServicoDetalhesDTO.ItemOSDTO> itensDTO = os.getItens().stream()
                .map(item -> new OrdemServicoDetalhesDTO.ItemOSDTO(
                        item.getNomePecaSnapshot(),
                        item.getQuantidade(),
                        item.getPrecoNoMomento(),
                        item.getPrecoNoMomento().multiply(BigDecimal.valueOf(item.getQuantidade()))
                )).toList();

        return new OrdemServicoDetalhesDTO(
                os.getId(),
                os.getStatus().name(),
                os.getVeiculo().getPlaca(),
                os.getVeiculo().getModelo(),
                os.getVeiculo().getDono().getNome(), // Aqui o JPA faz o join automaticamente
                os.getDescricaoProblema(),
                itensDTO,
                os.getValorTotal()
        );
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoListDTO> listarOrdensServico(Pageable pageable) {
        Page<OrdemServico> ordens = osRepository.findAllAtivas(pageable);
        return ordens.map(os -> new OrdemServicoListDTO(
                os.getId(),
                os.getStatus().name(),
                os.getDataAbertura(),
                os.getValorTotal(),
                os.getVeiculo().getPlaca(),
                os.getVeiculo().getModelo(),
                os.getVeiculo().getDono().getNome()
        ));
    }

    @Transactional
    public void aprovarOrcamento(UUID osId) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada."));

        if (os.getStatus() != StatusOS.AGUARDANDO_APROVACAO) {
            throw new BusinessException("A OS deve estar aguardando aprovação para ser aprovada.");
        }

        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        osRepository.save(os);

        // Enviar e-mail de notificação
        enviarEmailAtualizacaoStatus(os, "Seu orçamento foi aprovado! Iniciando execução do serviço.");
    }

    @Transactional
    public void atualizarStatus(UUID osId, StatusOS novoStatus) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada."));

        os.atualizarStatus(novoStatus);
        osRepository.save(os);

        // Enviar e-mail de notificação
        enviarEmailAtualizacaoStatus(os, "");
    }

    /**
     * Cancela uma ordem de serviço com motivo
     * @param osId ID da ordem de serviço
     * @param motivo Descrição do motivo do cancelamento
     */
    @Transactional
    public void cancelarOrdemServico(UUID osId, String motivo) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada."));

        os.cancelar(motivo);
        osRepository.save(os);

        // Enviar email de cancelamento
        enviarEmailCancelamento(os);

        log.info("Ordem de serviço {} cancelada com motivo: {}", osId, motivo);
    }

    /**
     * Marca uma ordem de serviço como concluída
     * @param osId ID da ordem de serviço
     */
    @Transactional
    public void concluirOrdemServico(UUID osId) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada."));

        if (os.getStatus() != StatusOS.EM_EXECUCAO) {
            throw new BusinessException("A OS deve estar em execução para ser finalizada.");
        }

        os.atualizarStatus(StatusOS.FINALIZADA);
        osRepository.save(os);

        // Enviar email de conclusão
        enviarEmailConclusao(os);

        log.info("Ordem de serviço {} marcada como concluída", osId);
    }


    /**
     * Envia email de criação de OS
     */
    private void enviarEmailCriacaoOS(OrdemServico os) {
        if (emailService == null || !emailService.isEmailEnabled()) {
            log.debug("EmailService não configurado, pulando notificação de criação");
            return;
        }

        try {
            String emailCliente = os.getVeiculo().getDono().getEmail();
            
            EmailRequest request = new EmailRequest(
                emailCliente,
                "Sua Ordem de Serviço #" + os.getId(),
                "email/ordem-servico-criada"
            );

            request.addVariable("ordemServicoId", os.getId().toString());
            request.addVariable("status", os.getStatus().name());
            request.addVariable("dataAbertura", os.getDataAbertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            request.addVariable("veiculo", os.getVeiculo().getModelo() + " " + os.getVeiculo().getMarca());
            request.addVariable("placa", os.getVeiculo().getPlaca());
            request.addVariable("descricaoProblema", os.getDescricaoProblema());

            boolean enviado = emailService.sendEmail(request);
            if (enviado) {
                log.info("Email de criação de OS enviado para: {}", emailCliente);
            } else {
                log.warn("Falha ao enviar email de criação de OS para: {}", emailCliente);
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email de criação de OS", e);
        }
    }

    /**
     * Envia email de atualização de status de OS
     */
    private void enviarEmailAtualizacaoStatus(OrdemServico os, String observacoes) {
        if (emailService == null || !emailService.isEmailEnabled()) {
            log.debug("EmailService não configurado, pulando notificação de atualização");
            return;
        }

        try {
            String emailCliente = os.getVeiculo().getDono().getEmail();
            
            EmailRequest request = new EmailRequest(
                emailCliente,
                "Atualização: Ordem de Serviço #" + os.getId(),
                "email/ordem-servico-atualizada"
            );

            request.addVariable("ordemServicoId", os.getId().toString());
            request.addVariable("novoStatus", traduirStatus(os.getStatus()));
            request.addVariable("dataAtualizacao", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            request.addVariable("veiculo", os.getVeiculo().getModelo() + " " + os.getVeiculo().getMarca());
            request.addVariable("placa", os.getVeiculo().getPlaca());
            if (!observacoes.isEmpty()) {
                request.addVariable("observacoes", observacoes);
            }

            boolean enviado = emailService.sendEmail(request);
            if (enviado) {
                log.info("Email de atualização de status enviado para: {}", emailCliente);
            } else {
                log.warn("Falha ao enviar email de atualização para: {}", emailCliente);
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email de atualização de status", e);
        }
    }

    /**
     * Traduz enum StatusOS para português
     */
    private String traduirStatus(StatusOS status) {
        return switch (status) {
            case AGUARDANDO_APROVACAO -> "Aguardando Aprovação";
            case EM_DIAGNOSTICO -> "Em Diagnóstico";
            case EM_EXECUCAO -> "Em Execução";
            case ENTREGUE -> "Entregue";
            case FINALIZADA -> "Finalizada";
            case RECEBIDA -> "Recebida";
            case CANCELADA -> "Cancelada";
        };
    }

    /**
     * Envia email de cancelamento de ordem de serviço
     */
    private void enviarEmailCancelamento(OrdemServico os) {
        if (emailService == null || !emailService.isEmailEnabled()) {
            log.debug("EmailService não configurado, pulando notificação de cancelamento");
            return;
        }

        try {
            String emailCliente = os.getVeiculo().getDono().getEmail();
            
            EmailRequest request = new EmailRequest(
                emailCliente,
                "Ordem de Serviço #" + os.getId() + " - CANCELADA",
                "email/ordem-servico-cancelada"
            );

            request.addVariable("ordemServicoId", os.getId().toString());
            request.addVariable("dataCancelamento", os.getDataCancelamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            request.addVariable("motivoCancelamento", os.getMotivoCancelamento());
            request.addVariable("veiculo", os.getVeiculo().getModelo() + " " + os.getVeiculo().getMarca());
            request.addVariable("placa", os.getVeiculo().getPlaca());

            boolean enviado = emailService.sendEmail(request);
            if (enviado) {
                log.info("Email de cancelamento enviado para: {}", emailCliente);
            } else {
                log.warn("Falha ao enviar email de cancelamento para: {}", emailCliente);
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email de cancelamento", e);
        }
    }

    /**
     * Envia email de conclusão de ordem de serviço
     */
    private void enviarEmailConclusao(OrdemServico os) {
        if (emailService == null || !emailService.isEmailEnabled()) {
            log.debug("EmailService não configurado, pulando notificação de conclusão");
            return;
        }

        try {
            String emailCliente = os.getVeiculo().getDono().getEmail();
            
            EmailRequest request = new EmailRequest(
                emailCliente,
                "Sua Ordem de Serviço #" + os.getId() + " foi Concluída!",
                "email/ordem-servico-concluida"
            );

            request.addVariable("ordemServicoId", os.getId().toString());
            request.addVariable("dataConclusao", os.getDataConclusao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            request.addVariable("veiculo", os.getVeiculo().getModelo() + " " + os.getVeiculo().getMarca());
            request.addVariable("placa", os.getVeiculo().getPlaca());
            request.addVariable("valorTotal", os.getValorTotal().toString());
            request.addVariable("quilometragem", "Consultar no sistema");
            request.addVariable("tempoServico", "Consultar no sistema");
            request.addVariable("pecasUtilizadas", os.getItens());
            request.addVariable("valorTotalPecas", os.getValorTotal().toString());
            request.addVariable("valorMaoObra", "0.00");
            request.addVariable("desconto", "0.00");
            request.addVariable("observacoesMecanico", "Serviço concluído.");

            boolean enviado = emailService.sendEmail(request);
            if (enviado) {
                log.info("Email de conclusão enviado para: {}", emailCliente);
            } else {
                log.warn("Falha ao enviar email de conclusão para: {}", emailCliente);
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email de conclusão", e);
        }
    }

}
