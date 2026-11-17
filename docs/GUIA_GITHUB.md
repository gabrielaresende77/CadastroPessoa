# GitHub passo a passo para quatro integrantes

## 1. Entender as pastas do pacote

- `CadastroPessoa/`: versão completa para abrir, estudar e testar agora. Não copie tudo dela para fazer um único commit final.
- `etapas/`: alterações separadas em ordem, cada pasta contendo somente os arquivos da etapa e os caminhos corretos. A etapa 01 contém o projeto de aula limpo, com `.gitignore`.
- `LEIA_PRIMEIRO.md`: visão geral do pacote.

Use o código como base de estudo e revisão. Antes de publicar uma etapa, o responsável deve entender o que ela faz, fazer as adaptações necessárias e verificar seu funcionamento. Não basta trocar o autor de commits ou distribuir arquivos para aparentar participação. Cada pessoa trabalha com a própria conta e descreve sua contribuição real no README, inclusive revisão e testes. As etapas não fazem commits automaticamente.

## 2. Definir os responsáveis

| Integrante | Responsabilidade principal | Etapas |
|---|---|---|
| 1 | Banco, modelos, ProdutoDAO e integração; reúne tarefas 1 e 2 sugeridas pelo professor | 01, 02, 03, 04 e 08 |
| 2 | Tela de produtos e documentação/checklist | 05 e 11 |
| 3 | VendaDAO e teste de banco | 06 e 10 |
| 4 | Tela de vendas e teste de regras | 07 e 09 |

Combinem os nomes antes de começar. A ordem numérica permite integrar as dependências gradualmente. Depois da etapa 11, cada integrante também atualiza sua linha no README com nome completo, tarefas e resultados realmente verificados.

## 3. Preparar cada computador

Cada integrante precisa de sua própria conta GitHub, Git instalado, JDK, NetBeans e acesso ao MySQL. No **Git Bash**, configure sua identidade:

```bash
git config --global user.name "SEU NOME COMPLETO"
git config --global user.email "EMAIL ASSOCIADO A SUA CONTA GITHUB"
git config --global --get user.name
git config --global --get user.email
```

Use um e-mail verificado na sua conta, ou o endereço `noreply` exibido nas configurações de e-mail do GitHub. Configurar nome/e-mail identifica o autor do commit; a autenticação para enviar mudanças é separada. Quando o Git pedir autenticação, siga o fluxo do gerenciador de credenciais com a sua conta. Não compartilhem senhas.

## 4. Criar um único repositório — integrante 1

1. No GitHub, escolha **New repository**.
2. Nome sugerido: `cadastro-pessoa-produtos-vendas`.
3. Escolha público, ou privado se for compartilhar com o professor antes da entrega.
4. Para seguir estes comandos, deixe desmarcadas as opções de iniciar com README, `.gitignore` e licença. Os arquivos virão da etapa 01.
5. Crie o repositório e copie a URL HTTPS exibida em **Code**.
6. Em **Settings > Collaborators > Add people**, convide as outras três contas. Cada pessoa aceita o convite.
7. Em uma pasta de trabalho, abra Git Bash e substitua a URL abaixo pela URL real:

```bash
git clone https://github.com/USUARIO/cadastro-pessoa-produtos-vendas.git
cd cadastro-pessoa-produtos-vendas
git switch -c main
```

O aviso de repositório vazio é esperado. Se o clone já estiver na branch `main`, não é necessário executar `git switch -c main`.

8. Copie **o conteúdo** de `etapas/01_base/` para dentro da pasta clonada. O `build.xml` deve ficar diretamente na raiz do repositório, não dentro de outra pasta `CadastroPessoa`.
9. Confira que `.gitignore` foi copiado antes de adicionar arquivos:

```bash
git status --short
git add .gitignore src nbproject build.xml manifest.mf banco_dados.sql
git diff --cached --stat
git commit -m "Importa projeto de aula e configura arquivos ignorados"
git push -u origin main
```

Este primeiro commit importa a base já existente. As novas funcionalidades serão acrescentadas separadamente. Não publique build/, dist/ ou nbproject/private/.

## 5. Clonar nos outros três computadores

Após o primeiro envio, cada colega aceita o convite e executa:

```bash
git clone https://github.com/USUARIO/cadastro-pessoa-produtos-vendas.git
cd cadastro-pessoa-produtos-vendas
git status
```

Todos trabalham no **mesmo repositório**. Não criem quatro repositórios independentes e não compartilhem uma mesma pasta `.git` por ZIP.

## 6. Aplicar as etapas em sequência

O fluxo abaixo usa a branch `main` e uma pessoa por vez para facilitar o início. Esperem o envio da etapa anterior antes de começar a seguinte. O trabalho não exige pull requests; se adotarem branches, preservem os commits de cada autor ao integrar.

Antes de cada etapa, o responsável executa:

```bash
git status
git pull --ff-only origin main
```

O diretório deve estar sem alterações pendentes. Copie somente o conteúdo da pasta da etapa para a raiz do clone, mesclando as pastas e substituindo apenas os arquivos correspondentes. Não copie a própria pasta `etapas` para dentro do projeto. Reveja o código, adapte o necessário e teste. Depois:

```bash
git diff --stat
git diff
```

Use o `git add` e a mensagem correspondentes à etapa na tabela abaixo; então envie:

```bash
git diff --cached --stat
git commit -m "MENSAGEM DA ETAPA"
git push origin main
```

| Etapa/pasta | Responsável | Arquivos para `git add` | Mensagem sugerida |
|---|---|---|---|
| 02_infraestrutura | 1 | `src/cadastropessoa/Conexao.java src/cadastropessoa/CadastroPessoa.java src/cadastropessoa/PessoaDAO.java src/cadastropessoa/TelaPrincipal.java src/cadastropessoa/TelaCadPessoaFisica.java src/cadastropessoa/TelaCadPessoaJuridica.java nbproject/project.properties db.properties.example` | Corrige conexoes JDBC e mensagens do cadastro de pessoas |
| 03_modelos_sql | 1 | `banco_dados.sql src/cadastropessoa/Produto.java src/cadastropessoa/Venda.java src/cadastropessoa/ItemVenda.java src/cadastropessoa/Validacao.java` | Adiciona tabelas modelos e validacoes de produtos e vendas |
| 04_produto_dao | 1 | `src/cadastropessoa/ProdutoDAO.java` | Implementa CRUD de produtos com fornecedor |
| 05_tela_produto | 2 | `src/cadastropessoa/TelaCadProduto.java` | Cria cadastro navegacao e listagem de produtos |
| 06_venda_dao | 3 | `src/cadastropessoa/VendaDAO.java` | Grava venda e itens com baixa de estoque e rollback |
| 07_tela_venda | 4 | `src/cadastropessoa/TelaVenda.java` | Cria carrinho de venda com total e verificacao de estoque |
| 08_menu | 1 | `src/cadastropessoa/TelaPrincipal.java` | Integra produtos e vendas ao menu principal |
| 09_teste_regras | 4 | `test/cadastropessoa/TesteRegras.java` | Testa valores totais e estoque de itens repetidos |
| 10_teste_banco | 3 | `test/cadastropessoa/TesteBanco.java` | Adiciona teste JDBC de persistencia rollback e vinculos |
| 11_documentacao | 2 | `README.md docs executar.cmd testar_regras.cmd testar_banco.cmd` | Documenta execucao testes e conferencia do enunciado |

Exemplo completo da etapa 04, depois de copiar, revisar e verificar `ProdutoDAO.java`:

```bash
git add src/cadastropessoa/ProdutoDAO.java
git diff --cached
git commit -m "Implementa CRUD de produtos com fornecedor"
git push origin main
```

As novas telas ficam acessíveis pelo menu a partir da etapa 08. Não interpretem a ausência do menu nas etapas anteriores como erro. A versão completa em `CadastroPessoa/` permite estudar o resultado integrado desde o começo. Na etapa 02, a criação automática da base é substituída pela preparação no Workbench; executem o script original nessa etapa e o atualizado após a etapa 03.

## 7. Preencher o README e registrar os testes reais

Depois da etapa 11, em sequência, cada integrante:

1. Executa `git pull --ff-only origin main`.
2. Abre `README.md`, substitui somente seu campo `PREENCHER NOME COMPLETO` e descreve o que realmente fez.
3. Executa os testes da sua parte. Em `docs/TESTES.md`, registra resultado, data e responsável nas linhas correspondentes. Não marque testes não executados como aprovados.
4. Faz um commit com mensagem específica e envia:

```bash
git add README.md docs/TESTES.md
git commit -m "Registra participacao e resultados dos testes de produtos"
git push origin main
```

Adapte a mensagem ao trabalho realizado. O README final precisa ter os quatro nomes completos. As correções encontradas nos testes devem ser commits adicionais pequenos, feitos por quem as resolveu; não alterem autores nem datas para fabricar um histórico.

## 8. Resolver dificuldades comuns

- **Push rejeitado porque há novidades:** não use `--force`. Com mudanças locais já commitadas, execute `git pull --rebase origin main`, resolva conflitos se surgirem e só então `git push origin main`.
- **Conflito:** abra os arquivos apontados por `git status`, combine a versão correta com o colega, remova os marcadores `<<<<<<<`, `=======`, `>>>>>>>`, use `git add ARQUIVO` e `git rebase --continue`. Para desistir daquele rebase e voltar ao estado anterior, `git rebase --abort`.
- **Arquivo .form em conflito:** evitem editar a mesma tela original ao mesmo tempo. As telas novas deste pacote não usam `.form`.
- **Arquivo não aparece no commit:** confira `git status`, `git diff --cached` e se ele foi copiado para a raiz certa. `db.properties` deve continuar ignorado.
- **Erro de `javac`:** selecione/instale o JDK e confira o PATH. Só o runtime Java não basta para os scripts `.cmd`.
- **Erro de banco:** confira se o MySQL está iniciado, se o SQL foi executado e se `db.properties` tem as credenciais corretas. Senha não é commitada.

## 9. Conferência final e envio

Um integrante clona o repositório em uma pasta nova e segue o README do zero. Isso ajuda a descobrir arquivos que ficaram apenas no computador de alguém.

```bash
git log --oneline --all
git shortlog -sne HEAD
git ls-files build dist nbproject/private
git status
```

Confiram os quatro autores e commits relevantes; o comando `git ls-files build dist nbproject/private` deve ficar sem saída. No GitHub, abram o histórico e verifiquem se os commits estão associados às contas corretas.

O repositório deve conter `src/`, `nbproject/`, `build.xml`, `manifest.mf`, `banco_dados.sql` atualizado e README preenchido. Testem P1–P5 e V1–V5, especialmente o rollback do segundo item, antes de entregar. Se o repositório for privado, compartilhem com o professor e confirmem o acesso. **Um integrante envia o link até 30/11/2026. Commits posteriores ao prazo não serão considerados.**

## Referências oficiais

- [Convidar colaboradores](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/repository-access-and-collaboration/inviting-collaborators-to-a-personal-repository).
- [Clonar um repositório](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository).
