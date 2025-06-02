package armazem.jogos.repositories;

import armazem.jogos.entities.Deposito;
import armazem.jogos.entities.ItemEstoque;
import armazem.jogos.entities.Jogo;
import armazem.jogos.entities.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {
    Optional<ItemEstoque> findByJogoAndPlataformaAndDeposito(Jogo jogo, Plataforma plataforma,
                                                             Deposito deposito);
    List<ItemEstoque> findByJogo(Jogo jogo);
    List<ItemEstoque> findByDeposito(Deposito deposito);
    List<ItemEstoque> findByPlataforma(Plataforma plataforma);

    @Query("SELECT ie FROM ItemEstoque ie WHERE " +
            "(:jogoId IS NULL OR ie.jogo.id = :jogoId) AND " +
            "(:plataformaId IS NULL OR ie.plataforma.id = :plataformaId) AND " +
            "(:depositoId IS NULL OR ie.deposito.id = :depositoId)")
    List<ItemEstoque> findByOptionalFilters(@Param("jogoId") Long jogoId,
                                            @Param("plataformaId") Long plataformaId,
                                            @Param("depositoId") Long depositoId);
}
