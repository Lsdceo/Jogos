package armazem.jogos.dtos;


import armazem.jogos.entities.TipoMovimentacao;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioMovimentacaoDTO {
    private Long movimentacaoId;
    private LocalDateTime dataHora;
    private TipoMovimentacao tipo;
    private String jogoTitulo;
    private Long jogoId;
    private String plataformaNome;
    private Long plataformaId;
    private String depositoOrigemNome;
    private Long depositoOrigemId;
    private String depositoDestinoNome;
    private Long depositoDestinoId;
    private int quantidade;
    private BigDecimal precoUnitarioMomento;
    private String observacao;

    public Long getMovimentacaoId() {
        return movimentacaoId;
    }

    public void setMovimentacaoId(Long movimentacaoId) {
        this.movimentacaoId = movimentacaoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public String getJogoTitulo() {
        return jogoTitulo;
    }

    public void setJogoTitulo(String jogoTitulo) {
        this.jogoTitulo = jogoTitulo;
    }

    public Long getJogoId() {
        return jogoId;
    }

    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public String getPlataformaNome() {
        return plataformaNome;
    }

    public void setPlataformaNome(String plataformaNome) {
        this.plataformaNome = plataformaNome;
    }

    public Long getPlataformaId() {
        return plataformaId;
    }

    public void setPlataformaId(Long plataformaId) {
        this.plataformaId = plataformaId;
    }

    public String getDepositoOrigemNome() {
        return depositoOrigemNome;
    }

    public void setDepositoOrigemNome(String depositoOrigemNome) {
        this.depositoOrigemNome = depositoOrigemNome;
    }

    public Long getDepositoOrigemId() {
        return depositoOrigemId;
    }

    public void setDepositoOrigemId(Long depositoOrigemId) {
        this.depositoOrigemId = depositoOrigemId;
    }

    public String getDepositoDestinoNome() {
        return depositoDestinoNome;
    }

    public void setDepositoDestinoNome(String depositoDestinoNome) {
        this.depositoDestinoNome = depositoDestinoNome;
    }

    public Long getDepositoDestinoId() {
        return depositoDestinoId;
    }

    public void setDepositoDestinoId(Long depositoDestinoId) {
        this.depositoDestinoId = depositoDestinoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
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
