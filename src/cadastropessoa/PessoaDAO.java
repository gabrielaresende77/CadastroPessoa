package cadastropessoa;
import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

public class PessoaDAO {
    public boolean inserir(Pessoa pessoa) { return gravar(pessoa, true); }
    public boolean atualizar(Pessoa pessoa) { return gravar(pessoa, false); }

    private boolean gravar(Pessoa p, boolean novo) {
        try (Connection con = Conexao.getConexao()) {
            con.setAutoCommit(false);
            try {
                int id = p.getId();
                String sql = novo ? "INSERT INTO pessoa(nome, endereco, email) VALUES (?, ?, ?)"
                    : "UPDATE pessoa SET nome = ?, endereco = ?, email = ? WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, p.getNome()); ps.setString(2, p.getEndereco()); ps.setString(3, p.getEmail());
                    if (!novo) ps.setInt(4, id);
                    if (ps.executeUpdate() == 0) throw new SQLException("Pessoa não encontrada.");
                    if (novo) {
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (!rs.next()) throw new SQLException("Identificador não retornado.");
                            id = rs.getInt(1);
                        }
                    }
                }
                if (p instanceof PessoaFisica) {
                    sql = novo ? "INSERT INTO pessoa_fisica(cpf, pessoa_id) VALUES (?, ?)"
                        : "UPDATE pessoa_fisica SET cpf = ? WHERE pessoa_id = ?";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, ((PessoaFisica)p).getCpf()); ps.setInt(2, id); ps.executeUpdate();
                    }
                } else if (p instanceof PessoaJuridica) {
                    sql = novo ? "INSERT INTO pessoa_juridica(cnpj, nome_fantasia, pessoa_id) VALUES (?, ?, ?)"
                        : "UPDATE pessoa_juridica SET cnpj = ?, nome_fantasia = ? WHERE pessoa_id = ?";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, ((PessoaJuridica)p).getCnpj());
                        ps.setString(2, p.getNomeFantasia()); ps.setInt(3, id); ps.executeUpdate();
                    }
                } else throw new SQLException("Tipo de pessoa inválido.");
                con.commit(); p.setId(id); return true;
            } catch (SQLException | RuntimeException ex) {
                try { con.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível gravar a pessoa: " + ex.getMessage());
            return false;
        }
    }
    public boolean excluir(Pessoa pessoa) {
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement("DELETE FROM pessoa WHERE id = ?")) {
            ps.setInt(1, pessoa.getId());
            if (ps.executeUpdate() == 0) throw new SQLException("Pessoa não encontrada.");
            return true;
        } catch (SQLException ex) {
            String mensagem = ex.getErrorCode() == 1451
                ? "Não é possível excluir: fornecedor com produtos ou cliente com vendas vinculadas."
                : "Não foi possível excluir a pessoa: " + ex.getMessage();
            JOptionPane.showMessageDialog(null, mensagem); return false;
        }
    }
    public List<Pessoa> listar() {
        try { return listarDoBanco(); }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar pessoas: " + ex.getMessage());
            return new ArrayList<>();
        }
    }
    public List<Pessoa> listarDoBanco() throws SQLException {
        String sql = "SELECT p.*, pf.cpf, pj.cnpj, pj.nome_fantasia FROM pessoa p "
            + "LEFT JOIN pessoa_fisica pf ON p.id = pf.pessoa_id "
            + "LEFT JOIN pessoa_juridica pj ON p.id = pj.pessoa_id ORDER BY p.id";
        List<Pessoa> pessoas = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pessoa p;
                if (rs.getString("cpf") != null)
                    p = new PessoaFisica(rs.getString("nome"), rs.getString("endereco"), rs.getString("email"), rs.getString("cpf"));
                else p = new PessoaJuridica(rs.getString("nome"), rs.getString("endereco"), rs.getString("email"),
                    rs.getString("cnpj"), rs.getString("nome_fantasia"));
                p.setId(rs.getInt("id")); pessoas.add(p);
            }
        }
        return pessoas;
    }
}
