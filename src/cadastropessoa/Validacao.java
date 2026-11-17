package cadastropessoa;
import java.math.*;
import java.text.NumberFormat;
import java.util.*;

public class Validacao {
    public static BigDecimal dinheiro(String texto) {
        try {
            BigDecimal valor = new BigDecimal(texto.trim().replace(',', '.'));
            validarValor(valor);
            return valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException | ArithmeticException ex) {
            throw new IllegalArgumentException("Informe um valor válido com até duas casas decimais (ex.: 12,50).");
        }
    }
    public static void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0 || valor.compareTo(new BigDecimal("99999999.99")) > 0)
            throw new IllegalArgumentException("O valor deve ser maior que zero e no máximo 99999999,99.");
        try { valor.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException ex) { throw new IllegalArgumentException("Use até duas casas decimais."); }
    }
    public static int inteiro(String texto, int minimo) {
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor < minimo) throw new NumberFormatException();
            return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Informe um número inteiro maior ou igual a " + minimo + ".");
        }
    }
    public static void produto(Produto p) {
        if (p == null || p.getNome() == null || p.getNome().trim().isEmpty() || p.getNome().trim().length() > 100)
            throw new IllegalArgumentException("Informe um nome de produto com até 100 caracteres.");
        validarValor(p.getPreco());
        if (p.getEstoque() < 0) throw new IllegalArgumentException("O estoque não pode ser negativo.");
        if (p.getFornecedor() == null || p.getFornecedor().getId() == null || p.getFornecedor().getId() <= 0)
            throw new IllegalArgumentException("Selecione um fornecedor (Pessoa Jurídica).");
    }
    public static void estoque(Produto produto, int quantidade, List<ItemVenda> itens) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        long total = quantidade;
        for (ItemVenda item : itens)
            if (item.getProduto().getId() == produto.getId()) total += item.getQuantidade();
        if (total > produto.getEstoque())
            throw new IllegalArgumentException("Estoque insuficiente para " + produto.getNome()
                + ". Disponível: " + produto.getEstoque() + "; solicitado na venda: " + total + ".");
    }
    public static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }
}
