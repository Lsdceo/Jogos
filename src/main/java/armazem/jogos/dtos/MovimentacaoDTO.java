package armazem.jogos.dtos;

import armazem.jogos.entities.Movimentacao;

public class MovimentacaoDTO {

    private Long jogoId;
    private String tipo; // entrada, saída, transferência
    private int quantidade;

    public MovimentacaoDTO() {

    }

    public MovimentacaoDTO(Long jogoId, String tipo, int quantidade) {
        this.jogoId = jogoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
    }

    public MovimentacaoDTO(Movimentacao movimentacao) {
        this.jogoId = movimentacao.getJogo().getId();
        this.tipo = movimentacao.getTipo();
        this.quantidade = movimentacao.getQuantidade();
    }

    public Long getJogoId() {
        return jogoId;
    }

    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
