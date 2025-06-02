package armazem.jogos.services;

import armazem.jogos.dtos.JogoDTO;
import armazem.jogos.entities.Jogo;
import armazem.jogos.repositories.JogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JogoService {
    @Autowired
     JogoRepository jogoRepository;

    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    public Optional<Jogo> buscarPorId(Long id) {
        return jogoRepository.findById(id);
    }

    public Jogo salvar(JogoDTO jogoDTO) {
        Jogo jogo = new Jogo();
        jogo.setTitulo(jogoDTO.getTitulo());
        jogo.setPlataforma(jogoDTO.getPlataforma());
        jogo.setMidia(jogoDTO.getMidia());
        jogo.setGenero(jogoDTO.getGenero());
        jogo.setEstoque(jogoDTO.getEstoque());
        return jogoRepository.save(jogo);
    }

    public Optional<Jogo> atualizar(Long id, JogoDTO jogoDTO) {
        return jogoRepository.findById(id).map(jogo -> {
            jogo.setTitulo(jogoDTO.getTitulo());
            jogo.setPlataforma(jogoDTO.getPlataforma());
            jogo.setMidia(jogoDTO.getMidia());
            jogo.setGenero(jogoDTO.getGenero());
            jogo.setEstoque(jogoDTO.getEstoque());
            return jogoRepository.save(jogo);
        });
    }

    public void deletar(Long id) {
        jogoRepository.deleteById(id);
    }
}

