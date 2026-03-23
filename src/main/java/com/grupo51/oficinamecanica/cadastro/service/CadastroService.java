package com.grupo51.oficinamecanica.cadastro.service;

import com.grupo51.oficinamecanica.cadastro.model.*;
import com.grupo51.oficinamecanica.cadastro.model.dto.ClienteDTO;
import com.grupo51.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CadastroService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente salvarCliente(ClienteDTO dto) {
        if (clienteRepository.existsById(dto.cpf())) {
            throw new BusinessException("Cliente já cadastrado com este CPF.");
        }

        // 1. Converter Strings/DTOs para Objetos de Valor (Aqui dispara as validações)
        Cpf cpfValidado = new Cpf(dto.cpf());
        Email emailValidado = new Email(dto.email());

        // 2. Converter EnderecoDTO para a Entidade/Value Object Endereco
        Endereco enderecoDominio = new Endereco(
                dto.endereco().logradouro(),
                dto.endereco().numero(),
                dto.endereco().complemento(),
                dto.endereco().bairro(),
                dto.endereco().cidade(),
                dto.endereco().uf(),
                dto.endereco().cep()
        );

        // 3. Agora passamos os tipos corretos para o construtor rico da Entidade
        Cliente novoCliente = new Cliente(
                dto.nome(),
                cpfValidado,
                emailValidado,
                dto.telefone(),
                enderecoDominio
        );

        return clienteRepository.save(novoCliente);
    }
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }
}