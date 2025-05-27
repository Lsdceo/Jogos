package armazem.jogos.services;

import armazem.jogos.entities.Jogo;
import armazem.jogos.repositories.JogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JogoService {
    @Autowired
    JogoRepository jogoRepository;

    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    public Jogo salvar(Jogo jogo) {
        return jogoRepository.save(jogo);
    }
}
