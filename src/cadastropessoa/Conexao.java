package cadastropessoa;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

public class Conexao {
    public static Connection getConexao() throws SQLException {
        Properties config = new Properties();
        Path arquivo = Path.of("db.properties");
        if (Files.exists(arquivo)) {
            try (Reader reader = Files.newBufferedReader(arquivo)) { config.load(reader); }
            catch (IOException ex) { throw new SQLException("Não foi possível ler db.properties.", ex); }
        }
        return DriverManager.getConnection(
            config.getProperty("url", "jdbc:mysql://localhost:3306/cadastro_pessoa"),
            config.getProperty("usuario", "root"), config.getProperty("senha", ""));
    }
}
