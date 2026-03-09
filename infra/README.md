# Infra DOX

## Subir tudo

```bash
docker-compose -f infra/docker-compose.yaml up -d   # PostgreSQL
./gradlew bootRun                                     # Aplicação (porta 8080)
```

## Endpoints

- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## Parar

```bash
docker-compose -f infra/docker-compose.yaml down       # mantém dados
docker-compose -f infra/docker-compose.yaml down -v    # apaga dados
```

## Build Docker da app

```bash
docker build -t dox-backend -f infra/Dockerfile .
```
