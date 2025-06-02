package armazem.jogos.controllers;

import armazem.jogos.dtos.MovimentacaoDTO;
import armazem.jogos.entities.Jogo;
import armazem.jogos.entities.Movimentacao;
import armazem.jogos.repositories.JogoRepository;
import armazem.jogos.services.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoController {
    @Autowired
    private MovimentacaoService movimentacaoService;

    @GetMapping
    public List<Movimentacao> listarMovimentacoes() {
        return movimentacaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimentacao> buscarPorId(@PathVariable Long id) {
        return movimentacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Movimentacao registrarMovimentacao(@RequestBody MovimentacaoDTO movimentacaoDTO) {
        return movimentacaoService.registrarMovimentacao(movimentacaoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movimentacao> atualizarMovimentacao(@PathVariable Long id, @RequestBody MovimentacaoDTO movimentacaoDTO) {
        return movimentacaoService.atualizar(id, movimentacaoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMovimentacao(@PathVariable Long id) {
        movimentacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}



