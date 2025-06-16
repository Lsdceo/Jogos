package armazem.jogos.repositories;

import armazem.jogos.dtos.ItemEstoqueDTO; // Importe o DTO
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

    // --- NOVA QUERY PARA RETORNAR DTOS COM DETALHES ---
    @Query("SELECT new armazem.jogos.dtos.ItemEstoqueDTO(" +
           "ie.id, ie.jogo.id, ie.jogo.titulo, ie.plataforma.id, ie.plataforma.nome, ie.deposito.id, ie.deposito.nome, ie.quantidade, ie.precoUnitarioAtual) " + // Seleciona campos para o DTO
           "FROM ItemEstoque ie " +
           "JOIN ie.jogo j " + // JOINs explícitos são necessários na query para acessar campos relacionados
           "JOIN ie.plataforma p " +
           "JOIN ie.deposito d " +
           "WHERE (:jogoId IS NULL OR ie.jogo.id = :jogoId) AND " +
           "(:plataformaId IS NULL OR ie.plataforma.id = :plataformaId) AND " +
           "(:depositoId IS NULL OR ie.deposito.id = :depositoId)")
    List<ItemEstoqueDTO> findWithDetailsByOptionalFilters(@Param("jogoId") Long jogoId, // Novo nome para o método
                                                         @Param("plataformaId") Long plataformaId,
                                                         @Param("depositoId") Long depositoId);

    // Você pode manter findByOptionalFilters retornando entidades se for usado em outros lugares,
    // ou substituí-lo por findWithDetailsByOptionalFilters se esta for a única forma de consultar estoque filtrado.

}
