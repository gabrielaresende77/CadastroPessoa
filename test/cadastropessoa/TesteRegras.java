package cadastropessoa;
import java.math.BigDecimal;

/** Testes sem bibliotecas externas: executados com java -ea. */
public class TesteRegras {
    private static int verificacoes;
    private static void conferir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
    private static void rejeitar(Runnable acao) {
        boolean rejeitou = false;
        try { acao.run(); } catch (IllegalArgumentException ex) { rejeitou = true; }
        conferir(rejeitou, "Entrada inválida foi aceita");
    }
    public static void main(String[] args) {
        conferir(Validacao.dinheiro("12,50").equals(new BigDecimal("12.50")), "Vírgula decimal");
        conferir(Validacao.dinheiro("0.10").equals(new BigDecimal("0.10")), "Centavos");
        for (String valor : new String[]{"", "abc", "0", "-1", "NaN", "Infinity", "1.001", "100000000"})
            rejeitar(() -> Validacao.dinheiro(valor));
        for (String valor : new String[]{"-1", "1.5", "2147483648", ""}) rejeitar(() -> Validacao.inteiro(valor, 0));
        conferir(Validacao.inteiro("0", 0) == 0, "Estoque zero");
        rejeitar(() -> Validacao.inteiro("0", 1));
        PessoaJuridica fornecedor = new PessoaJuridica("Fornecedor", "Rua", "email", "123", "Loja");
        fornecedor.setId(1);
        Produto p = new Produto(); p.setId(10); p.setNome("Caneta"); p.setPreco(new BigDecimal("0.10"));
        p.setEstoque(5); p.setFornecedor(fornecedor); Validacao.produto(p);
        Venda venda = new Venda(); venda.getItens().add(new ItemVenda(p, 3, p.getPreco()));
        conferir(venda.getTotal().compareTo(new BigDecimal("0.30")) == 0, "Total exato");
        Validacao.estoque(p, 2, venda.getItens());
        rejeitar(() -> Validacao.estoque(p, 3, venda.getItens()));
        venda.getItens().add(new ItemVenda(p, 2, new BigDecimal("0.20")));
        conferir(venda.getTotal().compareTo(new BigDecimal("0.70")) == 0, "Soma de itens");
        rejeitar(() -> Validacao.estoque(p, 1, venda.getItens()));
        venda.getItens().remove(0);
        conferir(venda.getTotal().compareTo(new BigDecimal("0.40")) == 0, "Remoção altera total");
        p.setNome("  "); rejeitar(() -> Validacao.produto(p)); p.setNome("Caneta");
        p.setEstoque(-1); rejeitar(() -> Validacao.produto(p)); p.setEstoque(0);
        p.setFornecedor(null); rejeitar(() -> Validacao.produto(p));
        System.out.println("OK: " + verificacoes + " verificações de regras e cálculos.");
    }
}
