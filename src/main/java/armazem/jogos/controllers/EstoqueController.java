package armazem.jogos.controllers;

import armazem.jogos.dtos.ItemEstoqueDTO; // Importe o DTO
import armazem.jogos.dtos.MovimentacaoRequestDTO;
import armazem.jogos.entities.ItemEstoque;
import armazem.jogos.entities.Movimentacao;
import armazem.jogos.services.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/estoque")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app",
        "front-jogos.vercel.app"
})
public class EstoqueController {

    @Autowired
    EstoqueService estoqueService;

    @PostMapping("/movimentar")
    public ResponseEntity<Movimentacao> registrarMovimentacao(@Valid @RequestBody MovimentacaoRequestDTO request) {
        Movimentacao movimentacao = estoqueService.registrarMovimentacao(request);
        return new ResponseEntity<>(movimentacao, HttpStatus.CREATED);
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> transferirEstoque(@RequestBody Map<String, Object> payload) {
        Long jogoId = Long.parseLong(payload.get("jogoId").toString());
        Long plataformaId = Long.parseLong(payload.get("plataformaId").toString());
        Long depositoOrigemId = Long.parseLong(payload.get("depositoOrigemId").toString());
        Long depositoDestinoId = Long.parseLong(payload.get("depositoDestinoId").toString());
        int quantidade = Integer.parseInt(payload.get("quantidade").toString());
        String observacao = (String) payload.get("observacao");

        estoqueService.registrarTransferencia(jogoId, plataformaId, depositoOrigemId, depositoDestinoId, quantidade, observacao);
        return ResponseEntity.ok().build();
    }

    // --- MÉTODO MODIFICADO PARA RETORNAR LISTA DE DTOs ---
    @GetMapping("/consultar")
    public ResponseEntity<List<ItemEstoqueDTO>> consultarEstoque( // Alterado o tipo de retorno
            @RequestParam(required = false) Long jogoId,
            @RequestParam(required = false) Long plataformaId,
            @RequestParam(required = false) Long depositoId) {
        List<ItemEstoqueDTO> itens = estoqueService.consultarEstoque(jogoId, plataformaId, depositoId); // O serviço já retorna DTOs
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/valor-total")
    public ResponseEntity<BigDecimal> calcularValorTotalEstoque(@RequestParam(required = false) Long depositoId) {
        BigDecimal valorTotal = estoqueService.calcularValorTotalEstoque(depositoId);
        return ResponseEntity.ok(valorTotal);
    }
}
