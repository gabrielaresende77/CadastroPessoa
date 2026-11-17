package cadastropessoa;
import java.math.BigDecimal;

public class Produto {
    private int id;
    private String nome;
    private BigDecimal preco;
    private int estoque;
    private PessoaJuridica fornecedor;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }
    public PessoaJuridica getFornecedor() { return fornecedor; }
    public void setFornecedor(PessoaJuridica fornecedor) { this.fornecedor = fornecedor; }
    @Override public String toString() { return nome + " (ID " + id + ")"; }
}
