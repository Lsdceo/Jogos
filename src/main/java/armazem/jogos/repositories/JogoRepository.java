package armazem.jogos.repositories;

import armazem.jogos.entities.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    Optional<Jogo> findByTitulo(String titulo); // Adicionando por título para consistência
}