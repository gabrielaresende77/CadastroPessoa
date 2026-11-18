# CadastroPessoa — Produtos e Vendas

Trabalho de Programação I, Sistemas de Informação, UEMG — Unidade Passos, 2026/02.
Prazo informado no enunciado: **30/11/2026**. Entrega: link deste repositório por um integrante.

## Integrantes e participação


| Integrante | Nome completo | Responsabilidade proposta |
|---|---|---|
| 1 | Gabriela Oliveira Resende | SQL, modelos, ProdutoDAO, integração e correções da base |
| 2 | Camila Bianca Alves Gonzaga | Tela de produtos, checklist e documentação de execução |
| 3 | Ana Lívia Sousa Moreira | VendaDAO, transação e testes de banco |
| 4 | Mariana Modesto Esper | Tela de vendas, carrinho, total e testes de regras |

## O que o sistema faz

Mantém o CRUD original de pessoas e acrescenta produtos com fornecedor PJ e vendas para clientes PF. Os itens ficam no carrinho até a finalização. O DAO grava a venda, os itens e a baixa de estoque na mesma transação. A checagem na tela soma todas as linhas do mesmo produto; a checagem no banco usa atualização condicional. O valor de cada item é preservado mesmo se o preço do produto mudar.

## Preparação e execução

1. Instale/configure um JDK 17 ou superior e abra a pasta que contém `build.xml` pelo NetBeans (projeto Java com Ant). O projeto está configurado para linguagem Java 17.
2. Inicie seu servidor MySQL. Use MySQL 8.0.16 ou superior para os `CHECK` do enunciado serem aplicados. As tabelas devem usar InnoDB para transações e chaves estrangeiras.
3. No Workbench, abra e execute **todo o `banco_dados.sql`**. Ele cria uma base vazia ou acrescenta as tabelas ausentes à base existente, sem apagar cadastros. `IF NOT EXISTS` não corrige uma tabela existente com estrutura incorreta.
4. Copie `db.properties.example` para `db.properties` na raiz e ajuste usuário, senha e URL. Esse arquivo local está no `.gitignore`.
5. No NetBeans, use **Limpar e Construir** e depois **Executar projeto**. A classe principal é `cadastropessoa.CadastroPessoa`.
6. Em **Arquivo**, cadastre ao menos uma Pessoa Jurídica e uma Pessoa Física. Em **Produtos e vendas**, cadastre um produto com estoque e abra **Nova venda**.
7. Se cadastrar pessoas ou produtos com outra janela já aberta, use **Novo** na tela de produtos para recarregar fornecedores, ou **Atualizar opções** na tela de vendas. Reabra a tela quando necessário.

No Windows, também é possível abrir um terminal na raiz e executar `executar.cmd`. É preciso ter `javac` e `java` no PATH; execute `javac -version` para conferir. No NetBeans, configure uma plataforma JDK em Ferramentas > Plataformas Java se necessário.

O driver JDBC recebido no projeto original foi mantido em `src/lib/mysql-connector-java-8.0.11.jar`. O caminho é relativo e já está em `nbproject/project.properties`. Se houver erro de autenticação, confira as credenciais, as permissões do usuário e a compatibilidade do driver com seu servidor. A senha não deve ser incluída no repositório.

## Decisões de implementação

- As tabelas/colunas e relacionamentos seguem o enunciado, sem uma coluna `total` na venda.
- Dinheiro é representado por `BigDecimal` no Java e `DECIMAL(10,2)` no banco, evitando imprecisões nos cálculos. A sugestão de `double` no enunciado não é uma obrigação.
- Valores unitários devem ser positivos e ter até duas casas decimais. Use `12,50` ou `12.50`, sem separador de milhar.
- As novas telas são `JInternalFrame` escritos em Java. Não há `.form` para elas; edite a aba Fonte. Os `.form` originais foram preservados. O novo menu fica fora do trecho gerado pelo editor visual.
- A criação automática do banco foi substituída pela execução explícita do SQL no Workbench, como documentado acima. Isso permite configurar a conexão sem colocar credenciais no código.
- Foram corrigidos recursos JDBC abertos no código original e mensagens de sucesso indevidas quando uma operação com pessoas falha.
- Não foi acrescentada edição/exclusão de vendas na interface: o enunciado pede a finalização. `VendaDAO.consultarTotal` lê o total persistido a partir dos itens.

## Verificações

- Compilação de todos os fontes e testes: **aprovada** com compilador JDK 21, opções `-source 17 -target 17`. Houve apenas aviso sobre o caminho dos módulos; não foi um teste com um JDK 17 instalado.
- Testes de regras: **24 verificações aprovadas**.
- MySQL real, execução no NetBeans e interação visual: **pendentes de execução pelo grupo**. Não se declara aprovação desses testes sem executá-los.

Execute `testar_regras.cmd` no Windows. Para testar persistência, prepare uma **base separada de testes**, conforme `docs/TESTES.md`, e execute `testar_banco.cmd --base-de-testes`. O teste de banco cria registros identificados como TESTE e remove os próprios registros ao terminar; não utilize uma base com dados importantes.

Veja [checklist de exigências](docs/CHECKLIST.md), [roteiro de testes](docs/TESTES.md) e [guia do GitHub](docs/GUIA_GITHUB.md).

## Referências

- Enunciado fornecido: Trabalho prático em grupo, Programação I — 2026/02, Eduardo Henrique Marques Ferreira.
- [GitHub: clonar repositório](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository).
- [GitHub: convidar colaboradores](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/repository-access-and-collaboration/inviting-collaborators-to-a-personal-repository).
- [MySQL: CHECK constraints](https://dev.mysql.com/doc/refman/8.0/en/create-table-check-constraints.html).
- [MySQL: InnoDB](https://dev.mysql.com/doc/refman/8.4/en/innodb-introduction.html).
