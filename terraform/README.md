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
- AWS CLI configurado com credenciais.

## Instruções de Aplicação

1. **Navegue para o diretório Terraform**:
   ```bash
   cd terraform
   ```

2. **Inicialize o Terraform**:
   ```bash
   terraform init
   ```

3. **Planeje a aplicação** (opcional, para visualizar mudanças):
   ```bash
   terraform plan -var="db_password=SUA_SENHA_SEGURA"
   ```

4. **Aplique a infraestrutura**:
   ```bash
   terraform apply -var="db_password=SUA_SENHA_SEGURA"
   ```
   Confirme com `yes` quando solicitado.

5. **Configure o kubectl** (para acessar o cluster EKS):
   ```bash
   aws eks update-kubeconfig --region us-east-1 --name oficina-cluster
   ```

6. **Verifique os outputs**:
   ```bash
   terraform output
   ```
   Isso mostrará o nome do cluster, endpoint e endpoint do banco de dados.

## Destruir a Infraestrutura

Para remover todos os recursos provisionados:
```bash
terraform destroy -var="db_password=SUA_SENHA_SEGURA"
```

## Notas

- A senha do banco de dados deve ser fornecida como variável. Use uma senha forte.
- O cluster EKS é provisionado em subnets privadas para segurança.
- O banco de dados RDS está em subnets privadas e acessível apenas do cluster EKS.
- Custos: Esta configuração usa recursos gratuitos ou de baixo custo, mas monitore o uso para evitar cobranças inesperadas.
