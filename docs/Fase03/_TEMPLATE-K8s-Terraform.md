# K8s Terraform - Main Files

## 📄 main.tf

```hcl
terraform {
  required_version = ">= 1.5"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  backend "s3" {}
}

provider "aws" {
  region = var.aws_region
}

module "vpc" {
  source = "./modules/vpc"
  environment = var.environment
  cluster_name = var.cluster_name
  vpc_cidr = var.vpc_cidr
  availability_zones = var.availability_zones
}

module "eks" {
  source = "./modules/eks"
  environment = var.environment
  cluster_name = var.cluster_name
  cluster_version = var.kubernetes_version
  vpc_id = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  node_instance_type = var.node_instance_type
  desired_node_count = var.desired_node_count
  min_node_count = var.min_node_count
  max_node_count = var.max_node_count
}
```

## 📄 variables.tf

```hcl
variable "aws_region" { type = string; default = "us-east-1" }
variable "environment" { type = string }
variable "cluster_name" { type = string; default = "oficina-eks" }
variable "kubernetes_version" { type = string; default = "1.28" }
variable "vpc_cidr" { type = string; default = "10.0.0.0/16" }
variable "availability_zones" { type = list(string); default = ["us-east-1a", "us-east-1b"] }
variable "node_instance_type" { type = string; default = "t3.medium" }
variable "desired_node_count" { type = number; default = 2 }
variable "min_node_count" { type = number; default = 2 }
variable "max_node_count" { type = number; default = 10 }
```

## 📄 outputs.tf

```hcl
output "cluster_endpoint" { value = module.eks.cluster_endpoint; sensitive = true }
output "cluster_name" { value = module.eks.cluster_name }
output "private_subnet_ids" { value = module.vpc.private_subnet_ids }
output "vpc_id" { value = module.vpc.vpc_id }
```

## 📄 .github/workflows/apply.yml

```yaml
name: Terraform Apply
on:
  push:
    branches: [main]

jobs:
  apply:
    runs-on: ubuntu-latest
    environment: production
    steps:
      - uses: actions/checkout@v3
      - uses: hashicorp/setup-terraform@v2
      - uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      - run: terraform init -backend-config="bucket=${{ secrets.TF_STATE_BUCKET }}"
      - run: terraform plan -var-file=envs/prod.tfvars
      - run: terraform apply -auto-approve -var-file=envs/prod.tfvars
```

## 📄 README.md

```markdown
# Kubernetes Infrastructure

Provision EKS cluster, VPC, nodes via Terraform.

## Deploy
```bash
terraform init
terraform plan -var-file=envs/prod.tfvars
terraform apply -var-file=envs/prod.tfvars
```

## Update Kubeconfig
```bash
aws eks update-kubeconfig --region us-east-1 --name $(terraform output -raw cluster_name)
```

## Cost
- EKS: $73/mês
- Nodes (2x t3.medium): $60/mês
- NAT: $32/mês
- **Total**: ~$165/mês
```

