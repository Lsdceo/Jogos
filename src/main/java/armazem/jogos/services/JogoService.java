package armazem.jogos.services;

import armazem.jogos.entities.Jogo;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.JogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class JogoService {
    @Autowired
    private JogoRepository jogoRepository;

    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    public Jogo buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado com id: " + id));
    }

    @Transactional
    public Jogo salvar(Jogo jogo) {
        jogoRepository.findByTitulo(jogo.getTitulo()).ifPresent(j -> {
            if (!j.getId().equals(jogo.getId())) { // Permite atualizar o mesmo objeto
                throw new IllegalArgumentException("Jogo com título '" + jogo.getTitulo() + "' já existe.");
            }
        });
        return jogoRepository.save(jogo);
    }

    @Transactional
    public Jogo atualizar(Long id, Jogo jogoDetalhes) {
        Jogo jogo = buscarPorId(id);
        jogo.setTitulo(jogoDetalhes.getTitulo());
        jogo.setDescricao(jogoDetalhes.getDescricao());
        jogo.setPrecoSugerido(jogoDetalhes.getPrecoSugerido());
        jogo.setGenero(jogoDetalhes.getGenero());
        jogo.setDesenvolvedora(jogoDetalhes.getDesenvolvedora());
        jogo.setPublicadora(jogoDetalhes.getPublicadora());
        return salvar(jogo);
    }


    @Transactional
    public void deletar(Long id) {
        if (!jogoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Jogo não encontrado para deleção com id: " + id);
        }
        // Adicionar verificações de integridade se necessário
        jogoRepository.deleteById(id);
    }
}