package armazem.jogos.security;

import armazem.jogos.repositories.UsuarioRepository; // Seu UsuarioRepository
import armazem.jogos.services.UsuarioService; // Seu UsuarioService
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    // Usaremos o UsuarioService para carregar UserDetails, pois ele já implementa UserDetailsService
    @Autowired
    private UsuarioService usuarioService;
    // Ou, se preferir buscar diretamente pelo username do repositório:
    // @Autowired
    // private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = recoverToken(request);

            if (jwt != null && tokenService.validateToken(jwt) != null) {
                String username = tokenService.validateToken(jwt); // Garante que validateToken retorna username

                // Carrega UserDetails usando o UsuarioService (que implementa UserDetailsService)
                UserDetails userDetails = usuarioService.loadUserByUsername(username);
                // Ou, se usando UsuarioRepository diretamente:
                // UserDetails userDetails = usuarioRepository.findByUsername(username)
                // .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));


                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Idealmente, logar o erro
            logger.error("Não foi possível definir a autenticação do usuário: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer "
        }
        return null;
    }
}