package armazem.jogos.dtos;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemEstoqueDTO {
    private Long id;
    private Long jogoId;
    private String jogoTitulo;
    private Long plataformaId;
    private String plataformaNome;
    private Long depositoId;
    private String depositoNome;
    private int quantidade;
    private BigDecimal precoUnitarioAtual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitarioAtual() {
        return precoUnitarioAtual;
    }

    public void setPrecoUnitarioAtual(BigDecimal precoUnitarioAtual) {
        this.precoUnitarioAtual = precoUnitarioAtual;
    }
}
