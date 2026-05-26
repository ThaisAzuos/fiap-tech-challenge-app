package com.grupo37.oficinamecanica.atendimento.application.usecase;

import com.grupo37.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.OrdemServicoDetalhesDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.OrdemServicoListDTO;
import com.grupo37.oficinamecanica.atendimento.application.port.out.OrdemServicoPort;
import com.grupo37.oficinamecanica.atendimento.application.port.out.PecaPort;
import com.grupo37.oficinamecanica.atendimento.application.port.out.VeiculoPort;
import com.grupo37.oficinamecanica.atendimento.application.port.out.ServicoPort;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo37.oficinamecanica.atendimento.domain.model.Servico;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.comum.email.model.EmailRequest;
import com.grupo37.oficinamecanica.comum.email.service.EmailService;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import com.grupo37.oficinamecanica.estoque.model.Peca;
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

    private final OrdemServicoPort ordemServicoPort;
    private final PecaPort pecaPort;
    private final VeiculoPort veiculoPort;
    private final ServicoPort servicoPort;
    private final EmailService emailService;

    // Construtor com injeção de dependências
    public AtendimentoService(OrdemServicoPort ordemServicoPort,
                            PecaPort pecaPort,
                            VeiculoPort veiculoPort,
                            ServicoPort servicoPort,
                            Optional<EmailService> emailService) {
        this.ordemServicoPort = ordemServicoPort;
        this.pecaPort = pecaPort;
        this.veiculoPort = veiculoPort;
        this.servicoPort = servicoPort;
        this.emailService = emailService.orElse(null);
        
        if (this.emailService != null) {
            log.info("AtendimentoService inicializado com EmailService habilitado? {}", this.emailService.isEmailEnabled());
        } else {
            log.warn("AtendimentoService inicializado SEM EmailService (bean opcional vazio). Verifique a propriedade spring.mail.host");
        }
    }

    @Transactional
    public OrdemServico abrirOrdem(AberturaOSDTO dto) {
        // 1. Busca o veículo pela placa
        Veiculo veiculo = veiculoPort.findById(dto.placa())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado para abertura de OS: " + dto.placa()));

        // 2. Cria a nova OS usando construtor
        OrdemServico novaOs = new OrdemServico(veiculo, dto.descricaoProblema());

        // 3. Salva a OS
        OrdemServico osSalva = ordemServicoPort.save(novaOs);

        // 4. Adiciona peças se fornecidas
        if (dto.pecas() != null && !dto.pecas().isEmpty()) {
            for (var pecaDto : dto.pecas()) {
                // Busca a peça no estoque
                Peca peca = pecaPort.findById(pecaDto.pecaId())
                        .orElseThrow(() -> new BusinessException("Peça não encontrada no estoque com o ID: " + pecaDto.pecaId()));

                // Adiciona na OS
                osSalva.adicionarPeca(peca, pecaDto.quantidade());
            }
            // Salva novamente com as peças
            osSalva = ordemServicoPort.save(osSalva);
        }

        // 5. Adiciona serviços se fornecidos
        if (dto.servicos() != null && !dto.servicos().isEmpty()) {
            for (var servicoDto : dto.servicos()) {
                // Busca o serviço
                Servico servico = servicoPort.findById(servicoDto.servicoId())
                        .orElseThrow(() -> new BusinessException("Serviço não encontrado com o ID: " + servicoDto.servicoId()));

                // Adiciona na OS
                osSalva.adicionarServico(servico);
            }
            // Salva novamente com os serviços
            osSalva = ordemServicoPort.save(osSalva);
        }

        // 6. Enviar email de criação
        enviarEmailCriacaoOS(osSalva);

        return osSalva;
    }


    @Transactional
    public void incluirPecaNaOS(UUID osId, UUID pecaId, int quantidade) {
        // 1. Busca a OS
        OrdemServico os = ordemServicoPort.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada com o ID: " + osId));

        // 2. Busca a peça no estoque
        Peca peca = pecaPort.findById(pecaId)
                .orElseThrow(() -> new BusinessException("Peça não encontrada no estoque com o ID: " + pecaId));

        // 3. Adiciona na OS (a lógica de negócio está na entidade OS)
        os.adicionarPeca(peca, quantidade);

        // 4. Salva a alteração
        ordemServicoPort.save(os);
    }

    @Transactional
    public void incluirServicoNaOS(UUID osId, UUID servicoId) {
        // 1. Busca a OS
        OrdemServico os = ordemServicoPort.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada com o ID: " + osId));

        // 2. Busca o serviço
        Servico servico = servicoPort.findById(servicoId)
                .orElseThrow(() -> new BusinessException("Serviço não encontrado com o ID: " + servicoId));

        // 3. Adiciona na OS (a lógica de negócio está na entidade OS)
        os.adicionarServico(servico);

        // 4. Salva a alteração
        ordemServicoPort.save(os);
    }

    @Transactional(readOnly = true)
    public OrdemServicoDetalhesDTO consultarDetalhes(UUID osId) {
        OrdemServico os = ordemServicoPort.findByIdWithDetails(osId)
                .orElseThrow(() -> new BusinessException("Ordem de Serviço não encontrada com o ID: " + osId));

        List<OrdemServicoDetalhesDTO.ItemOSDTO> itensDTO = os.getItens().stream()
                .map(item -> new OrdemServicoDetalhesDTO.ItemOSDTO(
                        item.getNomePecaSnapshot(),
                        item.getQuantidade(),
                        item.getPrecoNoMomento(),
                        item.getPrecoNoMomento().multiply(BigDecimal.valueOf(item.getQuantidade()))
                )).toList();

        List<OrdemServicoDetalhesDTO.ItemServicoOSDTO> servicosDTO = os.getServicos().stream()
                .map(servico -> new OrdemServicoDetalhesDTO.ItemServicoOSDTO(
                        servico.getNomeServicoSnapshot(),
                        servico.getPrecoNoMomento()
                )).toList();

        return new OrdemServicoDetalhesDTO(
                os.getId(),
                os.getStatus().name(),
                os.getVeiculo().getPlaca(),
                os.getVeiculo().getModelo(),
                os.getVeiculo().getDono().getNome(), // Aqui o JPA faz o join automaticamente
                os.getDescricaoProblema(),
                itensDTO,
                servicosDTO,
                os.getValorTotal()
        );
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoListDTO> listarOrdensServico(Pageable pageable) {
        Page<OrdemServico> ordens = ordemServicoPort.findAllAtivas(pageable);
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
        OrdemServico os = ordemServicoPort.findById(osId)
                .orElseThrow(() -> new BusinessException("Ordem de Serviço não encontrada com o ID: " + osId));

        if (os.getStatus() != StatusOS.AGUARDANDO_APROVACAO) {
            throw new BusinessException("A OS " + osId + " deve estar aguardando aprovação para ser aprovada.");
        }

        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        ordemServicoPort.save(os);

        // Enviar e-mail de notificação
        enviarEmailAtualizacaoStatus(os, "Seu orçamento foi aprovado! Iniciando execução do serviço.");
    }

    @Transactional
    public void atualizarStatus(UUID osId, StatusOS novoStatus) {
        OrdemServico os = ordemServicoPort.findById(osId)
                .orElseThrow(() -> new BusinessException("Ordem de Serviço não encontrada com o ID: " + osId));

        os.atualizarStatus(novoStatus);
        ordemServicoPort.save(os);

        // Verifica se é um cancelamento para enviar o email correto
        if (novoStatus == StatusOS.CANCELADA) {
            enviarEmailCancelamento(os);
            log.info("Ordem de serviço {} cancelada com sucesso via atualização de status.", osId);
        } else if (novoStatus == StatusOS.AGUARDANDO_APROVACAO) {
            enviarEmailAguardandoAprovacao(os);
        } else if (novoStatus == StatusOS.FINALIZADA) {
            enviarEmailConclusao(os);
        } else {
            // Enviar e-mail de notificação de atualização padrão
            enviarEmailAtualizacaoStatus(os, "");
        }
    }

    /**
     * Cancela uma ordem de serviço com motivo
     * @param osId ID da ordem de serviço
     * @param motivo Descrição do motivo do cancelamento
     */
    @Transactional
    public void cancelarOrdemServico(UUID osId, String motivo) {
        OrdemServico os = ordemServicoPort.findById(osId)
                .orElseThrow(() -> new BusinessException("Ordem de Serviço não encontrada com o ID: " + osId));

        os.cancelar(motivo);
        ordemServicoPort.save(os);

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
        OrdemServico os = ordemServicoPort.findById(osId)
                .orElseThrow(() -> new BusinessException("Ordem de Serviço não encontrada com o ID: " + osId));

        if (os.getStatus() != StatusOS.EM_EXECUCAO) {
            throw new BusinessException("A OS " + osId + " deve estar em execução para ser finalizada.");
        }

        os.atualizarStatus(StatusOS.FINALIZADA);
        ordemServicoPort.save(os);

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
        log.info("Tentando enviar email de atualização de status para OS: {}", os.getId());
        
        if (emailService == null) {
            log.warn("EmailService é NULL!");
            return;
        }
        
        if (!emailService.isEmailEnabled()) {
            log.warn("EmailService reporta isEmailEnabled() = false");
            return;
        }

        try {
            String emailCliente = os.getVeiculo().getDono().getEmail();
            log.info("Destinatário: {}", emailCliente);
            
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
            request.addVariable("dataCancelamento", os.getDataCancelamento() != null 
                ? os.getDataCancelamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            String motivo = os.getMotivoCancelamento();
            if (motivo == null || motivo.isEmpty()) {
                motivo = "Cancelado via atualização de status (sem motivo especificado)";
            }
            request.addVariable("motivoCancelamento", motivo);
            
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
            request.addVariable("dataConclusao", os.getDataConclusao() != null 
                ? os.getDataConclusao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            request.addVariable("veiculo", os.getVeiculo().getModelo() + " " + os.getVeiculo().getMarca());
            request.addVariable("placa", os.getVeiculo().getPlaca());
            request.addVariable("valorTotal", os.getValorTotal() != null ? os.getValorTotal().toString() : "0.00");
            request.addVariable("quilometragem", "Consultar no sistema");
            request.addVariable("tempoServico", "Consultar no sistema");
            request.addVariable("pecasUtilizadas", os.getItens());
            request.addVariable("valorTotalPecas", os.getValorTotal() != null ? os.getValorTotal().toString() : "0.00");
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

    /**
     * Envia email de aguardando aprovação de orçamento
     */
    private void enviarEmailAguardandoAprovacao(OrdemServico os) {
        if (emailService == null || !emailService.isEmailEnabled()) {
            log.debug("EmailService não configurado, pulando notificação de aprovação");
            return;
        }

        try {
            String emailCliente = os.getVeiculo().getDono().getEmail();

            EmailRequest request = new EmailRequest(
                emailCliente,
                "Aprovação de Orçamento - Ordem de Serviço #" + os.getId(),
                "email/ordem-servico-aprovacao"
            );

            // Link para aprovação pública (em produção, seria configurável)
            String baseUrl = "http://localhost:8080"; // TODO: Tornar configurável via properties
            String linkAprovacao = baseUrl + "/api/public/atendimento/" + os.getId() + "/aprovacao";

            request.addVariable("ordemServicoId", os.getId().toString());
            request.addVariable("veiculo", os.getVeiculo().getModelo() + " " + os.getVeiculo().getMarca());
            request.addVariable("placa", os.getVeiculo().getPlaca());
            request.addVariable("valorTotal", "R$ " + (os.getValorTotal() != null ? os.getValorTotal().toString() : "0.00"));
            request.addVariable("dataOrcamento", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            request.addVariable("linkAprovacao", linkAprovacao);

            boolean enviado = emailService.sendEmail(request);
            if (enviado) {
                log.info("Email de aguardando aprovação enviado para: {}", emailCliente);
            } else {
                log.warn("Falha ao enviar email de aguardando aprovação para: {}", emailCliente);
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email de aguardando aprovação", e);
        }
    }
}
