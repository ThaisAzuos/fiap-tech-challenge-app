#!/bin/bash

###############################################################################
# Script para gerenciar MailHog (SMTP para desenvolvimento)
#
# Uso: ./mailhog-setup.sh [start|stop|logs|reset|ui]
#
# Componentes:
# - SMTP Server: localhost:1025
# - Web UI: localhost:8025
# - Container: mailhog-dev
###############################################################################

set -e

PROJECT_NAME="oficina-mecanica"
CONTAINER_NAME="mailhog-dev"
IMAGE="mailhog/mailhog:latest"

resolve_running_mailhog() {
    if docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
        echo "$CONTAINER_NAME"
        return
    fi

    docker ps --filter ancestor="$IMAGE" --format '{{.Names}}' | head -n 1
}

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

start_mailhog() {
    print_header "Iniciando MailHog"

    local running_container
    running_container="$(resolve_running_mailhog)"

    if [ -n "$running_container" ]; then
        print_info "MailHog já está rodando"
        print_info "Container em uso: $running_container"
        return
    fi

    local port_owner
    port_owner="$(docker ps --format '{{.Names}} {{.Ports}}' | grep '0.0.0.0:1025->1025/tcp' | awk '{print $1}' | head -n 1 || true)"
    if [ -n "$port_owner" ]; then
        print_info "A porta 1025 já está em uso pelo container: $port_owner"
        print_info "Usando container existente na porta SMTP/Web UI"
        return
    fi

    if docker ps -a | grep -q "$CONTAINER_NAME"; then
        print_info "Removendo container antigo..."
        docker rm "$CONTAINER_NAME" > /dev/null 2>&1 || true
    fi

    print_info "Iniciando novo container MailHog..."
    NETWORK_ARG=()
    if docker network ls --format '{{.Name}}' | grep -qx "${PROJECT_NAME}_network"; then
        NETWORK_ARG=(--network "${PROJECT_NAME}_network")
    fi

    docker run -d \
        --name "$CONTAINER_NAME" \
        -p 1025:1025 \
        -p 8025:8025 \
        "${NETWORK_ARG[@]}" \
        "$IMAGE"

    sleep 2

    if docker ps | grep -q "$CONTAINER_NAME"; then
        print_success "MailHog iniciado com sucesso!"
        print_info "SMTP Server: localhost:1025"
        print_info "Web UI: http://localhost:8025"
    else
        print_error "Falha ao iniciar MailHog"
        exit 1
    fi
}

stop_mailhog() {
    print_header "Parando MailHog"

    local running_container
    running_container="$(resolve_running_mailhog)"

    if [ -n "$running_container" ]; then
        print_info "Parando container..."
        docker stop "$running_container"
        print_success "MailHog parado com sucesso!"
    else
        print_info "MailHog não está rodando"
    fi
}

reset_mailhog() {
    print_header "Ressetando MailHog"

    stop_mailhog

    if docker ps -a | grep -q "$CONTAINER_NAME"; then
        print_info "Removendo container..."
        docker rm "$CONTAINER_NAME"
        print_success "Container removido"
    fi

    print_info "Iniciando novo container..."
    start_mailhog

    print_success "MailHog ressetado! Todos os emails anteriores foram removidos."
}

show_logs() {
    print_header "Logs do MailHog"

    local running_container
    running_container="$(resolve_running_mailhog)"

    if [ -n "$running_container" ]; then
        docker logs -f "$running_container"
    else
        print_error "MailHog não está rodando"
        exit 1
    fi
}

open_ui() {
    print_header "Abrindo MailHog Web UI"

    local running_container
    running_container="$(resolve_running_mailhog)"

    if [ -n "$running_container" ]; then
        URL="http://localhost:8025"
        print_info "Abrindo $URL..."

        if command -v xdg-open > /dev/null; then
            xdg-open "$URL"
        elif command -v open > /dev/null; then
            open "$URL"
        else
            print_info "Acesse manualmente: $URL"
        fi
    else
        print_error "MailHog não está rodando. Inicie com: ./mailhog-setup.sh start"
        exit 1
    fi
}

status_mailhog() {
    print_header "Status do MailHog"

    local running_container
    running_container="$(resolve_running_mailhog)"

    if [ -n "$running_container" ]; then
        print_success "MailHog está rodando"

        # Tentar acessar a API
        if curl -s --max-time 2 http://localhost:8025/api/v1/events > /dev/null; then
            EMAIL_COUNT=$(curl -s --max-time 2 http://localhost:8025/api/v1/events | grep -o '"ID"' | wc -l)
            print_info "Emails na caixa: $EMAIL_COUNT"
        fi

        print_info "Container: $running_container"
        print_info "SMTP Server: localhost:1025"
        print_info "Web UI: http://localhost:8025"
    else
        print_error "MailHog não está rodando"
    fi
}

help_text() {
    cat << EOF
Uso: ./mailhog-setup.sh [COMMAND]

Comandos disponíveis:
    start       Inicia o MailHog
    stop        Para o MailHog
    status      Mostra status do MailHog
    reset       Para e reseta o MailHog (limpa todos os emails)
    logs        Mostra logs em tempo real
    ui          Abre a Web UI do MailHog (localhost:8025)
    help        Mostra esta mensagem

Exemplos:
    ./mailhog-setup.sh start
    ./mailhog-setup.sh ui
    ./mailhog-setup.sh reset

Documentação:
    SMTP Server: localhost:1025
    Web UI: http://localhost:8025
    API: http://localhost:8025/api/v1

Para usar com Spring Boot (dev profile):
    Configuração já está em: src/main/resources/application-dev.yml
    Execute testes: mvn clean test -Dspring.profiles.active=dev

Mais informações: https://github.com/mailhog/MailHog
EOF
}

# Main
case "${1:-help}" in
    start)
        start_mailhog
        ;;
    stop)
        stop_mailhog
        ;;
    reset)
        reset_mailhog
        ;;
    logs)
        show_logs
        ;;
    status)
        status_mailhog
        ;;
    ui)
        open_ui
        ;;
    help|--help|-h)
        help_text
        ;;
    *)
        print_error "Comando desconhecido: $1"
        help_text
        exit 1
        ;;
esac

