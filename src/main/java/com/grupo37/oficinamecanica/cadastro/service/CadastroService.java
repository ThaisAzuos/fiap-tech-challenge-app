package com.grupo37.oficinamecanica.cadastro.service;

import com.grupo37.oficinamecanica.cadastro.controller.dto.ClienteDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cpf;
import com.grupo37.oficinamecanica.cadastro.domain.model.Email;
import com.grupo37.oficinamecanica.cadastro.domain.model.Endereco;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.ClienteEntity;
import com.grupo37.oficinamecanica.cadastro.repository.ClienteRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CadastroService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente salvarCliente(ClienteDTO dto) {
        if (clienteRepository.existsById(dto.cpf())) {
            throw new BusinessException("Cliente já cadastrado com este CPF.");
        }

        Cpf cpfValidado = new Cpf(dto.cpf());
        Email emailValidado = new Email(dto.email());

        Endereco enderecoDominio = new Endereco(
                dto.endereco().logradouro(),
                dto.endereco().numero(),
                dto.endereco().complemento(),
                dto.endereco().bairro(),
                dto.endereco().cidade(),
                dto.endereco().uf(),
                dto.endereco().cep()
        );

        Cliente novoCliente = new Cliente(
                dto.nome(),
                cpfValidado,
                emailValidado,
                dto.telefone(),
                enderecoDominio
        );

        // Converte para entity, salva, e retorna como domínio
        return clienteRepository.save(new ClienteEntity(novoCliente)).toDomain();
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteEntity::toDomain)
                .collect(Collectors.toList());
    }
}