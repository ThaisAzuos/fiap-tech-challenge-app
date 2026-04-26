package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.model.Placa;
import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.model.dto.VeiculoDTO;
import com.grupo37.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo37.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // 1. Validar se o veículo já existe pela placa
        if (veiculoRepository.existsById(dto.placa())) {
            throw new BusinessException("Veículo com esta placa já está cadastrado.");
        }

        // 2. Buscar o cliente (Dono) no banco. Se não existir, lança erro.
        // O CPF aqui é usado como ID do cliente conforme o seu repositório.
        String cpfDonoLimpo = dto.cpfDono().replaceAll("\\D", "");
        Cliente dono = clienteRepository.findById(cpfDonoLimpo)
                .orElseThrow(() -> new BusinessException("Cliente (Dono) não encontrado com o CPF: " + cpfDonoLimpo));

        // 3. Validar a placa através do Value Object (Garante o formato correto)
        Placa placaValidada = new Placa(dto.placa());

        // 4. Instanciar a entidade rica com o relacionamento
        Veiculo novoVeiculo = new Veiculo(
                placaValidada,
                dto.modelo(),
                dto.marca(),
                dto.ano(),
                dto.cor(),
                dono // Vincula o objeto Cliente completo ao Veículo
        );

        return veiculoRepository.save(novoVeiculo);
    }

    public List<Veiculo> listarPorCpfDono(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        log.info("Buscando veículos para o dono com CPF: {}", cpfLimpo);
        
        // Valida se o cliente existe antes de buscar carros
        if (!clienteRepository.existsById(cpfLimpo)) {
            throw new BusinessException("Cliente não encontrado com o CPF informado: " + cpfLimpo);
        }

        List<Veiculo> veiculos = veiculoRepository.findByDonoCpf(cpfLimpo);
        
        if (veiculos.isEmpty()) {
            throw new BusinessException("Nenhum veículo encontrado para o proprietário com CPF: " + cpfLimpo);
        }

        log.info("Encontrados {} veículos.", veiculos.size());
        return veiculos;
    }
}
