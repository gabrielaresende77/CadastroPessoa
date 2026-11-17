package cadastropessoa;
import javax.swing.*;

public class CadastroPessoa {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try (java.sql.Connection con = Conexao.getConexao()) {
                TelaPrincipal tela = new TelaPrincipal();
                tela.setSize(1000, 720); tela.setLocationRelativeTo(null); tela.setVisible(true);
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(null,
                    "Não foi possível abrir o banco. Execute banco_dados.sql no MySQL "
                    + "e confira db.properties.\n" + ex.getMessage(), "Conexão", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
