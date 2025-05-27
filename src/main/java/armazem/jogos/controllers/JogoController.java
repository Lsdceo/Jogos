package armazem.jogos.controllers;

import armazem.jogos.entities.Jogo;
import armazem.jogos.services.JogoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public Jogo criarJogo(@RequestBody Jogo jogo) {
        return jogoService.salvar(jogo);
    }
}

