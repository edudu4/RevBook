# Sistema de Resenha de Livros - Documentação Completa

## Visão Geral

Este é um sistema completo de resenha de livros construído com uma arquitetura de microsserviços. O sistema permite que usuários façam login com suas contas do Google, compartilhem resenhas de livros, avaliem resenhas de outros usuários, participem de discussões através de comentários, reajam aos comentários com emojis, visualizem seu perfil com todas as suas contribuições e busquem resenhas por título, autor ou gênero.

## Arquitetura do Sistema

### Componentes Principais

O sistema é dividido em três microsserviços backend e um frontend React:

#### 1. API Gateway (Porta 3001)
O ponto de entrada único para o frontend. Responsável por:
- Roteamento de requisições para os microsserviços apropriados
- Validação de JWT
- Agregação de dados
- Habilitação de CORS

**Endpoints principais:**
- `POST /auth/google` - Login com Google
- `GET /reviews` - Obter todas as resenhas
- `GET /reviews/search?title=...&author=...&genre=...` - Buscar resenhas com filtros
- `GET /genres` - Obter lista de gêneros disponíveis
- `GET /reviews/:id` - Obter uma resenha específica
- `POST /reviews` - Criar nova resenha (requer autenticação)
- `POST /reviews/rate` - Avaliar uma resenha (requer autenticação)
- `GET /reviews/:reviewId/comments` - Obter comentários de uma resenha
- `POST /reviews/:reviewId/comments` - Criar novo comentário (requer autenticação)
- `DELETE /comments/:commentId` - Deletar comentário (requer autenticação)
- `PUT /comments/:commentId` - Atualizar comentário (requer autenticação)
- `GET /comments/:commentId/reactions` - Obter reações de um comentário
- `POST /comments/:commentId/reactions` - Adicionar reação a um comentário (requer autenticação)
- `DELETE /reactions/:reactionId` - Remover reação (requer autenticação)
- `GET /users/:userId/profile` - Obter estatísticas do usuário
- `GET /users/:userId/reviews` - Obter resenhas do usuário
- `GET /users/:userId/comments` - Obter comentários do usuário

#### 2. Auth Service (Porta 8877)
Microsserviço responsável por autenticação e gerenciamento de usuários.

**Funcionalidades:**
- Validação de credenciais do Google
- Criação e gerenciamento de usuários
- Geração de tokens JWT
- Validação de tokens

**Banco de dados:** SQLite (auth.sqlite)

**Entidades:**
- User: id, email, name, googleId, avatar

#### 3. Review Service (Porta 8878)
Microsserviço responsável por resenhas, avaliações, comentários, reações e dados de perfil.

**Funcionalidades:**
- Criação de resenhas com gênero
- Listagem de resenhas
- Busca avançada por título, autor ou gênero
- Listagem de gêneros disponíveis
- Avaliação de resenhas
- Gerenciamento de ratings
- Criação, leitura, atualização e exclusão de comentários
- Recuperação de comentários por resenha
- Adição e remoção de reações com emojis
- Agrupamento de reações por emoji com contagem
- Recuperação de resenhas e comentários por usuário
- Cálculo de estatísticas do usuário

**Banco de dados:** SQLite (reviews.sqlite)

**Entidades:**
- Review: id, bookTitle, author, genre, content, userId, userName, createdAt, ratings[], comments[]
- Rating: id, userId, value, reviewId
- Comment: id, content, userId, userName, userAvatar, reviewId, createdAt, updatedAt, reactions[]
- Reaction: id, emoji, userId, commentId, createdAt (com constraint único em commentId, userId, emoji)

#### 4. Frontend React (Porta 3000)
Interface web moderna construída com React, TailwindCSS e shadcn/ui.

**Páginas principais:**
- Login: Autenticação com Google
- Home: Feed de resenhas, funcionalidades de criação/avaliação, comentários e busca avançada
- Profile: Perfil do usuário com resenhas, comentários e estatísticas

**Componentes:**
- CommentsSection: Exibe e gerencia comentários de uma resenha com reações
- ReactionPicker: Seletor de emojis para reações
- SearchBar: Barra de busca avançada com filtros por título, autor e gênero

## Fluxo de Autenticação

1. Usuário clica em "Entrar com Google"
2. Google retorna um token JWT do Google
3. Frontend envia o token para o API Gateway (`POST /auth/google`)
4. API Gateway passa a requisição para o Auth Service
5. Auth Service valida o token e cria/recupera o usuário
6. Auth Service gera um token JWT da aplicação
7. Token é retornado ao frontend e armazenado em localStorage
8. Frontend usa o token em requisições subsequentes

## Fluxo de Criação de Resenha

1. Usuário autenticado clica em "Nova Resenha"
2. Modal de criação é exibido
3. Usuário preenche os dados (título, autor, gênero, conteúdo)
4. Frontend envia requisição `POST /reviews` com o token JWT
5. API Gateway valida o token e extrai informações do usuário
6. Requisição é passada para o Review Service
7. Review Service cria a resenha no banco de dados
8. Resenha é retornada e adicionada ao feed

## Fluxo de Busca Avançada

### Visualizar Barra de Busca
1. Usuário clica no ícone de seta na barra de busca para expandir
2. Barra de busca mostra campos para título, autor e seletor de gênero
3. Gêneros são carregados automaticamente do backend

### Buscar Resenhas
1. Usuário preenche um ou mais campos de filtro
2. Clica em "Buscar" ou pressiona Enter
3. Frontend envia requisição `GET /reviews/search?title=...&author=...&genre=...`
4. API Gateway passa para o Review Service
5. Review Service busca resenhas usando ILIKE (case-insensitive)
6. Resultados são retornados e exibidos no feed
7. Status de busca é exibido mostrando número de resultados

### Limpar Busca
1. Usuário clica em "Limpar" ou recarrega todas as resenhas
2. Frontend envia requisição `GET /reviews`
3. Feed volta a mostrar todas as resenhas
4. Status de busca é removido

## Fluxo de Avaliação de Resenha

1. Usuário autenticado clica no ícone de estrela em uma resenha
2. Frontend envia requisição `POST /reviews/rate` com o ID da resenha
3. API Gateway valida o token
4. Review Service cria ou atualiza a avaliação
5. Feed é recarregado com as avaliações atualizadas

## Fluxo de Comentários

### Visualizar Comentários
1. Usuário clica em "Ver comentários" em uma resenha
2. Frontend busca comentários via `GET /reviews/:reviewId/comments`
3. Comentários são exibidos em ordem cronológica reversa (mais recentes primeiro)

### Criar Comentário
1. Usuário autenticado escreve um comentário
2. Frontend envia `POST /reviews/:reviewId/comments` com o conteúdo
3. API Gateway valida o token e adiciona informações do usuário
4. Review Service cria o comentário no banco de dados
5. Comentário é exibido imediatamente na seção de comentários

### Editar Comentário
1. Usuário clica em "Editar" em seu próprio comentário
2. Conteúdo fica editável
3. Usuário clica em "Confirmar" ou "Cancelar"
4. Se confirmar, frontend envia `PUT /comments/:commentId` com novo conteúdo
5. API Gateway valida que o usuário é o autor do comentário
6. Review Service atualiza o comentário e marca como editado

### Deletar Comentário
1. Usuário clica em "Deletar" em seu próprio comentário
2. Frontend envia `DELETE /comments/:commentId`
3. API Gateway valida que o usuário é o autor
4. Review Service remove o comentário do banco de dados
5. Comentário desaparece da seção de comentários

## Fluxo de Reações

### Adicionar Reação
1. Usuário autenticado clica no ícone de emoji ou em um emoji já existente
2. Se clicou no ícone, um seletor de emojis aparece
3. Usuário seleciona um emoji (👍, ❤️, 😂, 😮, 😢, 🔥, 🎉, 💯)
4. Frontend envia `POST /comments/:commentId/reactions` com o emoji
5. API Gateway valida o token
6. Review Service verifica se a reação já existe:
   - Se existe: remove a reação (toggle)
   - Se não existe: cria a reação
7. Reações são agrupadas por emoji e exibidas com contagem
8. Reações do usuário atual são destacadas visualmente

### Visualizar Reações
1. Cada comentário exibe suas reações agrupadas por emoji
2. Cada grupo mostra o emoji e a contagem de reações
3. Ao passar o mouse, mostra quantas pessoas reagiram
4. Reações do usuário atual aparecem com fundo destacado

### Remover Reação
1. Usuário clica em um emoji que já reagiu
2. Frontend envia `DELETE /reactions/:reactionId`
3. API Gateway valida que o usuário é o autor da reação
4. Review Service remove a reação
5. Contagem é atualizada imediatamente

## Fluxo de Perfil de Usuário

### Acessar Perfil
1. Usuário autenticado clica no ícone de perfil (👤) no header
2. Frontend navega para `/profile`
3. Página carrega dados do usuário via:
   - `GET /users/:userId/profile` - Estatísticas
   - `GET /users/:userId/reviews` - Resenhas do usuário
   - `GET /users/:userId/comments` - Comentários do usuário

### Visualizar Perfil
1. Página exibe:
   - Avatar com inicial do nome
   - Nome e email do usuário
   - Estatísticas: número de resenhas, comentários e avaliações recebidas
   - Abas para visualizar resenhas e comentários
2. Usuário pode alternar entre abas para ver suas contribuições
3. Cada resenha/comentário exibe informações básicas e link para voltar ao feed

## Tecnologias Utilizadas

### Backend
- **NestJS**: Framework Node.js para construção de aplicações escaláveis
- **TypeORM**: ORM para gerenciamento de banco de dados
- **SQLite**: Banco de dados leve para desenvolvimento
- **JWT**: Autenticação baseada em tokens
- **Passport**: Middleware de autenticação
- **Jest**: Framework de testes

### Frontend
- **React 19**: Biblioteca para construção de interfaces
- **Vite**: Build tool moderno
- **TailwindCSS 4**: Framework de CSS utilitário
- **shadcn/ui**: Componentes React reutilizáveis
- **Wouter**: Router leve para React
- **Lucide React**: Ícones SVG

## Configuração e Execução

### Pré-requisitos
- Node.js 18+
- pnpm (gerenciador de pacotes)

### Instalação

1. **Instalar dependências do backend:**
```bash
cd backend/auth-service && pnpm install
cd ../review-service && pnpm install
cd ../gateway && pnpm install
```

2. **Instalar dependências do frontend:**
```bash
cd frontend && pnpm install
```

### Execução

1. **Iniciar o Auth Service:**
```bash
cd backend/auth-service
pnpm start
```

2. **Iniciar o Review Service:**
```bash
cd backend/review-service
pnpm start
```

3. **Iniciar o API Gateway:**
```bash
cd backend/gateway
pnpm start
```

4. **Iniciar o Frontend:**
```bash
cd frontend
pnpm dev
```

O sistema estará disponível em `http://localhost:3000`

## Testes Unitários

Os testes unitários foram implementados usando Jest para os serviços backend.

### Executar testes do Auth Service:
```bash
cd backend/auth-service
pnpm test
```

### Executar testes do Review Service (incluindo testes de comentários e reações):
```bash
cd backend/review-service
pnpm test
```

**Resultados esperados:**
- Auth Service: 3 testes passando
- Review Service: 10 testes passando (incluindo 2 testes de comentários e 4 testes de reações)

## Estrutura de Pastas

```
.
├── backend/
│   ├── auth-service/          # Microsserviço de autenticação
│   │   ├── src/
│   │   │   ├── user.entity.ts
│   │   │   ├── auth.service.ts
│   │   │   ├── auth.controller.ts
│   │   │   ├── auth.service.spec.ts
│   │   │   └── main.ts
│   │   └── package.json
│   ├── review-service/        # Microsserviço de resenhas, comentários e reações
│   │   ├── src/
│   │   │   ├── review.entity.ts
│   │   │   ├── rating.entity.ts
│   │   │   ├── comment.entity.ts
│   │   │   ├── reaction.entity.ts
│   │   │   ├── review.service.ts
│   │   │   ├── review.controller.ts
│   │   │   ├── review.service.spec.ts
│   │   │   ├── comment.service.spec.ts
│   │   │   ├── reaction.service.spec.ts
│   │   │   └── main.ts
│   │   └── package.json
│   └── gateway/               # API Gateway
│       ├── src/
│       │   ├── app.module.ts
│       │   ├── app.controller.ts
│       │   ├── auth.guard.ts
│       │   └── main.ts
│       └── package.json
└── frontend/                  # Frontend React
    ├── client/
    │   ├── src/
    │   │   ├── pages/
    │   │   │   ├── Home.tsx
    │   │   │   ├── Login.tsx
    │   │   │   ├── Profile.tsx
    │   │   │   └── NotFound.tsx
    │   │   ├── components/
    │   │   │   ├── CommentsSection.tsx
    │   │   │   ├── ReactionPicker.tsx
    │   │   │   └── SearchBar.tsx
    │   │   ├── contexts/
    │   │   │   ├── AuthContext.tsx
    │   │   │   └── ThemeContext.tsx
    │   │   ├── App.tsx
    │   │   ├── main.tsx
    │   │   └── index.css
    │   ├── public/
    │   └── index.html
    └── package.json
```

## Variáveis de Ambiente

### Backend
- `JWT_SECRET`: Chave secreta para assinar tokens JWT (padrão: "secretKey")

### Frontend
- `VITE_API_URL`: URL do API Gateway (padrão: "http://localhost:3001")
- `VITE_GOOGLE_CLIENT_ID`: ID do cliente Google OAuth

## Design da Interface

O frontend foi desenvolvido com uma filosofia de **Minimalismo Elegante com Foco em Leitura**, inspirado em plataformas literárias como Goodreads.

**Características de design:**
- Tipografia elegante: Playfair Display para títulos, Lato para corpo
- Paleta neutra: Cream, Charcoal, Warm Gray com accent em ouro
- Espaço em branco generoso para respiração visual
- Hierarquia clara através de tamanho e peso
- Animações sutis e elegantes
- Interface intuitiva para comentários com edição e exclusão inline
- Seletor de emojis com 8 reações populares
- Visualização de reações com contagem e destaque para reações do usuário
- Página de perfil com estatísticas e abas para resenhas/comentários
- Barra de busca avançada com filtros colapsáveis

## Funcionalidades Principais

### Autenticação
- Login com Google OAuth
- Gerenciamento de sessão com JWT
- Armazenamento seguro de tokens

### Resenhas
- Criar novas resenhas com título, autor, gênero e conteúdo
- Visualizar feed de todas as resenhas
- Avaliar resenhas com sistema de likes
- Visualizar todas as resenhas do usuário no perfil
- Exibição de gênero nas resenhas

### Busca Avançada
- Buscar resenhas por título (case-insensitive)
- Buscar resenhas por autor (case-insensitive)
- Filtrar resenhas por gênero
- Combinar múltiplos filtros
- Visualizar número de resultados encontrados
- Limpar filtros e voltar ao feed completo
- Seletor de gêneros com lista dinâmica

### Comentários
- Visualizar comentários em cada resenha
- Criar novos comentários (usuários autenticados)
- Editar próprios comentários
- Deletar próprios comentários
- Exibição de data de criação e indicação de edição
- Visualizar todos os comentários do usuário no perfil

### Reações
- Reagir aos comentários com 8 emojis diferentes (👍, ❤️, 😂, 😮, 😢, 🔥, 🎉, 💯)
- Sistema de toggle (clicar novamente remove a reação)
- Visualização de reações agrupadas por emoji com contagem
- Destaque visual para reações do usuário atual
- Seletor intuitivo de emojis com preview

### Perfil de Usuário
- Visualizar informações do perfil (nome, email, avatar)
- Exibir estatísticas: resenhas, comentários, avaliações recebidas
- Abas para visualizar resenhas e comentários
- Links para voltar ao feed a partir de cada resenha/comentário
- Mensagens vazias quando não há resenhas ou comentários

## Próximas Melhorias

1. **Notificações em tempo real**: Implementar WebSockets para notificar usuários quando seus comentários recebem reações
2. **Respostas aninhadas**: Permitir que usuários respondam a comentários específicos criando threads
3. **Ranking de resenhas**: Adicionar sistema de ranking baseado em avaliações e reações para destacar as melhores resenhas
4. **Edição de perfil**: Permitir que usuários editem suas informações de perfil
5. **Seguir usuários**: Implementar sistema de seguimento para ver atividades de usuários favoritos
6. **Exportação de dados**: Permitir que usuários exportem suas resenhas em PDF ou CSV

## Troubleshooting

### Erro: "Failed to fetch reviews"
- Verifique se o API Gateway está rodando na porta 3001
- Verifique se o Review Service está rodando na porta 8878
- Verifique o CORS no API Gateway

### Erro: "Login failed"
- Verifique se o Google Client ID está configurado corretamente
- Verifique se o Auth Service está rodando na porta 8877
- Verifique se o token do Google é válido

### Erro: "Unauthorized"
- Verifique se o token JWT está sendo enviado corretamente no header
- Verifique se o token não expirou
- Verifique se a chave secreta do JWT é a mesma em todos os serviços

### Erro: "Failed to create comment"
- Verifique se o usuário está autenticado
- Verifique se o conteúdo do comentário não está vazio
- Verifique se o ID da resenha é válido

### Reações não aparecem
- Verifique se o Review Service está rodando
- Verifique se o usuário está autenticado para reagir
- Verifique o console do navegador para erros

### Perfil não carrega
- Verifique se o usuário está autenticado
- Verifique se todos os microsserviços estão rodando
- Verifique o console do navegador para erros de API

### Busca não retorna resultados
- Verifique se o Review Service está rodando
- Verifique se há resenhas com os critérios de busca
- Tente limpar os filtros e buscar novamente
- Verifique o console do navegador para erros

## Licença

MIT

## Autor

Sistema de Resenha de Livros com Comentários, Reações, Perfil de Usuário e Busca Avançada - Desenvolvido com NestJS, React e Microsserviços
