package cadastropessoa;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int id;
    private LocalDateTime dataVenda;
    private PessoaFisica cliente;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }
    public PessoaFisica getCliente() { return cliente; }
    public void setCliente(PessoaFisica cliente) { this.cliente = cliente; }
    private final List<ItemVenda> itens = new ArrayList<>();
    public List<ItemVenda> getItens() { return itens; }
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) total = total.add(item.getSubtotal());
        return total;
    }
}
