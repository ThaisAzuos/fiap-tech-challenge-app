variable "region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "aws_profile" {
  description = "AWS CLI profile a ser usado"
  type        = string
  default     = "fiap"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}
