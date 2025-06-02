package armazem.jogos.controllers;

import armazem.jogos.dtos.AuthResponseDTO;
import armazem.jogos.dtos.RegistroUsuarioRequestDTO;
import armazem.jogos.dtos.UsuarioDTO;
import armazem.jogos.entities.Usuario;
import armazem.jogos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/auth") // ou /api/usuarios
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint público para registro de novos usuários comuns
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequestDTO registroDTO) {
        try {
            Usuario novoUsuario = usuarioService.registrarNovoUsuario(registroDTO, "ROLE_USUARIO");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponseDTO("Usuário registrado com sucesso!", novoUsuario.getId(), novoUsuario.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponseDTO(e.getMessage(), null, null));
        }
    }

    // Endpoints de Gerenciamento de Usuários (apenas para ADMIN)
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodosUsuarios());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/usuarios/{id}/roles")

    public ResponseEntity<UsuarioDTO> atualizarRolesUsuario(@PathVariable Long id, @RequestBody Set<String> roles) {
        try {
            return ResponseEntity.ok(usuarioService.atualizarRolesUsuario(id, roles));
        } catch (RuntimeException e) { // Captura ResourceNotFoundException também
            return ResponseEntity.badRequest().body(null); // Melhorar tratamento de erro
        }
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UsuarioDTO dto = usuarioService.convertToDto(usuarioLogado); // Supondo que existe esse método em UsuarioService ou no controller.
        return ResponseEntity.ok(dto);
    }
}