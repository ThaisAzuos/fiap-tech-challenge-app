# Infraestrutura como Código (IaC) - Terraform

Este diretório contém os scripts Terraform para provisionamento automatizado da infraestrutura da aplicação Oficina Mecânica na AWS.

## Recursos Provisionados

- **VPC**: Rede privada virtual com CIDR 10.0.0.0/16.
- **Subnets**: 2 subnets públicas e 2 privadas em zonas de disponibilidade diferentes.
- **Internet Gateway**: Para acesso à internet nas subnets públicas.
- **NAT Gateway**: Para saída à internet das subnets privadas.
- **Route Tables**: Configuração de roteamento para subnets públicas e privadas.
- **Security Groups**:
  - `eks_sg`: Para o cluster EKS (permite todo tráfego).
  - `rds_sg`: Para o banco de dados RDS (permite PostgreSQL na porta 5432 apenas do EKS).
- **EKS Cluster**: Cluster Kubernetes gerenciado pela AWS.
- **Node Group**: Grupo de nós EC2 para o cluster EKS (t3.medium, escalável de 1 a 5 nós).
- **RDS PostgreSQL**: Instância de banco de dados PostgreSQL 15 (db.t3.micro, 20GB).

## Pré-requisitos

- Conta AWS com permissões para criar recursos (VPC, EKS, RDS, IAM).
- Terraform instalado (versão >= 1.0).
- AWS CLI configurado com credenciais válidas (chave IAM ou SSO).

## Configuração do Profile AWS

O Terraform usa o profile AWS `fiap` por padrão (definido via variável `aws_profile`).  
Antes de executar qualquer comando, configure o profile:

```bash
# Criar/atualizar o profile fiap com suas credenciais IAM
aws configure --profile fiap
# AWS Access Key ID:     AKIA... (gerado no console IAM)
# AWS Secret Access Key: <40 caracteres>
# Default region:        us-east-1
# Default output format: json
```

Valide a autenticação antes de prosseguir:

```bash
aws sts get-caller-identity --profile fiap --region us-east-1
```

Saída esperada:
```json
{
    "UserId": "AIDA...",
    "Account": "123456789012",
    "Arn": "arn:aws:iam::123456789012:user/seu-usuario"
}
```

> Para usar um profile diferente de `fiap`, passe `-var="aws_profile=outro-profile"` nos comandos abaixo.

## Instruções de Aplicação

1. **Navegue para o diretório Terraform**:
   ```bash
   cd terraform
   ```

2. **Inicialize o Terraform**:
   ```bash
   terraform init
   ```

3. **Planeje a aplicação** (visualize o que será criado):
   ```bash
   terraform plan -var="db_password=Oficina2026Segura#123"
   ```

   Para usar um profile AWS diferente do padrão (`fiap`):
   ```bash
   terraform plan -var="db_password=Oficina2026Segura#123" -var="aws_profile=outro-profile"
   ```

4. **Aplique a infraestrutura**:
   ```bash
   terraform apply -var="db_password=Oficina2026Segura#123"
   ```
   Confirme com `yes` quando solicitado.  
   > ⚠️ O provisionamento leva aproximadamente **15–20 minutos** (EKS é lento).

5. **Configure o kubectl** (para acessar o cluster EKS):
   ```bash
   aws eks update-kubeconfig --region us-east-1 --name oficina-cluster --profile fiap
   ```

6. **Verifique os outputs**:
   ```bash
   terraform output
   ```
   Retorna: nome do cluster, endpoint EKS e endpoint do banco de dados.

## Destruir a Infraestrutura

Para remover todos os recursos provisionados (evitar custos):
```bash
terraform destroy -var="db_password=Oficina2026Segura#123"
```

## Variáveis Disponíveis

| Variável      | Padrão       | Descrição                                |
|---------------|--------------|------------------------------------------|
| `region`      | `us-east-1`  | Região AWS onde os recursos serão criados |
| `aws_profile` | `fiap`       | Profile do AWS CLI a ser utilizado        |
| `db_password` | *(obrigatório)* | Senha do banco de dados RDS PostgreSQL |

## Notas

- O profile `fiap` deve ter permissões para criar recursos de VPC, EKS, RDS e IAM.
- A senha do banco de dados deve ser forte (mínimo 8 caracteres, letras, números e símbolos), mas no RDS não pode conter `/`, `@`, `"` ou espaço.
- O cluster EKS e o banco RDS ficam em subnets privadas — não são acessíveis diretamente da internet.
- O RDS só aceita conexões na porta 5432 originadas do Security Group do EKS.
- ⚠️ **Atenção com custos**: EKS (nós `t3.medium`) e NAT Gateway geram custos contínuos. Destrua a infraestrutura após os testes com `terraform destroy`.
