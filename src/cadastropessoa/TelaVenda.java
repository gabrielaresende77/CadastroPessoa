package cadastropessoa;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** A lista mantém somente o carrinho; vendas finalizadas são persistidas pelo DAO. */
public class TelaVenda extends JInternalFrame {
    private final JComboBox<String> cliente = new JComboBox<>();
    private final List<PessoaFisica> clientes = new ArrayList<>();
    private final JComboBox<Produto> produto = new JComboBox<>();
    private final JTextField quantidade = new JTextField("1"), valor = new JTextField();
    private final Venda venda = new Venda();
    private final JLabel total = new JLabel("Total: R$ 0,00");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Produto", "Quantidade", "Valor unitário", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int l, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    public TelaVenda() {
        super("Nova venda", true, true, true, true);
        setSize(820, 490); setLayout(new BorderLayout(8, 8));
        JPanel campos = new JPanel(new GridLayout(4, 2, 8, 8));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        campos.add(new JLabel("Cliente (PF)")); campos.add(cliente);
        campos.add(new JLabel("Produto")); campos.add(produto);
        campos.add(new JLabel("Quantidade")); campos.add(quantidade);
        campos.add(new JLabel("Valor unitário")); campos.add(valor); add(campos, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        JPanel botoes = new JPanel();
        botao(botoes, "Atualizar opções", this::carregar);
        botao(botoes, "Adicionar item", this::adicionar); botao(botoes, "Remover item", this::remover);
        botoes.add(total); botao(botoes, "Finalizar venda", this::finalizar); add(botoes, BorderLayout.SOUTH);
        produto.addActionListener(e -> {
            Produto p = (Produto)produto.getSelectedItem(); valor.setText(p == null ? "" : p.getPreco().toPlainString());
        }); carregar();
    }
    private void botao(JPanel painel, String texto, Runnable acao) {
        JButton b = new JButton(texto); b.addActionListener(e -> acao.run()); painel.add(b);
    }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Vendas", JOptionPane.WARNING_MESSAGE); }
    private void carregar() {
        try {
            // Leia antes de substituir as opções para não deixar uma atualização pela metade.
            List<Pessoa> pessoas = new PessoaDAO().listarDoBanco();
            List<Produto> produtos = new ProdutoDAO().listar();
            Integer clienteId = cliente.getSelectedIndex() < 0 ? null : clientes.get(cliente.getSelectedIndex()).getId();
            clientes.clear(); cliente.removeAllItems();
            for (Pessoa p : pessoas) if (p instanceof PessoaFisica) {
                clientes.add((PessoaFisica)p); cliente.addItem(p.getNome() + " (ID " + p.getId() + ")");
            }
            if (clienteId != null) {
                cliente.setSelectedIndex(-1);
                for (int i = 0; i < clientes.size(); i++) if (clienteId.equals(clientes.get(i).getId())) cliente.setSelectedIndex(i);
            }
            produto.removeAllItems(); for (Produto p : produtos) produto.addItem(p);
            if (clientes.isEmpty() || produtos.isEmpty())
                JOptionPane.showMessageDialog(this, "Cadastre uma Pessoa Física e um produto para vender.");
        } catch (SQLException ex) { erro(ex); }
    }
    private void adicionar() {
        try {
            Produto selecionado = (Produto)produto.getSelectedItem();
            if (selecionado == null) throw new IllegalArgumentException("Selecione um produto.");
            int qtd = Validacao.inteiro(quantidade.getText(), 1);
            java.math.BigDecimal preco = Validacao.dinheiro(valor.getText());
            Produto atual = new ProdutoDAO().buscar(selecionado.getId());
            if (atual == null) throw new IllegalArgumentException("Produto removido. Atualize as opções.");
            Validacao.estoque(atual, qtd, venda.getItens());
            venda.getItens().add(new ItemVenda(atual, qtd, preco)); atualizarTabela();
        } catch (SQLException | IllegalArgumentException ex) { erro(ex); }
    }
    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um item na tabela."); return; }
        venda.getItens().remove(tabela.convertRowIndexToModel(linha)); atualizarTabela();
    }
    private void atualizarTabela() {
        modelo.setRowCount(0);
        for (ItemVenda item : venda.getItens()) modelo.addRow(new Object[]{item.getProduto().getNome(),
            item.getQuantidade(), Validacao.moeda(item.getValorUnitario()), Validacao.moeda(item.getSubtotal())});
        total.setText("Total: " + Validacao.moeda(venda.getTotal()));
    }
    private void finalizar() {
        try {
            int i = cliente.getSelectedIndex(); venda.setCliente(i < 0 ? null : clientes.get(i));
            new VendaDAO().finalizar(venda);
            JOptionPane.showMessageDialog(this, "Venda " + venda.getId() + " gravada! Total: " + Validacao.moeda(venda.getTotal()));
            venda.getItens().clear(); venda.setId(0); venda.setDataVenda(null); atualizarTabela(); carregar();
        } catch (SQLException | IllegalArgumentException ex) { erro(ex); }
    }
}
