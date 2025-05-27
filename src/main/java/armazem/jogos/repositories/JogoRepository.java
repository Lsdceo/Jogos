package armazem.jogos.repositories;

import armazem.jogos.entities.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    List<Jogo> findByGenero(String genero);
    List<Jogo> findByPlataforma(String plataforma);
}

