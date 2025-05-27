package armazem.jogos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @NotBlank(message = "O tipo de movimentação é obrigatório")
    private String tipo; // entrada, saída, transferência

    @Min(value = 1, message = "A quantidade deve ser maior que zero")
    private int quantidade;

    @ManyToOne
    private Jogo jogo;

    public Movimentacao() {
    }

    public Movimentacao(Long id, LocalDateTime dataHora,
                        String tipo, int quantidade, Jogo jogo) {
        this.id = id;
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.jogo = jogo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
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

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }
}
