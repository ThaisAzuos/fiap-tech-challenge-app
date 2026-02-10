package com.grupo51.oficinamecanica.cadastro.service;

import com.grupo51.oficinamecanica.cadastro.model.Funcionario;
import com.grupo51.oficinamecanica.cadastro.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FuncionarioService {
    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public Funcionario salvar(Funcionario funcionario) {
        return repository.save(funcionario);
    }

    public List<Funcionario> listarTodos() {
        return repository.findAll();
    }
}
