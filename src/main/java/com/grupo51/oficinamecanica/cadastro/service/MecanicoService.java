package com.grupo51.oficinamecanica.cadastro.service;


import com.grupo51.oficinamecanica.cadastro.model.Mecanico;
import com.grupo51.oficinamecanica.cadastro.repository.MecanicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MecanicoService {
    private final MecanicoRepository repository;

    public MecanicoService(MecanicoRepository repository) {
        this.repository = repository;
    }

    public Mecanico salvar(Mecanico mecanico) {
        return repository.save(mecanico);
    }

    public List<Mecanico> listarTodos() {
        return repository.findAll();
    }
}
