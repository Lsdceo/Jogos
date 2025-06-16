package armazem.jogos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioEstoqueConsolidadoDTO {
    private List<RelatorioEstoqueItemDTO> itens;
    private BigDecimal valorTotalGeralDoEstoque;
    private String depositoNomeFiltrado;

    public List<RelatorioEstoqueItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<RelatorioEstoqueItemDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotalGeralDoEstoque() {
        return valorTotalGeralDoEstoque;
    }

    public void setValorTotalGeralDoEstoque(BigDecimal valorTotalGeralDoEstoque) {
        this.valorTotalGeralDoEstoque = valorTotalGeralDoEstoque;
    }

    public String getDepositoNomeFiltrado() {
        return depositoNomeFiltrado;
    }

    public void setDepositoNomeFiltrado(String depositoNomeFiltrado) {
        this.depositoNomeFiltrado = depositoNomeFiltrado;
    }
}
