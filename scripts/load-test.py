import urllib.request
import threading
import time
import sys

# Configurações - Ajustado para gerar mais carga
URL = "http://localhost:8080/actuator/health"  # Endpoint leve
CONCURRENT_USERS = 200                         # Aumentado para garantir consumo de CPU
DURATION_SECONDS = 120                         # Duração aumentada para dar tempo ao HPA reagir

def load_test():
    while getattr(threading.current_thread(), "do_run", True):
        try:
            with urllib.request.urlopen(URL) as response:
                response.read()
                # Sem sleep no sucesso para maximizar requests/segundo
        except Exception as e:
            # Sleep apenas no erro para não floodar o log
            # print(f"Erro: {e}")
            time.sleep(0.5)

def main():
    print(f"--- Iniciando Teste de Carga (Stress no Health Check) ---")
    print(f"Alvo: {URL}")
    print(f"Usuários Simultâneos: {CONCURRENT_USERS}")
    print(f"Duração: {DURATION_SECONDS} segundos")
    print("---------------------------------------------------------")
    print("Certifique-se de que o port-forward está rodando:")
    print("kubectl port-forward svc/oficina-service 8080:80")
    print("---------------------------------------------------------")

    threads = []

    # Inicia as threads
    for i in range(CONCURRENT_USERS):
        t = threading.Thread(target=load_test)
        t.start()
        threads.append(t)

    print(f"Carga iniciada! Acompanhe o HPA em outro terminal: kubectl get hpa -w")


    # Aguarda o tempo definido
    try:
        time.sleep(DURATION_SECONDS)
    except KeyboardInterrupt:
        print("\nInterrompido pelo usuário.")

    # Para as threads
    print("\nParando threads...")
    for t in threads:
        t.do_run = False

    for t in threads:
        t.join()

    print("Teste finalizado.")

if __name__ == "__main__":
    main()
