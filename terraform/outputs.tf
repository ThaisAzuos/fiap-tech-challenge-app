output "cluster_name" {
  value = aws_eks_cluster.oficina_cluster.name
}

output "cluster_endpoint" {
  value = aws_eks_cluster.oficina_cluster.endpoint
}

output "db_endpoint" {
  value = aws_db_instance.oficina_db.endpoint
}
