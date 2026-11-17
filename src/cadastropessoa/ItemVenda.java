package cadastropessoa;
import java.math.BigDecimal;

public class ItemVenda {
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal valorUnitario;
    public ItemVenda(Produto produto, int quantidade, BigDecimal valorUnitario) {
        this.produto = produto; this.quantidade = quantidade; this.valorUnitario = valorUnitario;
    }
    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public BigDecimal getSubtotal() { return valorUnitario.multiply(BigDecimal.valueOf(quantidade)); }
}
