package armazem.jogos.repositories;

import armazem.jogos.entities.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Long> {
    Optional<Deposito> findByNome(String nome);
}