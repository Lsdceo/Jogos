package armazem.jogos.dtos;


import armazem.jogos.entities.TipoMovimentacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovimentacaoRequestDTO {
    @NotNull
    private TipoMovimentacao tipo;
    @NotNull
    private Long jogoId;
    @NotNull
    private Long plataformaId;
    private Long depositoOrigemId;
    private Long depositoDestinoId;
    @NotNull
    @Min(1)
    private Integer quantidade;
    private BigDecimal precoUnitarioMomento;
    private String observacao;

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public Long getJogoId() {
        return jogoId;
    }

    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public Long getPlataformaId() {
        return plataformaId;
    }

    public void setPlataformaId(Long plataformaId) {
        this.plataformaId = plataformaId;
    }

    public Long getDepositoOrigemId() {
        return depositoOrigemId;
    }

    public void setDepositoOrigemId(Long depositoOrigemId) {
        this.depositoOrigemId = depositoOrigemId;
    }

    public Long getDepositoDestinoId() {
        return depositoDestinoId;
    }

    public void setDepositoDestinoId(Long depositoDestinoId) {
        this.depositoDestinoId = depositoDestinoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitarioMomento() {
        return precoUnitarioMomento;
    }

    public void setPrecoUnitarioMomento(BigDecimal precoUnitarioMomento) {
        this.precoUnitarioMomento = precoUnitarioMomento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
