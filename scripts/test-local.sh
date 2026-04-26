#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"
TEST_COMPOSE_FILE="docker-compose.test.yml"

DOWN_AFTER=false
NO_CLEAN=false
MVN_ARGS=()

usage() {
  cat <<'EOF'
Uso: ./scripts/test-local.sh [opcoes] [-- <args_maven>]

Opcoes:
  --down       Para o servico mailhog ao final da execucao
  --no-clean   Executa "./mvnw test" em vez de "./mvnw clean test"
  --compose-file <arquivo>  Define arquivo compose para ambiente de teste (padrao: docker-compose.test.yml)
  -h, --help   Exibe esta ajuda

Exemplos:
  ./scripts/test-local.sh
  ./scripts/test-local.sh --down
  ./scripts/test-local.sh --compose-file docker-compose.test.yml
  ./scripts/test-local.sh -- -Dtest=OpenApiDocumentationTest
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --down)
      DOWN_AFTER=true
      shift
      ;;
    --no-clean)
      NO_CLEAN=true
      shift
      ;;
    --compose-file)
      [[ $# -lt 2 ]] && { echo "Faltou valor para --compose-file" >&2; exit 1; }
      TEST_COMPOSE_FILE="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    --)
      shift
      MVN_ARGS=("$@")
      break
      ;;
    *)
      echo "Opcao invalida: $1" >&2
      usage
      exit 1
      ;;
  esac
done

compose_cmd() {
  if docker compose version >/dev/null 2>&1; then
    echo "docker compose"
  elif docker-compose version >/dev/null 2>&1; then
    echo "docker-compose"
  else
    echo "ERRO: docker compose/docker-compose nao encontrado." >&2
    exit 1
  fi
}

COMPOSE="$(compose_cmd)"
COMPOSE_TEST="$COMPOSE -f $TEST_COMPOSE_FILE"

if [[ ! -f "$TEST_COMPOSE_FILE" ]]; then
  echo "ERRO: arquivo compose de teste nao encontrado: $TEST_COMPOSE_FILE" >&2
  exit 1
fi

cleanup() {
  if [[ "$DOWN_AFTER" == "true" ]]; then
    echo ">> Parando MailHog..."
    $COMPOSE_TEST stop mailhog
  fi
}

trap cleanup EXIT

echo ">> Subindo MailHog..."
$COMPOSE_TEST up -d mailhog

echo ">> Executando testes Maven..."
if [[ "$NO_CLEAN" == "true" ]]; then
  ./mvnw test "${MVN_ARGS[@]}"
else
  ./mvnw clean test "${MVN_ARGS[@]}"
fi


echo ">> Concluido com sucesso."

