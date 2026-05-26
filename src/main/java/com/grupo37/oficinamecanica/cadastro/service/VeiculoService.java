package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.controller.dto.VeiculoDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.domain.model.Placa;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
import com.grupo37.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo37.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;


    public VeiculoService(VeiculoRepository veiculoRepository, ClienteRepository clienteRepository) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public Veiculo salvarVeiculo(VeiculoDTO dto) {
        if (veiculoRepository.existsById(dto.placa())) {
            throw new BusinessException("Veículo com esta placa já está cadastrado.");
        }

        String cpfDonoLimpo = dto.cpfDono().replaceAll("\\D", "");

        // Busca como ClienteEntity e converte para domínio
        Cliente dono = clienteRepository.findById(cpfDonoLimpo)
                .orElseThrow(() -> new BusinessException("Cliente (Dono) não encontrado com o CPF: " + cpfDonoLimpo))
                .toDomain(); // <-- adiciona isso

        Placa placaValidada = new Placa(dto.placa());

        Veiculo novoVeiculo = new Veiculo(
                placaValidada,
                dto.modelo(),
                dto.marca(),
                dto.ano(),
                dto.cor(),
                dono
        );

        // Converte para entity, salva, e retorna como domínio
        return veiculoRepository.save(new VeiculoEntity(novoVeiculo)).toDomain(); // <-- ajusta isso
    }

    public List<Veiculo> listarPorCpfDono(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        log.info("Buscando veículos para o dono com CPF: {}", cpfLimpo);

        if (!clienteRepository.existsById(cpfLimpo)) {
            throw new BusinessException("Cliente não encontrado com o CPF informado: " + cpfLimpo);
        }

        List<Veiculo> veiculos = veiculoRepository.findByDonoCpf(cpfLimpo)
                .stream()
                .map(VeiculoEntity::toDomain)
                .collect(Collectors.toList());

        if (veiculos.isEmpty()) {
            throw new BusinessException("Nenhum veículo encontrado para o proprietário com CPF: " + cpfLimpo);
        }

        log.info("Encontrados {} veículos.", veiculos.size());
        return veiculos;
    }
}
