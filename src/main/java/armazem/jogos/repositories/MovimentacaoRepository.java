package armazem.jogos.repositories; // SEU PACOTE ATUALIZADO

import armazem.jogos.entities.Movimentacao; // SEU PACOTE ATUALIZADO
import armazem.jogos.entities.TipoMovimentacao; // SEU PACOTE ATUALIZADO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT m FROM Movimentacao m " +
            "LEFT JOIN FETCH m.jogo " +
            "LEFT JOIN FETCH m.plataforma " +
            "LEFT JOIN FETCH m.depositoOrigem " +
            "LEFT JOIN FETCH m.depositoDestino " +
            "WHERE (:dataInicio IS NULL OR m.dataHora >= :dataInicio) " +
            "AND (:dataFim IS NULL OR m.dataHora <= :dataFim) " +
            "AND (:tipo IS NULL OR m.tipo = :tipo) " +
            "AND (:jogoId IS NULL OR m.jogo.id = :jogoId) " +
            "AND (:plataformaId IS NULL OR m.plataforma.id = :plataformaId) " +
            "AND (" +
            "      (:depositoId IS NULL) OR " +
            // AQUI ESTÁ A MUDANÇA PRINCIPAL:
            "      (m.depositoOrigem.id = :depositoId AND m.tipo IN (armazem.jogos.entities.TipoMovimentacao.SAIDA, armazem.jogos.entities.TipoMovimentacao.TRANSFERENCIA_SAIDA, armazem.jogos.entities.TipoMovimentacao.AJUSTE_NEGATIVO)) OR " +
            "      (m.depositoDestino.id = :depositoId AND m.tipo IN (armazem.jogos.entities.TipoMovimentacao.ENTRADA, armazem.jogos.entities.TipoMovimentacao.TRANSFERENCIA_ENTRADA, armazem.jogos.entities.TipoMovimentacao.AJUSTE_POSITIVO))" +
            ") ORDER BY m.dataHora DESC")
    List<Movimentacao> findByFiltrosOpcionais(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("tipo") TipoMovimentacao tipo,
            @Param("jogoId") Long jogoId,
            @Param("plataformaId") Long plataformaId,
            @Param("depositoId") Long depositoId
    );
}