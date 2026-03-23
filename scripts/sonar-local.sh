#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SONAR_COMPOSE_FILE="docker-compose.sonar.yml"
SONAR_HOST_URL="http://localhost:9000"
DOWN_AFTER=false
SKIP_WAIT=false
CLEAR_CACHE=false
SONAR_TOKEN_ARG=""
MVN_ARGS=()

usage() {
  cat <<'EOF'
Uso: ./scripts/sonar-local.sh [opcoes] [-- <args_maven>]

Opcoes:
  --token <valor>        Token do SonarQube (ou use env SONAR_TOKEN)
  --host-url <url>       URL do SonarQube (padrao: http://localhost:9000)
  --compose-file <arq>   Compose do Sonar (padrao: docker-compose.sonar.yml)
  --down                 Derruba stack Sonar ao final
  --skip-wait            Nao espera o Sonar subir (executa Maven direto)
  --clear-cache          Limpa cache local do Sonar scanner (~/.sonar/cache)
  -h, --help             Exibe esta ajuda

Exemplos:
  export SONAR_TOKEN="seu_token"
  ./scripts/sonar-local.sh

  ./scripts/sonar-local.sh --token "seu_token" --down

  ./scripts/sonar-local.sh -- --batch-mode
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --token)
      [[ $# -lt 2 ]] && { echo "Faltou valor para --token" >&2; exit 1; }
      SONAR_TOKEN_ARG="$2"
      shift 2
      ;;
    --host-url)
      [[ $# -lt 2 ]] && { echo "Faltou valor para --host-url" >&2; exit 1; }
      SONAR_HOST_URL="$2"
      shift 2
      ;;
    --compose-file)
      [[ $# -lt 2 ]] && { echo "Faltou valor para --compose-file" >&2; exit 1; }
      SONAR_COMPOSE_FILE="$2"
      shift 2
      ;;
    --down)
      DOWN_AFTER=true
      shift
      ;;
    --skip-wait)
      SKIP_WAIT=true
      shift
      ;;
    --clear-cache)
      CLEAR_CACHE=true
      shift
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

if [[ ! -f "$SONAR_COMPOSE_FILE" ]]; then
  echo "ERRO: arquivo compose nao encontrado: $SONAR_COMPOSE_FILE" >&2
  exit 1
fi

SONAR_TOKEN_VALUE="${SONAR_TOKEN_ARG:-${SONAR_TOKEN:-}}"
if [[ -z "$SONAR_TOKEN_VALUE" ]]; then
  echo "ERRO: informe o token via --token ou variavel SONAR_TOKEN." >&2
  exit 1
fi

COMPOSE="$(compose_cmd)"
COMPOSE_SONAR="$COMPOSE -f $SONAR_COMPOSE_FILE"

cleanup() {
  if [[ "$DOWN_AFTER" == "true" ]]; then
    echo ">> Derrubando stack Sonar..."
    $COMPOSE_SONAR down
  fi
}
trap cleanup EXIT

echo ">> Subindo SonarQube e banco..."
$COMPOSE_SONAR up -d

if [[ "$SKIP_WAIT" != "true" ]]; then
  echo ">> Aguardando SonarQube ficar UP..."
  for i in {1..90}; do
    status_json="$(curl -fsS "$SONAR_HOST_URL/api/system/status" 2>/dev/null || true)"
    if echo "$status_json" | grep -q '"status":"UP"'; then
      echo ">> SonarQube pronto."
      break
    fi
    if [[ "$i" -eq 90 ]]; then
      echo "ERRO: SonarQube nao ficou pronto em tempo." >&2
      exit 1
    fi
    sleep 3
  done
fi

echo ">> Validando bootstrap index do Sonar..."
bootstrap_index="$(curl -fsS "$SONAR_HOST_URL/batch/index" 2>/dev/null || true)"
if [[ -z "$bootstrap_index" ]]; then
  echo "ERRO: endpoint $SONAR_HOST_URL/batch/index vazio ou inacessivel." >&2
  exit 1
fi
if ! echo "$bootstrap_index" | grep -Eq '^[^|]+\|[^|]+'; then
  echo "ERRO: bootstrap index em formato inesperado. Primeiras linhas retornadas:" >&2
  echo "$bootstrap_index" | head -n 5 >&2
  exit 1
fi

if [[ "$CLEAR_CACHE" == "true" ]]; then
  echo ">> Limpando cache local do Sonar scanner..."
  rm -rf "$HOME/.sonar/cache"
fi

echo ">> Executando analise Sonar Maven..."
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url="$SONAR_HOST_URL" \
  -Dsonar.token="$SONAR_TOKEN_VALUE" \
  "${MVN_ARGS[@]}"

echo ">> Analise concluida com sucesso."

