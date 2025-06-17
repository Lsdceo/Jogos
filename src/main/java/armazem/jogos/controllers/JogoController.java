package armazem.jogos.controllers;

import armazem.jogos.dtos.JogoDTO;
import armazem.jogos.entities.Jogo;
import armazem.jogos.services.JogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jogos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app",
        "https://front-jogos.vercel.app"
})
public class JogoController {
    @Autowired
    private JogoService jogoService;

    // Método para converter Jogo para JogoDTO
    private JogoDTO convertToDto(Jogo jogo) {
        JogoDTO dto = new JogoDTO();
        dto.setId(jogo.getId());
        dto.setTitulo(jogo.getTitulo());
        dto.setDescricao(jogo.getDescricao());
        dto.setPrecoSugerido(jogo.getPrecoSugerido());
        dto.setGenero(jogo.getGenero());
        dto.setDesenvolvedora(jogo.getDesenvolvedora());
        dto.setPublicadora(jogo.getPublicadora());
        dto.setUrlImagemCapa(jogo.getUrlImagemCapa());
        return dto;
    }

    // Método para converter JogoDTO para Jogo (para POST/PUT)
    private Jogo convertToEntity(JogoDTO dto) {
        Jogo jogo = new Jogo();
        jogo.setId(dto.getId()); // Id será null para criação, preenchido para atualização
        jogo.setTitulo(dto.getTitulo());
        jogo.setDescricao(dto.getDescricao());
        jogo.setPrecoSugerido(dto.getPrecoSugerido());
        jogo.setGenero(dto.getGenero());
        jogo.setDesenvolvedora(dto.getDesenvolvedora());
        jogo.setPublicadora(dto.getPublicadora());
        jogo.setUrlImagemCapa(dto.getUrlImagemCapa());
        return jogo;
    }


    @GetMapping
    public List<JogoDTO> listarJogos() {
        return jogoService.listarTodos().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JogoDTO> buscarJogoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(convertToDto(jogoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<JogoDTO> criarJogo(@Valid @RequestBody JogoDTO jogoDto) {
        Jogo jogo = convertToEntity(jogoDto);
        Jogo novoJogo = jogoService.salvar(jogo);
        return new ResponseEntity<>(convertToDto(novoJogo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JogoDTO> atualizarJogo(@PathVariable Long id, @Valid @RequestBody JogoDTO jogoDto) {
        Jogo jogo = convertToEntity(jogoDto);
        // Garanta que o ID da URL seja usado, não o do corpo, para evitar confusão
        // A lógica de `atualizar` no serviço já busca pelo ID
        return ResponseEntity.ok(convertToDto(jogoService.atualizar(id, jogo)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarJogo(@PathVariable Long id) {
        jogoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}