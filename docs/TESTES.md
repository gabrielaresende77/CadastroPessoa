# Roteiro de testes e apresentação

## O que já foi executado

Compilação dos fontes da aplicação e dos dois testes com o compilador JDK 21, linguagem/alvo 17: passou, com um aviso de caminho de módulos. Execução de `TesteRegras`: **24 verificações passaram**. Esse teste cobre entradas inválidas, dinheiro com vírgula, centavos, total, remoção, estoque somado por produto e validação de fornecedor. Ele não substitui testes com MySQL e Swing.

`TesteBanco` foi compilado, mas **não foi executado**, pois não há MySQL disponível no ambiente de preparação. A interface e o projeto Ant também precisam ser executados no computador do grupo.

## 1. Preparar o banco

Execute `banco_dados.sql` no Workbench e configure `db.properties`, como explica o README. Confira:

```sql
SELECT VERSION();
SELECT TABLE_NAME, ENGINE FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'cadastro_pessoa';
SHOW CREATE TABLE produto;
SHOW CREATE TABLE venda;
SHOW CREATE TABLE item_venda;
```

As seis tabelas devem existir, usar InnoDB e mostrar as FKs/CHECKs do enunciado. Se já existirem com estrutura diferente, `IF NOT EXISTS` não irá alterá-las. Não apague sua base sem antes preservar os dados.

## 2. Testar as telas manualmente

Registre resultado, responsável e data na última coluna. Crie pessoas fictícias, duas PJs (Fornecedor A e B), uma PF (Cliente A), produto Caneta a R$ 2,50 com estoque 10 e produto Caderno a R$ 12,00 com estoque 2.

| Teste | Ação e resultado esperado | Resultado do grupo |
|---|---|---|
| Cadastro de pessoas | Gravar, navegar, alterar e excluir uma PF e uma PJ sem vínculos; reabrir e conferir dados | Pendente |
| P1 | Gravar Caneta com Fornecedor A; somente PJs aparecem no combo | Pendente |
| P2 | Navegar pelos produtos com ◀/▶ e usar Listar; nome do fornecedor aparece | Pendente |
| P3 | Alterar nome/preço/estoque e trocar Fornecedor A por B; reabrir e conferir | Pendente |
| P4 sem vínculo | Criar produto descartável; cancelar exclusão, conferir que existe; confirmar e conferir que sumiu | Pendente |
| P5 | Tentar nome vazio/espaços, preço 0/negativo/texto, estoque negativo/fracionado e fornecedor ausente; bloquear | Pendente |
| V1 | No combo de clientes aparecem somente PFs | Pendente |
| V2/V3 | Repor valores iniciais. Adicionar 2 Canetas e 1 Caderno: total R$ 17,00. Remover Caderno: R$ 5,00 | Pendente |
| V4 | Com 2 Canetas no carrinho, adicionar 9 Canetas: bloquear, pois 2 + 9 > 10 | Pendente |
| V4 limite | Acrescentar 8 Canetas: aceitar, total 10 unidades; remover a linha de 8 antes de finalizar | Pendente |
| V5 | Finalizar 2 Canetas; consultar banco: venda, um item de 2, estoque 8 e total R$ 5,00 | Pendente |
| Persistência | Fechar e reabrir aplicação; produto e estoque continuam corretos | Pendente |
| P4 com vínculo | Tentar excluir Caneta vendida: bloquear com mensagem clara | Pendente |
| Vínculos de pessoas | Tentar excluir fornecedor com produto e cliente com venda: bloquear sem mensagem de sucesso | Pendente |
| Preço histórico | Alterar preço atual da Caneta; total da venda antiga continua R$ 5,00 | Pendente |
| Campos da venda | Bloquear quantidade 0/negativa/fracionada/texto, preço inválido e venda sem itens | Pendente |
| Sem cadastros | Em base vazia, avisar para cadastrar PF/PJ/produto, sem encerrar inesperadamente | Pendente |

## 3. Conferir o que foi gravado

Execute no Workbench após uma venda, substituindo o número do ID quando necessário:

```sql
SELECT * FROM produto ORDER BY id;
SELECT v.id, v.data_venda, p.nome AS cliente,
       SUM(i.quantidade * i.valor_unitario) AS total
FROM venda v
JOIN pessoa p ON p.id = v.cliente_id
JOIN item_venda i ON i.venda_id = v.id
GROUP BY v.id, v.data_venda, p.nome ORDER BY v.id;
SELECT * FROM item_venda ORDER BY id;
```

## 4. Demonstrar concorrência e rollback completo

1. Crie produtos X e Y, cada um com estoque 1. Abra duas janelas de venda (A e B).
2. Na A, adicione X e depois Y, uma unidade de cada, sem finalizar.
3. Na B, adicione Y e finalize. Agora Y tem estoque 0.
4. Finalize A. O banco deve rejeitar Y, depois de ter tentado baixar X.
5. Confira: X continua com estoque 1, Y com estoque 0. Só a venda B e seu item existem. Nenhum item da venda A ficou gravado.
6. O carrinho da A deve permanecer na tela para revisão. Remova Y e finalize: agora X pode ser vendido.

A conferência de estoque na tela melhora a experiência; o UPDATE condicional e a transação garantem o resultado quando há concorrência. O primeiro item precisa voltar ao estoque quando o segundo falha.

## 5. Teste automatizado com banco separado

1. Crie uma cópia **temporária** de `banco_dados.sql`, fora do repositório, trocando somente o nome da base nas duas primeiras linhas de `cadastro_pessoa` para `cadastro_pessoa_teste`.
2. Execute a cópia no Workbench. Não altere o SQL oficial entregue.
3. Em `db.properties`, aponte a URL para `jdbc:mysql://localhost:3306/cadastro_pessoa_teste`.
4. No terminal do Windows, dentro da raiz do projeto, execute:

```bat
testar_banco.cmd --base-de-testes
```

5. Espere a mensagem `OK: CRUD de produto, venda, total, rollback, vínculos e preço histórico.` Se houver falha, guarde a mensagem e corrija antes de registrar o teste como aprovado.
6. O teste usa JDBC real e tenta limpar somente os registros que ele criou. Uma interrupção pode deixar registros TESTE na base de testes.
7. Volte a URL de `db.properties` para `cadastro_pessoa` antes da apresentação.

## 6. Roteiro de apresentação por pessoa

1. Integrante 1: explique as FKs, os objetos fornecedor/cliente, os parâmetros `?` do ProdutoDAO e como preparar a base.
2. Integrante 2: demonstre cadastrar, navegar, atualizar/trocar fornecedor, listar e excluir produto, incluindo um erro de validação.
3. Integrante 4: selecione PF, adicione/remova itens, mostre total e bloqueio de estoque somado.
4. Integrante 3: finalize, confira tabelas e explique `setAutoCommit(false)`, `commit`, `rollback` e `executeUpdate() == 0`.
5. Todos: mostrem seus commits reais e expliquem as partes revisadas, implementadas e testadas.
