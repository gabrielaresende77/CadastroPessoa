package cadastropessoa;
import java.sql.*;
import java.util.*;

public class ProdutoDAO {
    private static final String SELECT = "SELECT pr.*, p.nome AS fornecedor_nome, p.endereco, p.email, "
        + "pj.cnpj, pj.nome_fantasia FROM produto pr "
        + "JOIN pessoa_juridica pj ON pj.pessoa_id = pr.fornecedor_id "
        + "JOIN pessoa p ON p.id = pj.pessoa_id ";
    public void inserir(Produto produto) throws SQLException {
        Validacao.produto(produto);
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(
                "INSERT INTO produto(nome, preco, estoque, fornecedor_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            parametros(ps, produto); ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) produto.setId(rs.getInt(1)); }
        }
    }
    public void atualizar(Produto produto) throws SQLException {
        Validacao.produto(produto);
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(
                "UPDATE produto SET nome = ?, preco = ?, estoque = ?, fornecedor_id = ? WHERE id = ?")) {
            parametros(ps, produto); ps.setInt(5, produto.getId());
            if (ps.executeUpdate() == 0) throw new SQLException("Produto não encontrado. Atualize a listagem.");
        }
    }
    private void parametros(PreparedStatement ps, Produto p) throws SQLException {
        ps.setString(1, p.getNome().trim()); ps.setBigDecimal(2, p.getPreco());
        ps.setInt(3, p.getEstoque()); ps.setInt(4, p.getFornecedor().getId());
    }
    public void excluir(int id) throws SQLException {
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement("DELETE FROM produto WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) throw new SQLException("Produto não encontrado.");
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451)
                throw new SQLException("Não é possível excluir este produto: ele já foi vendido.", ex);
            throw ex;
        }
    }
    public List<Produto> listar() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(SELECT + "ORDER BY pr.id");
             ResultSet rs = ps.executeQuery()) { while (rs.next()) produtos.add(ler(rs)); }
        return produtos;
    }
    public Produto buscar(int id) throws SQLException {
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(SELECT + "WHERE pr.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? ler(rs) : null; }
        }
    }
    private Produto ler(ResultSet rs) throws SQLException {
        PessoaJuridica fornecedor = new PessoaJuridica(rs.getString("fornecedor_nome"),
            rs.getString("endereco"), rs.getString("email"), rs.getString("cnpj"), rs.getString("nome_fantasia"));
        fornecedor.setId(rs.getInt("fornecedor_id"));
        Produto p = new Produto(); p.setNome(rs.getString("nome")); p.setPreco(rs.getBigDecimal("preco"));
        p.setEstoque(rs.getInt("estoque")); p.setFornecedor(fornecedor); p.setId(rs.getInt("id")); return p;
    }
}
