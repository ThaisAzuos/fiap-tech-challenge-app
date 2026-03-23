# AWS + Terraform no IntelliJ — Guia passo a passo

**Acesso rápido:** [`README`](../../README.md) | [`Índice`](../indice.md) | [`Leia primeiro`](../leia-primeiro.md) | [`README do Terraform`](../../terraform/README.md)

**Atualizado em:** 23 de Março de 2026

Este guia mostra, em ordem prática, como gerar suas credenciais **AWS IAM pelo site da AWS** e depois usá-las no IntelliJ para executar o Terraform deste projeto — mesmo que seja a primeira vez que você configura isso.

---

## O que você vai conseguir ao final

- ✅ Gerar as credenciais certas no site da AWS;
- ✅ Configurar o AWS CLI no terminal do IntelliJ;
- ✅ Executar `terraform plan` e `terraform apply` sem erros de autenticação;
- ✅ Acessar o cluster EKS criado com `kubectl`.

---

## Antes de começar — entenda o que você precisa

> Este é o ponto mais importante para quem está configurando pela primeira vez.

Para usar o Terraform localmente, você **não** precisa informar:

| ❌ O que NÃO usar | Por quê |
|---|---|
| Número da conta AWS (ex: `358687723479`) | Isso é o ID da conta, não uma credencial de acesso |
| Senha do console AWS | Essa senha é só para entrar no site |
| Nome do usuário IAM | O nome não autentica nada por si só |

Você precisa obter **apenas duas informações** no site da AWS:

| ✅ O que usar | Como identificar |
|---|---|
| **Access Key ID** | Sempre começa com `AKIA` e tem 20 caracteres |
| **Secret Access Key** | Sequência longa de letras e números (40 caracteres) |

Guarde essas duas informações — você vai colá-las no IntelliJ em seguida.

---

## Passo 1 — Entrar no console AWS e abrir o IAM

1. Abra o navegador e acesse: **https://console.aws.amazon.com/**
2. Faça login com seu usuário e senha da conta AWS.
3. Após o login, você verá o painel principal da AWS.
4. No **campo de busca no topo da página**, clique nele e digite:
   ```text
   IAM
   ```
5. Nos resultados, clique em **IAM** (o primeiro resultado, com ícone de cadeado).

✅ **Como saber que deu certo:** você verá o painel do IAM com o menu lateral mostrando opções como *Users*, *Roles*, *Policies*.

---

## Passo 2 — Localizar ou criar o usuário IAM

No menu lateral esquerdo do IAM, clique em **Users**.

Você verá a lista de usuários IAM da conta.

### Se já existe um usuário para usar neste projeto

1. Clique no nome do usuário na lista.
2. Confirme que ele tem permissões para criar recursos AWS (VPC, EKS, RDS, IAM).
3. **Se tiver permissões, vá para o Passo 3.**

### Se precisar criar um novo usuário

1. Clique no botão **Create user** (canto superior direito da lista).
2. No campo **User name**, digite:
   ```text
   fiap-terraform
   ```
3. Clique em **Next**.
4. Em **Permissions options**, selecione **Attach policies directly**.
5. Na lista de políticas, marque a checkbox de **AdministratorAccess** (para laboratório/estudo).
   > Em ambiente corporativo, use políticas mais restritivas.
6. Clique em **Next** e depois em **Create user**.

✅ **Como saber que deu certo:** o usuário aparece na lista de **Users** com o nome que você definiu.

---

## Passo 3 — Gerar a Access Key no site da AWS

> Aqui você obtém as duas informações que o Terraform realmente usa.

1. Na lista de **Users**, clique no nome do usuário escolhido.
2. Clique na aba **Security credentials** (segunda aba, após *Permissions*).
3. Role a página para baixo até encontrar a seção **Access keys**.
4. Clique no botão **Create access key**.
5. Em **Use case**, selecione **Command Line Interface (CLI)**.
6. Marque o checkbox de confirmação que aparece abaixo.
7. Clique em **Next**.
8. O campo *Description tag* é opcional — deixe em branco ou escreva `fiap-local`.
9. Clique em **Create access key**.

A AWS exibirá uma tela com as suas credenciais:

```text
Access key ID:     AKIA.....................
Secret access key: ****************************************
```

### O que copiar e onde guardar

| Campo | O que fazer |
|---|---|
| **Access key ID** | Clique no ícone de copiar ao lado e cole em um bloco de notas temporário |
| **Secret access key** | Clique em **Show** para revelar, copie e cole no mesmo bloco de notas |

> ⚠️ Depois que você fechar esta tela, a **Secret access key não pode ser recuperada**. Se fechar sem copiar, será necessário criar uma nova chave.

10. Após copiar os dois valores, clique em **Done**.

✅ **Como saber que deu certo:** a nova chave aparece na lista de Access keys com status **Active**.

---

## Passo 4 — Abrir o projeto no IntelliJ

1. Abra o IntelliJ IDEA.
2. No menu, clique em **File > Open**.
3. Navegue até a pasta do projeto e selecione:
   ```text
   /home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina
   ```
4. Clique em **Open** (ou **OK**).
5. Aguarde a indexação do projeto ser concluída (barra de progresso no rodapé do IntelliJ).
6. Abra o terminal integrado: clique em **View > Tool Windows > Terminal**.

✅ **Como saber que deu certo:** o terminal abre na parte inferior do IntelliJ com o prompt apontando para a pasta do projeto.

---

## Passo 5 — Instalar ou validar o AWS CLI

No terminal do IntelliJ, cole e execute:

```bash
aws --version
```

### Se aparecer a versão (ex: `aws-cli/2.x.x`)

✅ O AWS CLI já está instalado. **Vá para o Passo 6.**

### Se aparecer `command not found`

Use o instalador que já vem na pasta `aws/` do projeto:

```bash
cd "/home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina/aws"
./install -i ~/.local/aws-cli -b ~/.local/bin
export PATH="$HOME/.local/bin:$PATH"
```

Para que o `PATH` funcione em toda sessão futura:

```bash
echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

Execute novamente para confirmar:

```bash
aws --version
```

✅ **Como saber que deu certo:** o terminal imprime algo como `aws-cli/2.x.x Python/3.x.x Linux/...`

---

## Passo 6 — Colar as credenciais no terminal do IntelliJ

Agora você vai usar os dois valores copiados no Passo 3.

No terminal do IntelliJ, execute:

```bash
aws configure --profile fiap
```

O terminal pedirá quatro informações, uma por vez:

| Campo que aparece no terminal | O que colar ou digitar |
|---|---|
| `AWS Access Key ID [None]:` | Cole o **Access Key ID** (começa com `AKIA`) |
| `AWS Secret Access Key [None]:` | Cole a **Secret Access Key** (sequência longa) |
| `Default region name [None]:` | Digite `us-east-1` |
| `Default output format [None]:` | Digite `json` |

> Após cada resposta, pressione **Enter** para avançar.

### Exemplo do que você verá no terminal

```text
AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Default region name [None]: us-east-1
Default output format [None]: json
```

✅ **Como saber que deu certo:** o terminal volta ao prompt normalmente, sem erros.

---

## Passo 7 — Validar se a autenticação está funcionando

No terminal do IntelliJ, execute:

```bash
aws sts get-caller-identity --profile fiap --region us-east-1
```

### O que você verá se estiver correto

```json
{
    "UserId": "AIDAIOSFODNN7EXAMPLE",
    "Account": "123456789012",
    "Arn": "arn:aws:iam::123456789012:user/fiap-terraform"
}
```

✅ **Como saber que deu certo:** o terminal retorna um JSON com `UserId`, `Account` e `Arn`. Isso confirma que o AWS CLI reconheceu suas credenciais.

### O que fazer se aparecer erro

| Mensagem de erro | Causa provável | O que fazer |
|---|---|---|
| `InvalidClientTokenId` | Access Key inválida ou copiada errado | Volte ao Passo 3 e gere uma nova chave |
| `NoCredentialsError` | O profile `fiap` não foi salvo | Repita o Passo 6 |
| `SignatureDoesNotMatch` | Secret Access Key incorreta | Repita o Passo 6 com a chave correta |

---

## Passo 8 — Instalar o plugin Terraform no IntelliJ (opcional)

O plugin adiciona destaque de sintaxe nos arquivos `.tf`. Não é obrigatório.

1. Clique em **File > Settings** (ou pressione `Ctrl+Alt+S`).
2. No menu lateral, clique em **Plugins**.
3. Na aba **Marketplace**, no campo de busca, digite:
   ```text
   Terraform and HCL
   ```
4. Clique em **Install** no resultado encontrado.
5. Clique em **Restart IDE** quando solicitado.

✅ **Como saber que deu certo:** os arquivos `.tf` da pasta `terraform/` aparecem com cores ao serem abertos.

---

## Passo 9 — Validar o Terraform

No terminal do IntelliJ, execute:

```bash
terraform version
```

✅ **Como saber que deu certo:** o terminal exibe algo como `Terraform v1.x.x`.

> Se aparecer `command not found`, instale o Terraform em: **https://developer.hashicorp.com/terraform/install**

---

## Passo 10 — Executar o Terraform no projeto

### 10.1 — Ir para a pasta do Terraform

```bash
cd "/home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina/terraform"
```

### 10.2 — Inicializar

```bash
terraform init
```

✅ **Como saber que deu certo:** o terminal exibe `Terraform has been successfully initialized!`

### 10.3 — Validar a configuração

```bash
terraform validate
```

✅ **Como saber que deu certo:** o terminal exibe `Success! The configuration is valid.`

### 10.4 — Gerar o plano

```bash
terraform plan -var="db_password=Oficina2026Segura#123"
```

✅ **Como saber que deu certo:** o terminal termina com `Plan: X to add, 0 to change, 0 to destroy.`

### 10.5 — Aplicar a infraestrutura

```bash
terraform apply -var="db_password=Oficina2026Segura#123"
```

O Terraform pedirá confirmação. No terminal você verá:

```text
  Enter a value:
```

O que colar/digitar:

```text
yes
```

> ⚠️ O provisionamento do EKS pode levar de **15 a 20 minutos**. Aguarde até o terminal retornar ao prompt.

✅ **Como saber que deu certo:** o terminal exibe `Apply complete! Resources: X added, 0 changed, 0 destroyed.`

### 10.6 — Ver os endpoints gerados

```bash
terraform output
```

O terminal exibirá os endereços do cluster EKS e do banco RDS.

### 10.7 — Destruir ao final (evitar cobranças)

```bash
terraform destroy -var="db_password=Oficina2026Segura#123"
```

O que colar/digitar quando solicitado:

```text
yes
```

✅ **Como saber que deu certo:** o terminal exibe `Destroy complete! Resources: X destroyed.`

---

## Passo 11 — Configurar o kubectl para o cluster EKS

Após o `apply`, execute:

```bash
aws eks update-kubeconfig --region us-east-1 --name oficina-cluster --profile fiap
```

Para confirmar que o acesso está funcionando:

```bash
kubectl get nodes
```

✅ **Como saber que deu certo:** o terminal lista os nós do cluster com status `Ready`.

---

## Passo 12 — Fluxo completo de uma vez (para uso no dia a dia)

```bash
cd "/home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina/terraform"

# 1. Confirmar autenticação
aws sts get-caller-identity --profile fiap --region us-east-1

# 2. Inicializar e validar
terraform init
terraform validate

# 3. Planejar e aplicar
terraform plan -var="db_password=Oficina2026Segura#123"
terraform apply -var="db_password=Oficina2026Segura#123"

# 4. Configurar acesso ao cluster
aws eks update-kubeconfig --region us-east-1 --name oficina-cluster --profile fiap
kubectl get nodes
```

---

## Troubleshooting — erros mais comuns

### `InvalidClientTokenId`

**Causa:** credenciais inválidas, expiradas ou copiadas incorretamente.

**Checklist:**
- A **Access Key ID** começa com `AKIA`? Se não, você colou o campo errado.
- A **Secret Access Key** tem 40 caracteres? Se for curta, foi copiada pela metade.
- Você colou o número da conta AWS no lugar da chave?
- A chave foi desativada ou deletada no console IAM?

**Como corrigir:**

```bash
aws configure --profile fiap
```

Cole novamente as credenciais corretas. Se necessário, gere uma nova chave em:
**IAM > Users > (nome do usuário) > Security credentials > Access keys > Create access key**

---

### `Error acquiring the state lock`

**Causa:** lock do Terraform preso de uma execução anterior interrompida.

**Como corrigir** (use o ID exibido na mensagem de erro):

```bash
terraform force-unlock <ID_DO_LOCK>
```

---

### `InvalidParameterValue: The parameter MasterUserPassword is not a valid password`

**Causa:** a senha do `db_password` contém caracteres proibidos pelo RDS: `/` `@` `"` ou espaço.

**Senha válida para este projeto:**

```text
Oficina2026Segura#123
```

---

### `NodeCreationFailure: Unhealthy nodes`

**Causa:** policy `AmazonEKS_CNI_Policy` não estava anexada ao role dos nós. Já foi corrigida no `main.tf` deste projeto.

**O que verificar:**
- as credenciais AWS têm permissão para criar e anexar políticas IAM;
- não existe limite de recursos atingido na conta AWS;
- aguarde alguns minutos — o EKS é lento para provisionar.

---

## Referências relacionadas

- [`../../terraform/README.md`](../../terraform/README.md)
- [`../../aws/README.md`](../../aws/README.md)
- [`../indice.md`](../indice.md)
- [`../leia-primeiro.md`](../leia-primeiro.md)
