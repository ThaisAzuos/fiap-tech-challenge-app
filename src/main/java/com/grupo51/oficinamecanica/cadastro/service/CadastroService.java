package com.grupo51.oficinamecanica.cadastro.service;

import com.grupo51.oficinamecanica.cadastro.model.Cliente;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.model.dto.ClienteDTO;
import com.grupo51.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo51.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CadastroService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    public Cliente salvarCliente(ClienteDTO dto) {
        if (clienteRepository.existsById(dto.cpf())) {
            throw new BusinessException("Cliente já cadastrado com este CPF.");
        }

        Cliente novoCliente = new Cliente(
                dto.nome(), dto.cpf(), dto.email(), dto.telefone(), dto.endereco()
        );
        return clienteRepository.save(novoCliente);
    }

    public Veiculo salvarVeiculo(Veiculo veiculo) {
        if (veiculoRepository.existsById(veiculo.getPlaca())) {
            throw new BusinessException("Veículo com esta placa já existe no sistema.");
        }
        return veiculoRepository.save(veiculo);
    }
}
