package com.grupo51.oficinamecanica.atendimento.service;

import com.grupo51.oficinamecanica.atendimento.model.OrdemDeServico;
import com.grupo51.oficinamecanica.atendimento.repository.OrdemDeServicoRepository;
import com.grupo51.oficinamecanica.atendimento.repository.PecaRepository;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtendimentoService {

    @Autowired
    private OrdemDeServicoRepository osRepository;
    @Autowired
    private PecaRepository pecaRepository;

    @Transactional
    public void incluirPecaNaOS(UUID osId, UUID pecaId, int quantidade) {
        // 1. Busca a OS
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new BusinessException("OS não encontrada"));

        // 2. Busca a peça no estoque
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new BusinessException("Peça não encontrada no estoque"));

        // 3. Adiciona na OS (a lógica de negócio está na entidade OS)
        os.adicionarPeca(peca, quantidade);

        // 4. Salva a alteração
        osRepository.save(os);
    }
}
