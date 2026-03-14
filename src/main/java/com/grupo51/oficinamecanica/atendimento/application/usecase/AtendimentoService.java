package com.grupo51.oficinamecanica.atendimento.application.usecase;

import com.grupo51.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo51.oficinamecanica.atendimento.application.dto.OrdemServicoDetalhesDTO;
import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo51.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo51.oficinamecanica.atendimento.infrastructure.repository.OrdemServicoRepository;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo51.oficinamecanica.estoque.repository.PecaRepository;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AtendimentoService {

    private final OrdemServicoRepository osRepository;
    private final PecaRepository pecaRepository;
    private final VeiculoRepository veiculoRepository;

    // Construtor explícito
    public AtendimentoService(OrdemServicoRepository osRepository, PecaRepository pecaRepository, VeiculoRepository veiculoRepository) {
        this.osRepository = osRepository;
        this.pecaRepository = pecaRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public OrdemServico abrirOrdem(AberturaOSDTO dto) {
        // 1. Busca o veículo pela placa
        Veiculo veiculo = veiculoRepository.findById(dto.placa())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado para abertura de OS."));

        // 2. Cria a nova OS usando construtor
        OrdemServico novaOs = new OrdemServico(veiculo, dto.descricaoProblema());

        return osRepository.save(novaOs);
    }

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

    @Transactional
    public void atualizarStatus(UUID osId, StatusOS novoStatus) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada."));

        os.atualizarStatus(novoStatus);
        osRepository.save(os);
    }
}
