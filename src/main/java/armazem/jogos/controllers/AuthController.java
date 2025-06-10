package armazem.jogos.controllers;

import armazem.jogos.dtos.*; // Seus DTOs
import armazem.jogos.entities.Usuario;
import armazem.jogos.security.TokenService; // Seu TokenService
import armazem.jogos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app"
})
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager; // Para o endpoint de login

    @Autowired
    private TokenService tokenService; // Para gerar o token

    // Endpoint público para registro de novos usuários comuns
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequestDTO registroDTO) {
        try {
            Usuario novoUsuario = usuarioService.registrarNovoUsuario(registroDTO, "ROLE_USUARIO");
            // Retornar um DTO mais simples ou uma mensagem de sucesso, sem o objeto Usuario inteiro
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponseDTO("Usuário registrado com sucesso!", novoUsuario.getId(), novoUsuario.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponseDTO(e.getMessage(), null, null));
        }
    }

    // NOVO ENDPOINT DE LOGIN
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        // SecurityContextHolder.getContext().setAuthentication(authentication); // O AuthenticationManager já faz isso implicitamente no sucesso

        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal(); // Obtém seu objeto Usuario
        String jwtToken = tokenService.generateToken(usuarioAutenticado);

        return ResponseEntity.ok(new JwtResponseDTO(
                jwtToken,
                "Bearer",
                tokenService.getExpirationInMillis(), // Envia o tempo de expiração
                usuarioAutenticado.getUsername()
                // , usuarioAutenticado.getRoles().stream().map(Role::getNome).collect(Collectors.toList()) // Se quiser enviar roles
        ));
    }


    // Endpoints de Gerenciamento de Usuários (apenas para ADMIN)
    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')") // Exemplo de uso de @PreAuthorize
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodosUsuarios());
    }

    @GetMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/usuarios/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> atualizarRolesUsuario(@PathVariable Long id, @RequestBody Set<String> roles) {
        try {
            return ResponseEntity.ok(usuarioService.atualizarRolesUsuario(id, roles));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    // @AuthenticationPrincipal UserDetails userDetails // Pode usar UserDetails aqui
    public ResponseEntity<UsuarioDTO> getUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) { // Ou seu objeto Usuario diretamente se o cast for seguro
        if (usuarioLogado == null) {
            // Este caso não deve ocorrer se o endpoint é .authenticated() e o filtro JWT funciona
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UsuarioDTO dto = usuarioService.convertToDto(usuarioLogado);
        return ResponseEntity.ok(dto);
    }
}