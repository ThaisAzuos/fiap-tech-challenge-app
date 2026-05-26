package com.grupo37.oficinamecanica.cadastro.config;

import com.grupo37.oficinamecanica.cadastro.domain.model.*;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.ClienteEntity;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.FuncionarioEntity;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
import com.grupo37.oficinamecanica.cadastro.repository.*;
import com.grupo37.oficinamecanica.seguranca.model.Perfil;
import com.grupo37.oficinamecanica.seguranca.model.Usuario;
import com.grupo37.oficinamecanica.seguranca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(FuncionarioRepository funcionarioRepository,
                           ClienteRepository clienteRepository,
                           VeiculoRepository veiculoRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Cpf idJoao = new Cpf("73383053036");
        Cpf idMec = new Cpf("09151522037");
        Cpf idAtend = new Cpf("25390437021");
        Placa idPlaca = new Placa("ABC1D23");

        Cliente joao;
        if (!clienteRepository.existsById(idJoao.numero())) {
            joao = new Cliente(
                    "João da Silva",
                    idJoao,
                    new Email("joao@email.com"),
                    "11999998888",
                    new Endereco("Rua das Flores", "123", "Casa 1", "Jardim Paulista", "São Paulo", "SP", "01234-567")
            );
            clienteRepository.save(new ClienteEntity(joao));
            System.out.println(">>> Cliente de teste inserido.");
        } else {
            joao = clienteRepository.findById(idJoao.numero()).orElseThrow().toDomain();
        }

        if (!veiculoRepository.existsById(idPlaca.valor())) {
            Veiculo veiculo = new Veiculo(idPlaca, "Civic", "Honda", 2022, "Prata", joao);
            veiculoRepository.save(new VeiculoEntity(veiculo));
            System.out.println(">>> Veículo de teste inserido.");
        }

        if (!funcionarioRepository.existsById(idMec.numero())) {
            Funcionario mecanico = new Funcionario(
                    "Mestre Ioda",
                    idMec,
                    new Email("ioda@oficina.com"),
                    Cargo.MECANICO,
                    passwordEncoder.encode("Senh@316497"),
                    Especialidade.MOTOR,
                    "MF-001"
            );
            funcionarioRepository.save(new FuncionarioEntity(mecanico));
            System.out.println(">>> Mecânico de teste inserido.");
        }

        if (!funcionarioRepository.existsById(idAtend.numero())) {
            Funcionario atendente = new Funcionario(
                    "Atendente Solo",
                    idAtend,
                    new Email("solo@oficina.com"),
                    Cargo.ATENDENTE,
                    passwordEncoder.encode("Senh@316497"),
                    null,
                    null
            );
            funcionarioRepository.save(new FuncionarioEntity(atendente));
            System.out.println(">>> Funcionário de teste inserido.");
        }

        garantirUsuario(idMec.numero(), Perfil.MECANICO, "Senh@316497");
        garantirUsuario(idAtend.numero(), Perfil.ATENDENTE, "Senh@316497");
    }

    private void garantirUsuario(String login, Perfil perfil, String senhaEmTextoPlano) {
        if (usuarioRepository.findByLogin(login).isEmpty()) {
            usuarioRepository.save(new Usuario(null, login, passwordEncoder.encode(senhaEmTextoPlano), perfil));
            System.out.println(">>> Usuário de autenticação seed criado para login: " + login);
        }
    }
}