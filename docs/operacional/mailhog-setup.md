# 📧 MailHog - Guia de Configuração e Testes

---

## 📌 O que é MailHog?

MailHog é um servidor SMTP local que captura todos os emails enviados durante o desenvolvimento. Permite:
- ✅ Testar envio de emails sem enviá-los de verdade
- ✅ Visualizar emails em uma Web UI
- ✅ Verificar templates HTML renderizados
- ✅ Simular falhas de entrega
- ✅ API REST para automação

---

## 🚀 Quick Start

### 1. Pré-requisitos
- Docker e Docker Compose instalados
- Projeto officina-mecanica configurado
- Git Bash ou Terminal Linux/Mac

### 2. Iniciar MailHog

#### Opção A: Usando o script (Recomendado)
```bash
chmod +x mailhog-setup.sh
./mailhog-setup.sh start
```

#### Opção B: Usando docker-compose
```bash
docker-compose up mailhog -d
```

#### Opção C: Docker direto
```bash
docker run -d \
  --name mailhog-dev \
  -p 1025:1025 \
  -p 8025:8025 \
  mailhog/mailhog:latest
```

### 3. Acessar a Web UI
Abra o navegador: **http://localhost:8025**

---

## 📋 Estrutura de Ports

| Serviço | Port | Protocolo | Descrição |
|---------|------|-----------|-----------|
| **SMTP** | 1025 | SMTP | Servidor SMTP (sem autenticação) |
| **Web UI** | 8025 | HTTP | Interface web para visualizar emails |
| **API** | 8025 | REST | API para integração |

---

## 🔧 Configuração no Projeto

### application-dev.yml (Já Configurado)
```yaml
spring:
  mail:
    host: mailhog
    port: 1025
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false

  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    cache: false

app:
  email:
    from: noreply@oficinamecanica.com
    fromName: Oficina Mecânica
    replyTo: suporte@oficinamecanica.com
```

### docker-compose.yml (Já Configurado)
```yaml
mailhog:
  image: mailhog/mailhog:latest
  container_name: mailhog-dev
  ports:
    - "1025:1025"  # SMTP
    - "8025:8025"  # Web UI
  networks:
    - oficina_network
```

---

## 🧪 Executar Testes de Email

### Testes de Integração
```bash
# Iniciar MailHog primeiro
./mailhog-setup.sh start

# Executar testes
mvn clean test \
  -Dspring.profiles.active=dev \
  -Dtest=EmailServiceIntegrationTest

# Ou todos os testes
mvn clean test -Dspring.profiles.active=dev
```

### Testes Específicos
```bash
# Teste de template de criação
mvn test -Dtest=EmailServiceIntegrationTest#shouldSendEmailOrdenServioCriada

# Teste de validações
mvn test -Dtest=EmailServiceIntegrationTest#shouldNotSendEmailWithoutRecipient
```

---

## 📊 Web UI - Funcionalidades

### 1. Dashboard
- Lista de todos os emails capturados
- Visualização em tempo real
- Busca e filtros

### 2. Visualizar Email
- **HTML Preview:** Renderização do template
- **Raw Message:** Código-fonte MIME
- **Headers:** Informações do email
- **SMTP Log:** Protocolo SMTP

### 3. Ações
- **View HTML:** Visualizar template renderizado
- **View MIME:** Ver estrutura completa
- **Download:** Baixar como arquivo .eml
- **Delete:** Remover email individual
- **Release:** Enviar para servidor SMTP real

### 4. Admin
- Limpar todos os emails
- Configurações de retenção
- API documentation

---

## 🔍 Web UI - Exemplo de Visualização

```
Dashboard
├── From: noreply@oficinamecanica.com
├── To: cliente@teste.com
├── Subject: Sua Ordem de Serviço #12345
├── Received: 2026-03-16 14:30:00
└── Size: 5.2 KB

Preview:
  ✓ HTML válido com CSS
  ✓ Thymeleaf renderizado
  ✓ Variáveis substituídas
```

---

## 🛠️ Comandos Úteis

### Script mailhog-setup.sh
```bash
# Iniciar
./mailhog-setup.sh start

# Parar
./mailhog-setup.sh stop

# Resetar (limpa todos os emails)
./mailhog-setup.sh reset

# Mostrar logs
./mailhog-setup.sh logs

# Abrir Web UI
./mailhog-setup.sh ui

# Status
./mailhog-setup.sh status

# Ajuda
./mailhog-setup.sh help
```

### Lógica de reuso de container existente (`start`)

O comando `./mailhog-setup.sh start` foi padronizado para **reusar container existente** sempre que possível:

1. Se `mailhog-dev` já estiver rodando, apenas informa e mantém o container atual.
2. Se outro container `mailhog/mailhog:latest` estiver rodando, usa esse container.
3. Se a porta `1025` já estiver ocupada por outro container Docker, reutiliza esse container (sem derrubar nada).
4. Só cria um novo container quando não há nenhum candidato reutilizável.

Isso evita conflitos de porta e reduz reinicializações desnecessárias durante os testes.

### Docker direto
```bash
# Ver logs
docker logs mailhog-dev

# Parar
docker stop mailhog-dev

# Remover
docker rm mailhog-dev

# Inspecionar
docker inspect mailhog-dev
```

---

## 🔗 API REST

MailHog expõe API REST em `http://localhost:8025/api/v1`

### Endpoints Principais

#### 1. Listar Eventos
```bash
curl http://localhost:8025/api/v1/events
```

Resposta:
```json
{
  "items": [
    {
      "ID": "1a2b3c4d",
      "From": "noreply@oficinamecanica.com",
      "To": ["cliente@teste.com"],
      "Content": {
        "Subject": "Sua Ordem de Serviço #12345",
        "Headers": {...}
      },
      "MIME": {...},
      "Raw": "..."
    }
  ]
}
```

#### 2. Obter Email Específico
```bash
curl http://localhost:8025/api/v1/messages/1a2b3c4d
```

#### 3. Deletar Email
```bash
curl -X DELETE http://localhost:8025/api/v1/messages/1a2b3c4d
```

#### 4. Limpar Todos
```bash
curl -X DELETE http://localhost:8025/api/v1/messages
```

#### 5. Buscar Emails
```bash
curl "http://localhost:8025/api/v2/search?kind=from&query=noreply"
```

---

## 🐛 Troubleshooting

### Problema: "Connection refused" ao enviar email

**Causa:** MailHog não está rodando

**Solução:**
```bash
./mailhog-setup.sh start
# ou
docker-compose up mailhog -d
```

### Problema: Porta 1025 já em uso

**Causa:** Outro serviço usando a porta

**Solução:**
```bash
# Localizar processo usando porta 1025
netstat -tln | grep 1025
# ou
lsof -i :1025

# Tentar iniciar com reuso automático
./mailhog-setup.sh start

# Verificar qual container está em uso
./mailhog-setup.sh status
```

Se a porta estiver em uso por processo fora do Docker (ou serviço não-MailHog), libere a porta antes de iniciar novamente.

### Problema: Testes falhando

**Causa:** MailHog não iniciado antes dos testes

**Solução:**
```bash
# Ordem correta:
./mailhog-setup.sh start
sleep 2
mvn clean test -Dspring.profiles.active=dev
```

### Problema: Não vejo emails na Web UI

**Causa:** Aplicação enviando para servidor errado

**Verificar:**
1. `application-dev.yml` com `host: mailhog` ou `host: localhost`
2. Porta SMTP: 1025 (não confundir com 8025 da Web UI)
3. Perfil ativo: `dev` (usar `-Dspring.profiles.active=dev`)

### Problema: HTML não renderizado nos templates

**Causa:** Thymeleaf não processando variáveis

**Verificar:**
1. Templates em `src/main/resources/templates/email/`
2. Variáveis adicionadas com `request.addVariable()`
3. Thymeleaf namespace: `xmlns:th="http://www.thymeleaf.org"`
4. Cache desabilitado: `cache: false` em application-dev.yml

---

## 📧 Templates Disponíveis

### 1. ordem-servico-criada.html
**Quando enviado:** Ao criar nova ordem de serviço

**Variáveis esperadas:**
- `ordemServicoId`: UUID da OS
- `status`: StatusOS (ex: AGUARDANDO_APROVACAO)
- `dataAbertura`: LocalDateTime formatado
- `veiculo`: Marca + Modelo do veículo
- `placa`: Placa do veículo
- `descricaoProblema`: Descrição do problema
- `currentYear`: Ano atual

**Caminho:** `src/main/resources/templates/email/ordem-servico-criada.html`

### 2. ordem-servico-atualizada.html
**Quando enviado:** Ao atualizar status da OS

**Variáveis esperadas:**
- `ordemServicoId`: UUID da OS
- `novoStatus`: StatusOS traduzido
- `dataAtualizacao`: LocalDateTime formatado
- `veiculo`: Marca + Modelo
- `placa`: Placa do veículo
- `observacoes`: Observações opcionais
- `currentYear`: Ano atual

**Caminho:** `src/main/resources/templates/email/ordem-servico-atualizada.html`

---

## 🔒 Segurança

⚠️ **MailHog é APENAS para desenvolvimento!**

**Nunca use em produção porque:**
- ❌ Sem autenticação
- ❌ Sem encriptação
- ❌ Todos os emails acessíveis publicamente
- ❌ Não é persistente

**Para produção:** Use Gmail, SendGrid, AWS SES, etc.

---

## 📚 Fluxo de Teste End-to-End

```
1. Iniciar MailHog
   └─ ./mailhog-setup.sh start

2. Iniciar Aplicação
   └─ mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

3. Executar Teste/Ação
   └─ Criar nova OS
   └─ Atualizar status
   └─ etc.

4. Verificar Email
   └─ Abrir http://localhost:8025
   └─ Visualizar email capturado
   └─ Confirmar template renderizado

5. Validar em Teste Automático
   └─ mvn test -Dtest=EmailServiceIntegrationTest -Dspring.profiles.active=dev
```

---

## 🎯 Testes Disponíveis

### EmailServiceIntegrationTest

Localização: `src/test/java/com/grupo51/oficinamecanica/comum/email/integration/`

Testes:
- ✅ `shouldSendEmailOrdenServioCriada` - Enviar email de criação
- ✅ `shouldSendEmailAtualizacaoStatus` - Enviar email de atualização
- ✅ `shouldNotSendEmailWithoutRecipient` - Validação de destinatário
- ✅ `shouldNotSendEmailWithoutTemplate` - Validação de template
- ✅ `shouldSendEmailWithCC` - Enviar com cópia
- ✅ `shouldSendEmailWithBCC` - Enviar com cópia oculta
- ✅ `shouldValidateEmailServiceIsEnabled` - Serviço habilitado
- ✅ `shouldValidateEmailPropertiesConfigured` - Properties configuradas

---

## 📖 Referências

- **Documentação MailHog:** https://github.com/mailhog/MailHog
- **Spring Boot Mail:** https://spring.io/guides/gs/sending-email/
- **Thymeleaf Templates:** https://www.thymeleaf.org/

---

## 📝 Checklist de Verificação

- [ ] Docker e docker-compose instalados
- [ ] Projeto `oficina-mecanica` clonado
- [ ] MailHog iniciado: `./mailhog-setup.sh start`
- [ ] Web UI acessível: http://localhost:8025
- [ ] SMTP respondendo na porta 1025
- [ ] application-dev.yml com configuração MailHog
- [ ] Testes rodando: `mvn clean test -Dspring.profiles.active=dev`
- [ ] Testes de email passando
- [ ] Templates HTML renderizados corretamente
- [ ] Variáveis Thymeleaf substituídas

---

## ✅ Status do escopo A–D

| Item | Descricao | Status |
|------|-----------|--------|
| **A** | MailHog configurado (Docker, K8s, script de reuso) | ✅ Concluido |
| **B** | Templates cancelamento e conclusao + testes de integracao | ✅ Concluido |
| **C** | Modulo Fase 2 Clean Architecture (`modules/oficina-clean-mvp`) | ✅ Concluido |
| **D** | SonarQube em producao + workflow CI/CD ativo | ✅ Concluido |

Workflow CI/CD ativo: `.github/workflows/sonar.yml`


