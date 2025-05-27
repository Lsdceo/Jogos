package armazem.jogos.repositories;

import armazem.jogos.entities.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}

