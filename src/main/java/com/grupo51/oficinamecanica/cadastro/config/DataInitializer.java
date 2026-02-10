package com.grupo51.oficinamecanica.cadastro.config;

import com.grupo51.oficinamecanica.cadastro.model.*;
import com.grupo51.oficinamecanica.cadastro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final MecanicoRepository mecanicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    public DataInitializer(MecanicoRepository mecanicoRepository,
                           FuncionarioRepository funcionarioRepository,
                           ClienteRepository clienteRepository,
                           VeiculoRepository veiculoRepository) {
        this.mecanicoRepository = mecanicoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Instanciamos os objetos de ID primeiro
        Cpf idJoao = new Cpf("73383053036");
        Cpf idMec = new Cpf("09151522037");
        Cpf idAtend = new Cpf("25390437021");
        Placa idPlaca = new Placa("ABC1D23");

        // 1. Cliente
        Cliente joao;
        // O existsById precisa do objeto Cpf, não da String
        if (!clienteRepository.existsById(idJoao.numero())) {
            joao = new Cliente(
                    "João da Silva",
                    idJoao,
                    new Email("joao@email.com"),
                    "11999998888",
                    new Endereco("Rua das Flores", "123", "Casa 1", "Jardim Paulista", "São Paulo", "SP", "01234-567")
            );
            clienteRepository.save(joao);
            System.out.println(">>> Cliente de teste inserido.");
        } else {
            joao = clienteRepository.findById(idJoao.numero()).orElseThrow();
        }

        // 2. Veículo
        if (!veiculoRepository.existsById(idPlaca.valor())) {
            veiculoRepository.save(new Veiculo(idPlaca, "Civic", "Honda", 2022, "Prata", joao));
            System.out.println(">>> Veículo de teste inserido.");
        }

        // 3. Mecânico
        if (!mecanicoRepository.existsById(idMec.numero())) {
            mecanicoRepository.save(new Mecanico("Mestre Joda", idMec, Especialidade.MOTORES, "MF-001"));
            System.out.println(">>> Mecânico de teste inserido.");
        }

        // 4. Atendente
        if (!funcionarioRepository.existsById(idAtend.numero())) {
            funcionarioRepository.save(new Funcionario("Atendente Solo", idAtend, new Email("solo@oficina.com"), Cargo.ATENDENTE));
            System.out.println(">>> Funcionário de teste inserido.");
        }
    }
}