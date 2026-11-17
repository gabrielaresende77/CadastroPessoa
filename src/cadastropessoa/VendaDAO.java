package cadastropessoa;
import java.sql.*;
import java.math.BigDecimal;

public class VendaDAO {
    public void finalizar(Venda venda) throws SQLException {
        if (venda == null || venda.getCliente() == null || venda.getCliente().getId() == null
                || venda.getCliente().getId() <= 0 || venda.getItens().isEmpty())
            throw new IllegalArgumentException("Selecione um cliente e adicione pelo menos um item.");
        for (ItemVenda item : venda.getItens()) {
            if (item == null || item.getProduto() == null || item.getProduto().getId() <= 0 || item.getQuantidade() <= 0)
                throw new IllegalArgumentException("Item inválido: informe produto e quantidade positiva.");
            Validacao.validarValor(item.getValorUnitario());
        }
        try (Connection con = Conexao.getConexao()) {
            con.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO venda(cliente_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, venda.getCliente().getId()); ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Não foi possível obter o número da venda.");
                        id = rs.getInt(1);
                    }
                }
                try (PreparedStatement baixa = con.prepareStatement(
                        "UPDATE produto SET estoque = estoque - ? WHERE id = ? AND estoque >= ?");
                     PreparedStatement itemSQL = con.prepareStatement(
                        "INSERT INTO item_venda(venda_id, produto_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)")) {
                    for (ItemVenda item : venda.getItens()) {
                        baixa.setInt(1, item.getQuantidade()); baixa.setInt(2, item.getProduto().getId());
                        baixa.setInt(3, item.getQuantidade());
                        if (baixa.executeUpdate() == 0)
                            throw new SQLException("Estoque insuficiente ou produto removido: "
                                + item.getProduto().getNome() + ". Nenhum item foi gravado.");
                        itemSQL.setInt(1, id); itemSQL.setInt(2, item.getProduto().getId());
                        itemSQL.setInt(3, item.getQuantidade()); itemSQL.setBigDecimal(4, item.getValorUnitario());
                        itemSQL.executeUpdate();
                    }
                }
                java.time.LocalDateTime data;
                try (PreparedStatement ps = con.prepareStatement("SELECT data_venda FROM venda WHERE id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Venda não encontrada.");
                        data = rs.getTimestamp(1).toLocalDateTime();
                    }
                }
                con.commit(); venda.setId(id); venda.setDataVenda(data);
            } catch (SQLException | RuntimeException ex) {
                try { con.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
            // Conexão exclusiva: o try-with-resources a fecha após commit ou rollback.
        }
    }
    public BigDecimal consultarTotal(int vendaId) throws SQLException {
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(quantidade * valor_unitario), 0) AS total FROM item_venda WHERE venda_id = ?")) {
            ps.setInt(1, vendaId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getBigDecimal("total"); }
        }
    }
}
