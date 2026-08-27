# Quick Start - Sistema de Resenha de Livros com Busca Avançada

## Pré-requisitos
- Node.js 18+
- pnpm

## 1. Instalar Dependências

```bash
# Auth Service
cd backend/auth-service
pnpm install

# Review Service
cd ../review-service
pnpm install

# API Gateway
cd ../gateway
pnpm install

# Frontend
cd ../../frontend
pnpm install
```

## 2. Executar os Microsserviços

Abra 3 terminais diferentes:

**Terminal 1 - Auth Service:**
```bash
cd backend/auth-service
pnpm start
# Deve exibir: "Auth Microservice is listening on port 8877"
```

**Terminal 2 - Review Service:**
```bash
cd backend/review-service
pnpm start
# Deve exibir: "Review Microservice is listening on port 8878"
```

**Terminal 3 - API Gateway:**
```bash
cd backend/gateway
pnpm start
# Deve exibir: "API Gateway is running on port 3001"
```

## 3. Executar o Frontend

Abra um novo terminal:

```bash
cd frontend
pnpm dev
# Deve exibir: "Local: http://localhost:3000/"
```

## 4. Acessar a Aplicação

Abra seu navegador e acesse: `http://localhost:3000`

## 5. Testar a Aplicação

### Sem Login (Visitante)
- Você pode visualizar todas as resenhas
- Você pode visualizar comentários em cada resenha
- Você pode visualizar reações aos comentários
- Você pode usar a busca avançada para encontrar resenhas
- Clique em "Explorar como visitante" na tela de login

### Com Login (Google)
- Clique em "Entrar com Google"
- Use sua conta Google para fazer login
- Após login, você poderá:
  - Criar novas resenhas com gênero
  - Avaliar resenhas de outros usuários
  - Criar comentários em resenhas
  - Editar seus próprios comentários
  - Deletar seus próprios comentários
  - Reagir aos comentários com emojis
  - Remover suas reações
  - Acessar seu perfil
  - Usar a busca avançada para encontrar resenhas

## 6. Testar o Sistema de Busca Avançada

1. **Expandir a barra de busca:**
   - Clique no ícone de seta (▶) na barra "Busca Avançada"
   - A barra se expandirá mostrando os campos de filtro

2. **Buscar por título:**
   - Digite um título de livro no campo "Título do Livro"
   - Clique em "Buscar" ou pressione Enter
   - Apenas resenhas com esse título serão exibidas

3. **Buscar por autor:**
   - Digite um nome de autor no campo "Autor"
   - Clique em "Buscar" ou pressione Enter
   - Apenas resenhas desse autor serão exibidas

4. **Filtrar por gênero:**
   - Clique no seletor "Gênero"
   - Escolha um gênero da lista
   - Clique em "Buscar"
   - Apenas resenhas desse gênero serão exibidas

5. **Combinar filtros:**
   - Preencha múltiplos campos (ex: título + gênero)
   - Clique em "Buscar"
   - Apenas resenhas que correspondem a TODOS os critérios serão exibidas

6. **Ver resultados:**
   - O número de resenhas encontradas é exibido
   - Status mostra "Mostrando resultados da busca"

7. **Limpar filtros:**
   - Clique em "Limpar" para remover todos os filtros
   - Todas as resenhas voltarão a ser exibidas

## 7. Testar o Sistema de Comentários

1. **Criar uma resenha:**
   - Clique em "Nova Resenha"
   - Preencha os dados (título, autor, gênero, conteúdo)
   - Clique em "Publicar Resenha"

2. **Visualizar comentários:**
   - Na resenha criada, clique em "Ver Comentários"
   - A seção de comentários será expandida

3. **Criar um comentário:**
   - Escreva seu comentário na caixa de texto
   - Clique em "Comentar"
   - Seu comentário aparecerá na lista

4. **Editar um comentário:**
   - Clique no ícone de edição (lápis) no seu comentário
   - Modifique o texto
   - Clique em "Confirmar" (ícone de check)

5. **Deletar um comentário:**
   - Clique no ícone de lixeira no seu comentário
   - O comentário será removido imediatamente

## 8. Testar o Sistema de Reações

1. **Adicionar uma reação:**
   - Em um comentário, clique no ícone de emoji (😊)
   - Selecione um emoji: 👍, ❤️, 😂, 😮, 😢, 🔥, 🎉, 💯
   - A reação será adicionada imediatamente

2. **Visualizar reações:**
   - Cada comentário mostra suas reações agrupadas por emoji
   - Cada grupo exibe o emoji e a contagem de reações
   - Suas reações aparecem com fundo destacado

3. **Remover uma reação:**
   - Clique em um emoji que você já reagiu
   - A reação será removida imediatamente

4. **Reagir com o mesmo emoji:**
   - Se você já reagiu com 👍, clicar novamente remove a reação
   - Clicar em outro emoji adiciona uma nova reação

## 9. Testar a Página de Perfil

1. **Acessar o perfil:**
   - Clique no ícone de perfil (👤) no header, ao lado do seu nome
   - Você será redirecionado para sua página de perfil

2. **Visualizar estatísticas:**
   - A página exibe seu nome, email e avatar
   - Mostra 3 estatísticas: número de resenhas, comentários e avaliações recebidas

3. **Visualizar suas resenhas:**
   - Clique na aba "Minhas Resenhas"
   - Todas as suas resenhas serão exibidas
   - Clique em "Ver Resenha Completa" para voltar ao feed

4. **Visualizar seus comentários:**
   - Clique na aba "Meus Comentários"
   - Todos os seus comentários serão exibidos com a resenha associada
   - Clique em "Ver Resenha" para voltar ao feed

5. **Voltar ao feed:**
   - Clique na seta de volta (←) no header
   - Você será redirecionado para o feed principal

## 10. Executar Testes Unitários

```bash
# Auth Service
cd backend/auth-service
pnpm test

# Review Service (incluindo testes de comentários e reações)
cd backend/review-service
pnpm test
```

**Resultados esperados:**
- Auth Service: 3 testes passando
- Review Service: 10 testes passando (incluindo testes de comentários e reações)

## Estrutura de Portas

| Serviço | Porta | URL |
|---------|-------|-----|
| Frontend | 3000 | http://localhost:3000 |
| API Gateway | 3001 | http://localhost:3001 |
| Auth Service | 8877 | TCP (microsserviço) |
| Review Service | 8878 | TCP (microsserviço) |

## Arquivos de Banco de Dados

Os bancos de dados SQLite são criados automaticamente:
- `backend/auth-service/auth.sqlite` - Dados de usuários
- `backend/review-service/reviews.sqlite` - Dados de resenhas, avaliações, comentários e reações

## Novidades - Busca Avançada

### Funcionalidades
- **Buscar por título**: Encontre resenhas pelo título do livro (case-insensitive)
- **Buscar por autor**: Encontre resenhas pelo nome do autor (case-insensitive)
- **Filtrar por gênero**: Selecione um gênero da lista dinâmica
- **Combinar filtros**: Use múltiplos critérios simultaneamente
- **Visualizar resultados**: Veja quantas resenhas foram encontradas
- **Limpar filtros**: Volte ao feed completo facilmente

### Endpoints da API
- `GET /reviews/search?title=...&author=...&genre=...` - Buscar resenhas com filtros
- `GET /genres` - Obter lista de gêneros disponíveis

### Componente SearchBar
- Barra de busca colapsável
- Campos de entrada para título e autor
- Seletor de gêneros com lista dinâmica
- Botões de busca e limpeza
- Status de busca ativa

## Fluxo Completo de Uso

1. **Fazer login** com sua conta Google
2. **Criar uma resenha** clicando em "Nova Resenha" (com gênero)
3. **Usar a busca avançada** para encontrar resenhas
4. **Visualizar comentários** em uma resenha
5. **Criar um comentário** em uma resenha de outro usuário
6. **Reagir aos comentários** com emojis
7. **Acessar seu perfil** para ver todas as suas contribuições
8. **Explorar as estatísticas** do seu perfil

## Troubleshooting

### "Port already in use"
Se uma porta já estiver em uso, você pode mudar a porta no arquivo `main.ts` do serviço correspondente.

### "Failed to connect to microservice"
Certifique-se de que todos os três microsserviços estão rodando antes de iniciar o API Gateway.

### "CORS error"
O CORS já está habilitado no API Gateway. Se o erro persistir, verifique se o frontend está acessando a URL correta do API Gateway.

### "Comentário não aparece"
- Verifique se você está autenticado
- Verifique se o Review Service está rodando
- Verifique o console do navegador para erros

### "Reações não funcionam"
- Verifique se você está autenticado
- Verifique se o Review Service está rodando
- Tente recarregar a página
- Verifique o console do navegador para erros

### "Perfil não carrega"
- Verifique se você está autenticado
- Verifique se todos os microsserviços estão rodando
- Verifique o console do navegador para erros

### "Busca não retorna resultados"
- Verifique se o Review Service está rodando
- Verifique se há resenhas com os critérios de busca
- Tente limpar os filtros e buscar novamente
- Verifique o console do navegador para erros

### "Gêneros não aparecem no seletor"
- Verifique se há resenhas com gêneros cadastrados
- Tente criar uma resenha com um gênero
- Recarregue a página para atualizar a lista de gêneros

## Próximos Passos

1. Configure o Google OAuth Client ID no arquivo `Login.tsx`
2. Crie várias resenhas com diferentes gêneros
3. Teste a busca avançada com diferentes combinações de filtros
4. Teste a edição e exclusão de comentários
5. Teste adicionar e remover reações
6. Acesse seu perfil e visualize suas contribuições
7. Verifique os testes unitários
8. Explore as funcionalidades de avaliação

Divirta-se! 📚💬😊👤🔍
