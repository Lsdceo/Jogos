package armazem.jogos.controllers;

import armazem.jogos.dtos.JogoDTO;
import armazem.jogos.entities.Jogo;
import armazem.jogos.services.JogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jogos")
public class JogoController {
    @Autowired
    private JogoService jogoService;

    @GetMapping
    public List<Jogo> listarJogos() {
        return jogoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jogo> buscarPorId(@PathVariable Long id) {
        return jogoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Jogo criarJogo(@RequestBody JogoDTO jogoDTO) {
        return jogoService.salvar(jogoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jogo> atualizarJogo(@PathVariable Long id, @RequestBody JogoDTO jogoDTO) {
        return jogoService.atualizar(id, jogoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarJogo(@PathVariable Long id) {
        jogoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}


