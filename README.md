# CadastroPessoa — Produtos e Vendas

Projeto de Programação I — Sistemas de Informação, UEMG.

Sistema em Java Swing e MySQL com cadastro de pessoas, cadastro de produtos e vendas com controle de estoque.

## Integrantes e participação

| Nome completo | Responsabilidade |
|---|---|
| Gabriela Oliveira Resende | Script SQL, classes de modelo, ProdutoDAO e integração no menu. |
| Camila Bianca Alves Gonzaga | Tela de cadastro de produtos e documentação de execução. |
| Ana Lívia Sousa Moreira | VendaDAO, transação e testes de banco. |
| Mariana Modesto Esper | Tela de vendas, carrinho, cálculo do total e testes de regras. |

## Como executar

1. Abra o projeto no NetBeans com JDK 17 ou superior.
2. Inicie o MySQL e execute o arquivo `banco_dados.sql`.
3. Copie `db.properties.example` para `db.properties` e configure a URL, o usuário e a senha do banco.
4. No NetBeans, selecione **Limpar e Construir** e depois **Executar projeto**.
5. Cadastre uma Pessoa Jurídica como fornecedor e uma Pessoa Física como cliente. Em **Produtos e vendas**, cadastre os produtos e registre as vendas.