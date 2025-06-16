package armazem.jogos.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioEstoqueItemDTO {
    private Long jogoId;
    private String jogoTitulo;
    private Long plataformaId;
    private String plataformaNome;
    private Long depositoId;
    private String depositoNome;
    private int quantidadeEmEstoque;
    private BigDecimal precoUnitarioAtual;
    private BigDecimal valorTotalItem;

    public Long getJogoId() {
        return jogoId;
    }

    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public String getJogoTitulo() {
        return jogoTitulo;
    }

    public void setJogoTitulo(String jogoTitulo) {
        this.jogoTitulo = jogoTitulo;
    }

    public Long getPlataformaId() {
        return plataformaId;
    }

    public void setPlataformaId(Long plataformaId) {
        this.plataformaId = plataformaId;
    }

    public String getPlataformaNome() {
        return plataformaNome;
    }

    public void setPlataformaNome(String plataformaNome) {
        this.plataformaNome = plataformaNome;
    }

    public Long getDepositoId() {
        return depositoId;
    }

    public void setDepositoId(Long depositoId) {
        this.depositoId = depositoId;
    }

    public String getDepositoNome() {
        return depositoNome;
    }

    public void setDepositoNome(String depositoNome) {
        this.depositoNome = depositoNome;
    }

    public int getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public void setQuantidadeEmEstoque(int quantidadeEmEstoque) {
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    public BigDecimal getPrecoUnitarioAtual() {
        return precoUnitarioAtual;
    }

    public void setPrecoUnitarioAtual(BigDecimal precoUnitarioAtual) {
        this.precoUnitarioAtual = precoUnitarioAtual;
    }

    public BigDecimal getValorTotalItem() {
        return valorTotalItem;
    }

    public void setValorTotalItem(BigDecimal valorTotalItem) {
        this.valorTotalItem = valorTotalItem;
    }
}
