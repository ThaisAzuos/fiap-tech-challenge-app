-- CLIENTES
-- PK é o CPF (alinhado com ClienteEntity que usa String como @Id via @GeneratedValue UUID não, CPF direto)
CREATE TABLE clientes (
    cpf VARCHAR(11) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    uf VARCHAR(2),
    cep VARCHAR(20)
);

-- VEICULOS
-- PK é a placa (alinhado com VeiculoEntity que usa String placa como @Id)
CREATE TABLE veiculos (
    placa VARCHAR(7) PRIMARY KEY,
    modelo VARCHAR(255),
    marca VARCHAR(255),
    ano INTEGER,
    cor VARCHAR(100),
    cliente_id VARCHAR(11) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes(cpf) ON DELETE CASCADE
);

-- FUNCIONARIOS
-- PK é o CPF (alinhado com FuncionarioEntity)
CREATE TABLE funcionarios (
    cpf VARCHAR(11) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    especialidade VARCHAR(50),
    registro_funcional VARCHAR(100) UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- USUARIOS (seguranca)
CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL
);

-- PECAS (estoque)
CREATE TABLE pecas (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL
);

-- SERVICOS
CREATE TABLE servicos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    descricao VARCHAR(500)
);

-- ORDENS DE SERVICO
CREATE TABLE ordens_servico (
    id UUID PRIMARY KEY,
    veiculo_id VARCHAR(7) NOT NULL,
    descricao_problema TEXT NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    valor_total NUMERIC(10, 2) DEFAULT 0.00,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento TEXT,
    data_conclusao TIMESTAMP,
    FOREIGN KEY (veiculo_id) REFERENCES veiculos(placa) ON DELETE CASCADE
);

-- ITENS DE PECAS DA OS
CREATE TABLE ordem_servico_itens (
    id UUID PRIMARY KEY,
    os_id UUID NOT NULL,
    peca_id UUID,
    nome_peca_snapshot VARCHAR(255),
    quantidade INTEGER NOT NULL,
    preco_no_momento NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (os_id) REFERENCES ordens_servico(id) ON DELETE CASCADE
);

-- ITENS DE SERVICOS DA OS
CREATE TABLE ordem_servico_servicos (
    id UUID PRIMARY KEY,
    os_id UUID NOT NULL,
    servico_id UUID,
    nome_servico_snapshot VARCHAR(255),
    preco_no_momento NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (os_id) REFERENCES ordens_servico(id) ON DELETE CASCADE
);

-- AGENDAMENTOS
CREATE TABLE agendamentos (
    id UUID PRIMARY KEY,
    cliente_id VARCHAR(11),
    veiculo_id VARCHAR(7),
    data_hora_inicio TIMESTAMP,
    data_hora_fim TIMESTAMP,
    tipo VARCHAR(50),
    recurso_id VARCHAR(255),
    confirmado BOOLEAN NOT NULL DEFAULT FALSE
);