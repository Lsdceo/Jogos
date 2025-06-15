package armazem.jogos.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_itens_estoque",
        uniqueConstraints = @UniqueConstraint(columnNames = {"jogo_id", "plataforma_id", "deposito_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEstoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para fácil acesso nos DTOs de relatório. LAZY pode ser mais performático.
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plataforma_id", nullable = false)
    private Plataforma plataforma;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deposito_id", nullable = false)
    private Deposito deposito;

    @Min(0)
    private int quantidade;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoUnitarioAtual;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public Plataforma getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
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
