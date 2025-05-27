package armazem.jogos.controllers;

import armazem.jogos.entities.Movimentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {
    @Autowired
    RelatorioService relatorioService;

    @GetMapping
    public List<Movimentacao> gerarRelatorio(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return relatorioService.gerarRelatorio(inicio, fim);
    }
}

