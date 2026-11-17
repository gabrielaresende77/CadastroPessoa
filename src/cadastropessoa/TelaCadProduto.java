package cadastropessoa;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Tela Swing escrita em Java; não depende de arquivo .form. */
public class TelaCadProduto extends JInternalFrame {
    private final JTextField nome = new JTextField(), preco = new JTextField(), estoque = new JTextField("0");
    private final JComboBox<String> fornecedor = new JComboBox<>();
    private final List<PessoaJuridica> fornecedores = new ArrayList<>();
    private final ProdutoDAO dao = new ProdutoDAO();
    private final JLabel identificador = new JLabel("Novo produto");
    private int idAtual;
    public TelaCadProduto() {
        super("Cadastro de produtos", true, true, true, true);
        setSize(680, 320); setLayout(new BorderLayout(8, 8));
        JPanel campos = new JPanel(new GridLayout(5, 2, 8, 8));
        campos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        campos.add(new JLabel("Registro")); campos.add(identificador);
        campos.add(new JLabel("Nome")); campos.add(nome);
        campos.add(new JLabel("Preço (ex.: 12,50)")); campos.add(preco);
        campos.add(new JLabel("Estoque")); campos.add(estoque);
        campos.add(new JLabel("Fornecedor (PJ)")); campos.add(fornecedor); add(campos, BorderLayout.CENTER);
        JPanel botoes = new JPanel();
        botao(botoes, "Novo", () -> { limpar(); carregarFornecedores(); });
        botao(botoes, "Gravar", this::gravar); botao(botoes, "◀", () -> navegar(-1));
        botao(botoes, "▶", () -> navegar(1)); botao(botoes, "Excluir", this::excluir);
        botao(botoes, "Listar", this::listar); add(botoes, BorderLayout.SOUTH);
        carregarFornecedores();
    }
    private void botao(JPanel painel, String texto, Runnable acao) {
        JButton b = new JButton(texto); b.addActionListener(e -> acao.run()); painel.add(b);
    }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Produtos", JOptionPane.WARNING_MESSAGE); }
    private void carregarFornecedores() {
        try {
            List<Pessoa> pessoas = new PessoaDAO().listarDoBanco();
            fornecedores.clear(); fornecedor.removeAllItems();
            for (Pessoa p : pessoas) if (p instanceof PessoaJuridica) {
                fornecedores.add((PessoaJuridica)p); fornecedor.addItem(p.getNome() + " (ID " + p.getId() + ")");
            }
            if (fornecedores.isEmpty()) JOptionPane.showMessageDialog(this, "Cadastre uma Pessoa Jurídica primeiro.");
        } catch (SQLException ex) { erro(ex); }
    }
    private void limpar() {
        idAtual = 0; identificador.setText("Novo produto"); nome.setText(""); preco.setText(""); estoque.setText("0");
    }
    private void gravar() {
        try {
            Produto p = new Produto(); p.setId(idAtual); p.setNome(nome.getText().trim());
            p.setPreco(Validacao.dinheiro(preco.getText())); p.setEstoque(Validacao.inteiro(estoque.getText(), 0));
            int i = fornecedor.getSelectedIndex(); p.setFornecedor(i < 0 ? null : fornecedores.get(i));
            if (idAtual == 0) dao.inserir(p); else dao.atualizar(p);
            JOptionPane.showMessageDialog(this, "Produto gravado com sucesso! ID: " + p.getId()); limpar();
        } catch (SQLException | IllegalArgumentException ex) { erro(ex); }
    }
    private void navegar(int direcao) {
        try {
            List<Produto> produtos = dao.listar();
            if (produtos.isEmpty()) { limpar(); JOptionPane.showMessageDialog(this, "Nenhum produto cadastrado."); return; }
            int indice = -1;
            for (int i = 0; i < produtos.size(); i++) if (produtos.get(i).getId() == idAtual) indice = i;
            int proximo = indice < 0 ? (direcao > 0 ? 0 : produtos.size() - 1) : indice + direcao;
            if (proximo < 0 || proximo >= produtos.size()) { JOptionPane.showMessageDialog(this, "Fim do cadastro."); return; }
            carregarFornecedores(); Produto p = produtos.get(proximo); idAtual = p.getId();
            identificador.setText("ID " + idAtual); nome.setText(p.getNome()); preco.setText(p.getPreco().toPlainString());
            estoque.setText(String.valueOf(p.getEstoque())); fornecedor.setSelectedIndex(-1);
            for (int i = 0; i < fornecedores.size(); i++)
                if (fornecedores.get(i).getId().equals(p.getFornecedor().getId())) fornecedor.setSelectedIndex(i);
        } catch (SQLException ex) { erro(ex); }
    }
    private void excluir() {
        if (idAtual == 0) { JOptionPane.showMessageDialog(this, "Navegue até um produto para excluir."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir o produto " + nome.getText() + "?", "Confirmação",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dao.excluir(idAtual); limpar(); JOptionPane.showMessageDialog(this, "Produto excluído."); }
        catch (SQLException ex) { erro(ex); }
    }
    private void listar() {
        try {
            DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço", "Estoque", "Fornecedor"}, 0) {
                @Override public boolean isCellEditable(int l, int c) { return false; }
            };
            for (Produto p : dao.listar()) modelo.addRow(new Object[]{p.getId(), p.getNome(),
                Validacao.moeda(p.getPreco()), p.getEstoque(), p.getFornecedor().getNome()});
            JScrollPane painel = new JScrollPane(new JTable(modelo)); painel.setPreferredSize(new Dimension(680, 260));
            JOptionPane.showMessageDialog(this, painel, "Produtos do banco", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) { erro(ex); }
    }
}
