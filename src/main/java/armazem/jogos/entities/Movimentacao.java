package armazem.jogos.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_movimentacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para fácil acesso nos DTOs de relatório e Jasper
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plataforma_id", nullable = false)
    private Plataforma plataforma;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deposito_origem_id")
    private Deposito depositoOrigem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deposito_destino_id")
    private Deposito depositoDestino;

    @Min(1)
    private int quantidade;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoUnitarioMomento;

    @Column(length = 500)
    private String observacao;
}