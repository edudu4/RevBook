# RevBook

Rede social de nicho para resenhas de livros em português. Os usuários publicam opiniões sobre obras validadas automaticamente pela Google Books API, avaliam por estrelas, comentam, respondem a comentários e recebem notificações — tudo em um espaço pensado exclusivamente para leitores, sem a dispersão de conteúdo das redes sociais genéricas.

🔗 **Produção:** [revbook.com.br](https://revbook.com.br) · API em `api.revbook.com.br`

## Funcionalidades

- Login via OAuth2 do Google, com sessão persistente por refresh token
- Busca de livros com validação real via Google Books API (sinopse, capa e autor preenchidos automaticamente)
- Criação, edição e exclusão de resenhas
- Avaliação por estrelas
- Comentários com respostas encadeadas e reações em emoji
- Notificações em quase tempo real (nova avaliação, comentário ou resposta)
- Perfil público com histórico de resenhas e comentários
- Cargo de moderador com permissão para editar/excluir conteúdo de terceiros

## Arquitetura

O backend é dividido em três microsserviços independentes, cada um com seu próprio banco PostgreSQL, além de um frontend React servido separadamente:

```
                    ┌──────────────┐
   navegador  ───▶  │   gateway    │  (Spring Cloud Gateway)
                    └──────┬───────┘
                           │ REST
            ┌──────────────┼──────────────┐
            ▼                             ▼
   ┌─────────────────┐          ┌──────────────────┐
   │  auth-service    │          │  review-service   │
   │  (login, JWT,    │          │  (resenhas,       │
   │  refresh token)  │          │  comentários,     │
   │                  │          │  notificações,    │
   │   auth_db        │          │  Google Books)    │
   └─────────────────┘          │   review_db        │
                                 └──────────────────┘
```

- **gateway**: ponto de entrada único. Valida o JWT presente no cookie httpOnly, converte para o header `Authorization` e roteia para o serviço correto. Também aplica as políticas de CORS.
- **auth-service**: login via OAuth2 do Google, emissão de JWT de curta duração (15 min) e refresh token opaco rotativo (30 dias).
- **review-service**: núcleo funcional — resenhas, avaliações, comentários, respostas, reações, notificações e integração com a Google Books API.
- **frontend**: SPA em React consumindo a API através do gateway.

Toda a infraestrutura roda em containers Docker, orquestrados via Docker Compose. Em produção, um servidor Caddy atua como proxy reverso com HTTPS automático, e o deploy é feito por um pipeline de CI/CD no GitHub Actions.

## Stack tecnológica

**Backend:** Java 21, Spring Boot 3, Spring Cloud Gateway, Spring Data JPA, PostgreSQL, JUnit 5 + Mockito

**Frontend:** React, TypeScript, Vite, Tailwind CSS

**Infraestrutura:** Docker, Docker Compose, GitHub Actions (CI/CD), Caddy, AWS EC2

## Como rodar localmente

### Pré-requisitos

- Java 21
- Node.js 20+ e [pnpm](https://pnpm.io/)
- Docker

### 1. Banco de dados

```bash
cd backend
docker compose up -d
```

Isso sobe um PostgreSQL local na porta `5433`, com os bancos `auth_db` e `review_db`.

### 2. Variáveis de ambiente

Crie um arquivo `backend/.env` com:

```
JWT_SECRET=uma-chave-secreta-qualquer
DB_USERNAME=revbook
DB_PASSWORD=sua-senha-local
GOOGLE_CLIENT_ID=id-do-client-oauth-google
GOOGLE_BOOKS=chave-da-google-books-api      # opcional, mas sujeita a rate limit sem uma chave
MODERATOR_EMAIL=seu-email@gmail.com          # opcional, habilita o cargo de moderador
```

### 3. Backend

Cada módulo roda de forma independente. A partir de `backend/`, com as variáveis do `.env` exportadas no ambiente:

```bash
mvn -pl auth-service spring-boot:run     # porta 8877
mvn -pl review-service spring-boot:run   # porta 8878
mvn -pl gateway spring-boot:run          # porta 3001, precisa de AUTH_SERVICE_URL e REVIEW_SERVICE_URL
```

### 4. Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

## Testes

```bash
cd backend
mvn test          # roda os testes dos três módulos

cd frontend
pnpm check         # checagem de tipos TypeScript
```

Os testes de backend usam JUnit 5 e Mockito, organizados no padrão AAA (Arrange-Act-Assert), cobrindo as regras de negócio da camada de serviço com mocks para dependências externas.

## CI/CD

A cada push na branch `main`, um pipeline no GitHub Actions:

1. Roda a suíte de testes dos três módulos backend e a checagem de tipos do frontend;
2. Constrói as imagens Docker dos quatro componentes (auth-service, review-service, gateway, frontend);
3. Publica as imagens no GitHub Container Registry;
4. Atualiza automaticamente os containers em produção via SSH.

## Estrutura do repositório

```
backend/
  auth-service/     # autenticação, JWT, refresh token
  review-service/    # resenhas, comentários, notificações, Google Books
  gateway/           # API Gateway
  docker-compose.yml # ambiente local (Postgres)
frontend/
  client/src/         # aplicação React
docker-compose.prod.yml  # orquestração de produção
Caddyfile                # configuração do proxy reverso
```
