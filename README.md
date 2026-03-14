# 🛠️ Oficina Mecânica API - Tech Challenge

Esta é uma API REST robusta desenvolvida para a gestão completa de uma oficina mecânica. O sistema permite o controle de clientes, veículos (incluindo especificações como cor e ano), estoque de peças e o ciclo de vida completo de uma **Ordem de Serviço (O.S.)**.

O projeto foi construído focando em boas práticas de desenvolvimento, separação de responsabilidades e facilidade de deploy.

## 🚀 Tecnologias Utilizadas

* **Java 21** & **Spring Boot 3**
* **Spring Data JPA** (Persistência de dados)
* **PostgreSQL 15** (Banco de dados relacional)
* **Docker & Docker Compose** (Containerização e orquestração)
* **Kubernetes (K8s)** (Orquestração de containers e escalabilidade)
* **Maven** (Gerenciamento de dependências)
* **Postman** (Documentação e testes das rotas)
* **SonarQube** (Qualidade do Código e vulnerabilidades de segurança)

---

## 🏗️ Principais Funcionalidades

* **Gestão de Cadastros:** Registro completo de clientes e seus respectivos veículos.
* **Controle de Estoque:** Cadastro e atualização de peças com controle de quantidade.
* **Fluxo de Atendimento:** * Abertura de O.S. vinculada a um veículo.
    * Adição dinâmica de peças e serviços na O.S.
    * Atualização de status (ABERTA, EM_DIAGNOSTICO, AGUARDANDO_PECA, CONCLUIDA, etc.).
* **Cálculos Automáticos:** Cálculo total do valor da O.S. com base nas peças utilizadas.
---

## 🛠️ Diferenciais Técnicos e Arquitetura

### 🆔 Uso de UUID
Todas as entidades utilizam `UUID` como chave primária (`PK`). Esta escolha garante:
- **Segurança:** Impede a descoberta de volume de dados por ID sequencial.
- **Escalabilidade:** Facilita a migração e sincronização de dados entre diferentes bancos.

### 🧠 Domínio Rico (DDD)
Diferente de arquiteturas anêmicas, a lógica de negócio está protegida nas entidades:
- **Máquina de Status:** A `OrdemServico` valida suas próprias transições de status (ex: não permite retrocesso de etapas ou alteração de O.S. finalizada).
- **Snapshot de Preços:** Ao adicionar uma peça, o sistema grava o preço e nome no momento da venda, protegendo o histórico financeiro contra alterações futuras no cadastro de estoque.

### ⚡ Performance e DTOs
- **Lazy Loading:** Implementado para carregar coleções apenas sob demanda.
- **Transactional Read-Only:** Consultas otimizadas com `@Transactional(readOnly = true)` para evitar processamento desnecessário do Hibernate.
- **Records:** Uso de Java Records para DTOs de entrada e saída, garantindo imutabilidade e clareza no contrato da API.

### 🏛️ Clean Architecture/Hexagonal (Fase 2)
A aplicação foi refatorada seguindo os princípios de **Clean Architecture** para melhorar a manutenibilidade e testabilidade:
- **Domain Layer:** Contém as entidades de negócio, regras de domínio e lógica pura (ex: `OrdemServico`, `StatusOS`).
- **Application Layer:** Casos de uso e DTOs de entrada/saída (ex: `AtendimentoService`, `AberturaOSDTO`).
- **Infrastructure Layer:** Implementações técnicas como controllers REST, repositories JPA e configurações externas.
- **Separação de Responsabilidades:** Camadas independentes permitem mudanças na infraestrutura sem afetar o domínio.
- **Injeção de Dependências:** Facilita testes unitários e mocking de dependências externas.

---

## 🐳 Como Rodar com Docker (Desenvolvimento Local)

O projeto está totalmente conteinerizado, utilizando **Docker** e **Docker Compose** para orquestrar a API e o banco de dados PostgreSQL. O processo utiliza **Multi-stage Build**, garantindo uma imagem final leve e segura.

### 🛠️ Diferenciais da Configuração:
- **Multi-stage Build:** A compilação é feita dentro do container (imagem Maven), e a execução usa apenas o JRE (imagem Temurin), reduzindo o tamanho da imagem final.
- **Healthcheck:** A aplicação só inicia após o PostgreSQL confirmar que está pronto para receber conexões.
- **Persistência e Credenciais:** Variáveis de ambiente configuradas para integração imediata.

### 🚀 Passo a Passo

1. **Certifique-se de ter o Docker instalado em sua máquina.**
2. No terminal, navegue até a raiz do projeto e execute:

   a. Limpar containers antigos e órfãos (importante para evitar conflitos):
   ```bash
   docker-compose down --remove-orphans
   ```
   
   b. Subir a aplicação e o banco de dados:
   ```bash
   docker-compose up --build
   ``` 

A API estará disponível em `http://localhost:8080`.

### 🛑 Como Parar (Docker)

Para parar a aplicação e remover os containers criados:
```bash
docker-compose down
```

---

## ☸️ Como Rodar com Kubernetes (Escalabilidade)

Para ambientes de produção ou simulação de escalabilidade, o projeto inclui manifestos Kubernetes completos.

### 📋 Pré-requisitos
- Um cluster Kubernetes rodando (Minikube, Kind, ou Docker Desktop com Kubernetes habilitado).
- Ferramenta de linha de comando `kubectl` instalada e configurada.

### 🚀 Passo a Passo

1. **Aplicar os Manifestos:**
   Na raiz do projeto, execute o comando abaixo para criar todos os recursos (Secrets, ConfigMaps, Deployment, Service e HPA):
   ```bash
   kubectl apply -f k8s-secret.yaml -f k8s-configmap.yaml -f k8s-deployment.yaml -f k8s-service.yaml -f k8s-hpa.yaml
   ```
   *Ou simplesmente:*
   ```bash
   kubectl apply -f .
   ```
   *(Ignore os erros de validação sobre arquivos que não são YAML do K8s, como o README ou docker-compose)*

2. **Verificar os Pods:**
   Aguarde alguns instantes e verifique se os pods estão rodando:
   ```bash
   kubectl get pods
   ```

3. **Acessar a Aplicação:**
   Verifique o serviço criado para obter o IP/Porta de acesso:
   ```bash
   kubectl get svc oficina-service
   ```
   *Se estiver usando Minikube, pode ser necessário rodar `minikube service oficina-service` para acessar.*

4. **Escalabilidade Automática (HPA):**
   O projeto inclui um **Horizontal Pod Autoscaler** configurado para escalar a aplicação automaticamente com base no uso de CPU.
   - **Mínimo de Réplicas:** 2
   - **Máximo de Réplicas:** 10
   - **Gatilho:** 70% de uso de CPU

   Para monitorar o HPA:
   ```bash
   kubectl get hpa
   ```

### 🛑 Como Parar (Kubernetes)

Para remover todos os recursos criados no cluster (Pods, Services, Secrets, etc.):
```bash
kubectl delete -f .
```
*(Isso deletará todos os recursos definidos nos arquivos YAML da pasta atual)*

---

## 📥 Como usar a Collection do Postman

Para facilitar os testes, incluímos o arquivo `Oficina_Mecanica.postman_collection.json` na raiz do projeto. Siga os passos abaixo para importar:

1. Abra o **Postman**.
2. No canto superior esquerdo, clique no botão **Import**.
3. Arraste e solte o arquivo `Oficina_Mecanica.postman_collection.json` na janela que abrir.
4. Uma nova coleção chamada **"Oficina Mecânica API - Tech Challenge"** aparecerá na sua aba lateral.
5. As requisições já estão configuradas com os corpos (JSON) e URLs padrão (`http://localhost:8080`).

> **Nota Importante:** A primeira requisição da coleção, **"0. Login (Obter JWT)"**, deve ser executada primeiro para obter o token de autenticação. As demais requisições usarão este token automaticamente.

---

## 📋 Guia de Testes (Sequence Flow)

Siga a ordem abaixo no Postman para testar o fluxo completo:

### 0. Autenticação
- **POST** `/login`: Execute a requisição "0. Login (Obter JWT)" para obter o token de autenticação.

### 1. Contexto de Cadastro
- **POST** `/api/v1/clientes`: Cadastre um cliente (use CPF válido).
- **POST** `/api/v1/veiculos`: Vincule um veículo ao CPF do dono.

### 2. Contexto de Estoque
- **POST** `/api/v1/pecas`: Cadastre peças (ex: Pastilha de freio, Óleo).

### 3. Contexto de Atendimento (Fluxo Principal)
- **POST** `/api/v1/atendimento/os`: Abra uma O.S. informando apenas a placa e o problema.
- **POST** `/api/v1/atendimento/os/{osId}/pecas`: Adicione itens à O.S. (O sistema calcula o total automaticamente).
- **PATCH** `/api/v1/atendimento/os/{osId}/status?novoStatus={Status de evolução}`: Avance o status (ex: `RECEBIDA` -> `EM_DIAGNOSTICO`).
- **PATCH** `/api/v1/atendimento/os/{osId}/aprovacao`: Aprove orçamento (muda status para `EM_EXECUCAO`).
- **GET** `/api/v1/atendimento/os/{osId}`: Veja o resumo detalhado com dados do cliente, veículo e lista de peças.
- **GET** `/api/v1/atendimento/os?page=0&size=10&sort=status`: Liste O.S. ativas (excluindo finalizadas/entregues) com paginação.
