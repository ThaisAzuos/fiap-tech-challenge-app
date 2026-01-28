package com.grupo51.oficinamecanica.estoque.service;

import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.controller.PecaDTO;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import com.grupo51.oficinamecanica.estoque.repository.PecaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PecaService {

    private final PecaRepository pecaRepository;

    public PecaService(PecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    @Transactional
    public Peca salvarPeca(PecaDTO dto) {

        if (dto.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O preço da peça deve ser maior que zero.");
        }

        Peca novaPeca = new Peca(
                dto.nome(),
                dto.preco(),
                dto.quantidadeEstoque()
        );

        return pecaRepository.save(novaPeca);
    }

    public List<Peca> listarTodas() {
        return pecaRepository.findAll();
    }
}
