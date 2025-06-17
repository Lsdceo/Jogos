package armazem.jogos.controllers;

import armazem.jogos.entities.Deposito;
import armazem.jogos.services.DepositoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depositos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app",
        "https://front-jogos.vercel.app"
})
public class DepositoController {
    @Autowired
    DepositoService depositoService;

    @GetMapping
    public List<Deposito> listarDepositos() {
        return depositoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> buscarDepositoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(depositoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Deposito> criarDeposito(@Valid @RequestBody Deposito deposito) {
        Deposito novoDeposito = depositoService.salvar(deposito);
        return new ResponseEntity<>(novoDeposito, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposito> atualizarDeposito(@PathVariable Long id, @Valid @RequestBody Deposito depositoDetalhes) {
        return ResponseEntity.ok(depositoService.atualizar(id, depositoDetalhes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDeposito(@PathVariable Long id) {
        depositoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
