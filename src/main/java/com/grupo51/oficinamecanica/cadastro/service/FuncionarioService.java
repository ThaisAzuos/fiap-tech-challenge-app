package com.grupo51.oficinamecanica.cadastro.service;

import com.grupo51.oficinamecanica.cadastro.controller.dto.FuncionarioCadastroDTO;
import com.grupo51.oficinamecanica.cadastro.model.Cargo;
import com.grupo51.oficinamecanica.cadastro.model.Cpf;
import com.grupo51.oficinamecanica.cadastro.model.Email;
import com.grupo51.oficinamecanica.cadastro.model.Funcionario;
import com.grupo51.oficinamecanica.cadastro.repository.FuncionarioRepository;
import com.grupo51.oficinamecanica.seguranca.model.Perfil;
import com.grupo51.oficinamecanica.seguranca.model.Usuario;
import com.grupo51.oficinamecanica.seguranca.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Funcionario salvar(FuncionarioCadastroDTO dados) {
        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var emailUnico = new Email("funcionario." + dados.cpf() + "@oficina.com");
        
        var funcionario = new Funcionario(
            dados.nome(), 
            new Cpf(dados.cpf()), 
            emailUnico,
            dados.cargo(), 
            senhaCriptografada,
            dados.especialidade(),
            dados.registroFuncional()
        );

        funcionarioRepository.save(funcionario);

        var perfil = converterCargoParaPerfil(dados.cargo());
        var usuario = new Usuario(null, funcionario.getCpf(), senhaCriptografada, perfil);
        usuarioRepository.save(usuario);

        return funcionario;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    private Perfil converterCargoParaPerfil(Cargo cargo) {
        return switch (cargo) {
            case ATENDENTE -> Perfil.ATENDENTE;
            case GERENTE -> Perfil.GERENTE;
            case MECANICO -> Perfil.MECANICO;
        };
    }
}
