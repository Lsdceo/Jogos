package armazem.jogos.controllers;

import armazem.jogos.entities.Movimentacao;
import armazem.jogos.services.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoController {
    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping
    public Movimentacao registrarMovimentacao(@RequestBody Movimentacao movimentacao) {
        return movimentacaoService.registrarMovimentacao(movimentacao);
    }
}

