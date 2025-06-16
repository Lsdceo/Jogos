package armazem.jogos.controllers;

import armazem.jogos.entities.Plataforma;
import armazem.jogos.services.PlataformaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/plataformas")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app",
        "front-jogos.vercel.app"
})
public class PlataformaController {
    @Autowired
    PlataformaService plataformaService;

    @GetMapping
    public List<Plataforma> listarPlataformas() {
        return plataformaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plataforma> buscarPlataformaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(plataformaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Plataforma> criarPlataforma(@Valid @RequestBody Plataforma plataforma) {
        Plataforma novaPlataforma = plataformaService.salvar(plataforma);
        return new ResponseEntity<>(novaPlataforma, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plataforma> atualizarPlataforma(@PathVariable Long id, @Valid @RequestBody Plataforma plataformaDetalhes) {
        return ResponseEntity.ok(plataformaService.atualizar(id, plataformaDetalhes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlataforma(@PathVariable Long id) {
        plataformaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
