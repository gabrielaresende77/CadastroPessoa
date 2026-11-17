package cadastropessoa;
import java.sql.*;
import java.math.BigDecimal;

/** Executar em uma base de testes preparada com banco_dados.sql. */
public class TesteBanco {
    private static void conferir(boolean ok, String mensagem) {
        if (!ok) throw new AssertionError(mensagem);
    }
    private static int inserir(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }
    private static int numero(Connection c, String sql, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }
    private static void executar(Connection c, String sql, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); ps.executeUpdate(); }
    }
    private static Venda venda(PessoaFisica cliente, Produto p, int qtd) {
        Venda v = new Venda(); v.setCliente(cliente); v.getItens().add(new ItemVenda(p, qtd, p.getPreco())); return v;
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !args[0].equals("--base-de-testes"))
            throw new IllegalArgumentException("Use --base-de-testes após configurar db.properties para uma base de testes.");
        int pj = 0, pf = 0, produtoId = 0;
        try (Connection c = Conexao.getConexao()) {
            try {
                pj = inserir(c, "INSERT INTO pessoa(nome,endereco,email) VALUES (?,?,?)", "TESTE Fornecedor", "Rua", "teste");
                inserir(c, "INSERT INTO pessoa_juridica(pessoa_id,cnpj,nome_fantasia) VALUES (?,?,?)", pj, "000", "Teste");
                pf = inserir(c, "INSERT INTO pessoa(nome,endereco,email) VALUES (?,?,?)", "TESTE Cliente", "Rua", "teste");
                inserir(c, "INSERT INTO pessoa_fisica(pessoa_id,cpf) VALUES (?,?)", pf, "111");
                PessoaJuridica fornecedor = new PessoaJuridica("TESTE Fornecedor", "Rua", "teste", "000", "Teste"); fornecedor.setId(pj);
                PessoaFisica cliente = new PessoaFisica("TESTE Cliente", "Rua", "teste", "111"); cliente.setId(pf);
                Produto p = new Produto(); p.setNome("TESTE Produto"); p.setPreco(new BigDecimal("12.50"));
                p.setEstoque(5); p.setFornecedor(fornecedor);
                ProdutoDAO produtos = new ProdutoDAO(); produtos.inserir(p); produtoId = p.getId();
                conferir(produtos.buscar(produtoId).getFornecedor().getId() == pj, "Fornecedor não persistido");
                p.setNome("TESTE Produto atualizado"); produtos.atualizar(p);
                conferir(produtos.buscar(produtoId).getNome().equals(p.getNome()), "Atualização não persistida");
                VendaDAO vendas = new VendaDAO(); Venda sucesso = venda(cliente, p, 2); vendas.finalizar(sucesso);
                conferir(produtos.buscar(produtoId).getEstoque() == 3, "Baixa incorreta");
                conferir(vendas.consultarTotal(sucesso.getId()).compareTo(new BigDecimal("25.00")) == 0, "Total do banco incorreto");
                // Primeiro item cabe; o segundo falha: até a primeira baixa precisa ser desfeita.
                Venda falha = venda(cliente, p, 2); falha.getItens().add(new ItemVenda(p, 2, p.getPreco()));
                boolean bloqueou = false;
                try { vendas.finalizar(falha); } catch (SQLException ex) { bloqueou = true; }
                conferir(bloqueou, "Venda sem estoque aceita");
                conferir(falha.getId() == 0, "Venda falha recebeu ID de sucesso");
                conferir(produtos.buscar(produtoId).getEstoque() == 3, "Rollback não restaurou estoque");
                conferir(numero(c, "SELECT COUNT(*) FROM venda WHERE cliente_id = ?", pf) == 1, "Venda parcial persistida");
                conferir(numero(c, "SELECT COUNT(*) FROM item_venda WHERE produto_id = ?", produtoId) == 1, "Item parcial persistido");
                bloqueou = false;
                try { produtos.excluir(produtoId); } catch (SQLException ex) { bloqueou = true; }
                conferir(bloqueou, "Produto vendido foi excluído");
                bloqueou = false;
                try { executar(c, "DELETE FROM pessoa WHERE id = ?", pj); } catch (SQLException ex) { bloqueou = true; }
                conferir(bloqueou, "Fornecedor vinculado foi excluído");
                bloqueou = false;
                try { executar(c, "DELETE FROM pessoa WHERE id = ?", pf); } catch (SQLException ex) { bloqueou = true; }
                conferir(bloqueou, "Cliente vinculado foi excluído");
                p.setEstoque(3); p.setPreco(new BigDecimal("99.00")); produtos.atualizar(p);
                conferir(vendas.consultarTotal(sucesso.getId()).compareTo(new BigDecimal("25.00")) == 0, "Preço histórico alterado");
                executar(c, "DELETE FROM venda WHERE id = ?", sucesso.getId());
                conferir(numero(c, "SELECT COUNT(*) FROM item_venda WHERE produto_id = ?", produtoId) == 0, "Cascade dos itens falhou");
                produtos.excluir(produtoId); conferir(produtos.buscar(produtoId) == null, "Produto não excluído");
                System.out.println("OK: CRUD de produto, venda, total, rollback, vínculos e preço histórico.");
            } finally {
                // Remove apenas registros criados por este teste, em ordem de dependência.
                if (pf != 0) executar(c, "DELETE FROM venda WHERE cliente_id = ?", pf);
                if (produtoId != 0) executar(c, "DELETE FROM produto WHERE id = ?", produtoId);
                if (pf != 0) executar(c, "DELETE FROM pessoa WHERE id = ?", pf);
                if (pj != 0) executar(c, "DELETE FROM pessoa WHERE id = ?", pj);
            }
        }
    }
}
