package armazem.jogos.controllers;

import armazem.jogos.dtos.*; // Seus DTOs
import armazem.jogos.entities.Role;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app",
        "front-jogos.vercel.app"
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
            // Assuming registrarNovoUsuario returns the saved Usuario entity or a DTO with id, username, nomeCompleto
            Usuario novoUsuario = usuarioService.registrarNovoUsuario(registroDTO, "ROLE_USUARIO");
            // You might want to return a JWT here as well, or just a success message
            // For now, let's stick to returning success message as in your code
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponseDTO("Usuário registrado com sucesso!", novoUsuario.getId(), novoUsuario.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponseDTO(e.getMessage(), null, null));
        }
    }

    // LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        // SecurityContextHolder.getContext().setAuthentication(authentication); // The AuthenticationManager already does this implicitly on success

        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal(); // Get your Usuario object
        String jwtToken = tokenService.generateToken(usuarioAutenticado);

        // The log indicated you were already returning roles, but let's ensure id and nomeCompleto are included too
        // Make sure your Usuario entity has getId() and getNomeCompleto() methods
        return ResponseEntity.ok(new JwtResponseDTO(
                jwtToken,
                "Bearer",
                tokenService.getExpirationInMillis(), // Send expiration time
                usuarioAutenticado.getUsername(), // This is likely the email
                usuarioAutenticado.getId(), // Include the user's ID
                usuarioAutenticado.getNomeCompleto(), // Include the user's full name
                usuarioAutenticado.getRoles().stream().map(Role::getNome).collect(Collectors.toList()) // Include the user's roles
        ));
    }


    // User Management Endpoints (ADMIN only)
    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')") // Example of using @PreAuthorize
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
    public ResponseEntity<UsuarioDTO> actualizarRolesUsuario(@PathVariable Long id, @RequestBody Set<String> roles) {
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
    // @AuthenticationPrincipal UserDetails userDetails // You can use UserDetails here
    public ResponseEntity<UsuarioDTO> getUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) { // Or your Usuario object directly if the cast is safe
        if (usuarioLogado == null) {
            // This case should not happen if the endpoint is .authenticated() and the JWT filter works
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UsuarioDTO dto = usuarioService.convertToDto(usuarioLogado);
        return ResponseEntity.ok(dto);
    }
}
