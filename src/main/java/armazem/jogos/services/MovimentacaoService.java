package armazem.jogos.services;

import armazem.jogos.dtos.MovimentacaoDTO;
import armazem.jogos.entities.Jogo;
import armazem.jogos.entities.Movimentacao;
import armazem.jogos.repositories.JogoRepository;
import armazem.jogos.repositories.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {
    @Autowired
    MovimentacaoRepository movimentacaoRepository;
    @Autowired
    JogoRepository jogoRepository;

    public List<Movimentacao> listarTodas() {
        return movimentacaoRepository.findAll();
    }

    public Optional<Movimentacao> buscarPorId(Long id) {
        return movimentacaoRepository.findById(id);
    }

    public Movimentacao registrarMovimentacao(MovimentacaoDTO movimentacaoDTO) {
        Jogo jogo = jogoRepository.findById(movimentacaoDTO.getJogoId())
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setJogo(jogo);
        movimentacao.setTipo(movimentacaoDTO.getTipo());
        movimentacao.setQuantidade(movimentacaoDTO.getQuantidade());
        movimentacao.setDataHora(LocalDateTime.now());

        return movimentacaoRepository.save(movimentacao);
    }

    public Optional<Movimentacao> atualizar(Long id, MovimentacaoDTO movimentacaoDTO) {
        return movimentacaoRepository.findById(id).map(movimentacao -> {
            Jogo jogo = jogoRepository.findById(movimentacaoDTO.getJogoId())
                    .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

            movimentacao.setJogo(jogo);
            movimentacao.setTipo(movimentacaoDTO.getTipo());
            movimentacao.setQuantidade(movimentacaoDTO.getQuantidade());
            return movimentacaoRepository.save(movimentacao);
        });
    }

    public void deletar(Long id) {
        movimentacaoRepository.deleteById(id);
    }
}


