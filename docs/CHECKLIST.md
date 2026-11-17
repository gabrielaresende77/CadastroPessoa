# Conferência das exigências do enunciado

**Legenda:** Implementado = conferido no código; Testado = executado aqui; Pendente = exige execução no computador/banco do grupo ou ação no GitHub. Implementado não significa pontuação garantida: o professor avalia o funcionamento e a participação real.

## Funcionalidades — 15 pontos

| Item | Pontos | Onde está atendido | Situação |
|---|---:|---|---|
| P1 — cadastrar produto com PJ no combo | 2,0 | TelaCadProduto, ProdutoDAO.inserir, FK fornecedor_id | Implementado; teste MySQL/tela pendente |
| P2 — navegar/listar com nome do fornecedor | 1,0 | TelaCadProduto.navegar/listar; JOIN em ProdutoDAO | Implementado; teste MySQL/tela pendente |
| P3 — atualizar e trocar fornecedor | 1,5 | TelaCadProduto.gravar e ProdutoDAO.atualizar | Implementado; teste MySQL/tela pendente |
| P4 — excluir com confirmação e erro se vendido | 1,0 | TelaCadProduto.excluir; tratamento MySQL 1451 no DAO | Implementado; teste MySQL/tela pendente |
| P5 — obrigatórios, preço positivo, estoque não negativo | 1,5 | Validacao, tela, DAO e CHECKs SQL | Regras Java testadas; CHECKs/tela pendentes |
| V1 — selecionar cliente PF | 1,0 | TelaVenda.carregar filtra PessoaFisica | Implementado; teste MySQL/tela pendente |
| V2 — adicionar/remover itens com quantidade/valor | 2,0 | TelaVenda, JTable não editável e ItemVenda | Implementado; teste visual pendente |
| V3 — total da venda | 1,0 | ItemVenda.getSubtotal, Venda.getTotal, atualizarTabela | Cálculos testados; rótulo visual pendente |
| V4 — bloquear estoque insuficiente somando itens repetidos | 2,0 | Validacao.estoque; busca atual do produto antes de adicionar | Soma testada; integração pendente |
| V5 — gravar venda/itens/baixa em transação | 2,0 | VendaDAO.finalizar, UPDATE condicional, commit/rollback | Implementado; teste MySQL pendente |

## Código — 12 pontos

| Critério | Pontos | Evidência / situação |
|---|---:|---|
| Modelos corretos | 3 | Produto com PessoaJuridica; Venda com PessoaFisica e List<ItemVenda>; ItemVenda com produto/quantidade/valor. Implementado |
| DAOs com PreparedStatement e recursos fechados | 4 | PessoaDAO, ProdutoDAO e VendaDAO usam try-with-resources. Valores variáveis são parâmetros `?`; SELECTs sem filtro não precisam de parâmetros |
| Transação de venda | 2 | Uma conexão, autocommit desativado, UPDATE obrigatório e rollback em falha. Implementado; execução real pendente |
| Organização | 3 | Camadas de modelo, DAO, validação e telas; código novo comentado onde útil. Avaliação final do professor |

## Telas — 8 pontos

| Critério | Pontos | Evidência / situação |
|---|---:|---|
| Produtos | 3 | JInternalFrame, nome/preço/estoque, combo PJ, Novo/Gravar/◀/▶/Excluir/Listar |
| Vendas | 3 | JInternalFrame, combo PF/produto, preço preenchido, quantidade, JTable, adicionar/remover, total/finalizar |
| Menu | 1 | TelaPrincipal > Produtos e vendas, com as duas telas |
| Mensagens | 1 | Erros de validação, confirmação de exclusão, FK e estoque; sucesso só após operação aceita |

Todas implementadas; inspeção interativa pendente.

## Aplicação rodando — 5 pontos

| Critério | Pontos | Situação |
|---|---:|---|
| Compila | 2 | Testado: todos os fontes e testes compilam com JDK 21 em modo source/target 17. Build Ant/NetBeans ainda deve ser conferido |
| Executa e demonstra cadastro + venda | 3 | Pendente no computador do grupo com MySQL |

## Modelo de dados e regras gerais

| Exigência | Conferência |
|---|---|
| Preservar tabelas/colunas de pessoas | Mantidas, sem alteração de nomes/tipos; removidos apenas INSERTs de exemplo do script original |
| Acrescentar produto, venda e item_venda ao banco_dados.sql | Incluídas ao final com os nomes e tipos do enunciado |
| FK fornecedor → PJ; cliente → PF | Presentes |
| CHECK preço > 0, estoque >= 0, quantidade > 0 | Presentes; dependem de MySQL com suporte efetivo |
| ON DELETE CASCADE dos itens ao excluir venda | Presente; sem CASCADE no fornecedor/cliente/produto |
| Valor histórico por item | valor_unitario gravado no item; não depende de preço futuro |
| Total sem coluna extra | Calculado em Java no carrinho e por SUM no DAO/consulta SQL |
| Dados persistidos | DAOs usam JDBC real; só o carrinho não finalizado fica em memória |
| Partir do projeto de aula | Mantidos pacote, modelos de pessoas, telas originais e estrutura NetBeans |
| .gitignore antes do primeiro commit | Incluído na etapa inicial: build/, dist/, nbproject/private/ e configuração local |
| Projeto completo | src/, nbproject/ sem private/, build.xml, manifest.mf, driver relativo e SQL incluídos |

## Pendências obrigatórias antes de entregar

- [ ] Os quatro integrantes revisaram, adaptaram e testaram suas partes.
- [ ] Os quatro nomes completos e contribuições reais estão no README.
- [ ] Todos têm commits relevantes associados às próprias contas.
- [ ] O grupo executou o roteiro de TESTES.md e corrigiu eventuais falhas do ambiente.
- [ ] O projeto foi aberto, construído e executado no NetBeans com MySQL.
- [ ] O repositório está público ou compartilhado com o professor.
- [ ] Um integrante enviou o link até 30/11/2026; commits posteriores ao prazo não contam.

Não há repositório criado nem commits publicados nesta preparação. As pastas de etapas são propostas de organização e não constituem histórico de participação.
