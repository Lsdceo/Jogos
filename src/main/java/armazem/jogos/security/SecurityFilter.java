package armazem.jogos.security;

import armazem.jogos.repositories.UsuarioRepository; // Seu UsuarioRepository
import armazem.jogos.services.UsuarioService; // Seu UsuarioService
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class); // Adicionar Logger

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
            // --- LOGS DE DEPURACAO ---
            logger.info("Processando requisição para URI: {}", request.getRequestURI());
            logger.info("Token JWT recuperado do cabeçalho Authorization: {}", (jwt != null ? "Presente" : "Ausente"));
            // -----------------------

            if (jwt != null) {
                String username = tokenService.validateToken(jwt); // Garante que validateToken retorna username
                // --- LOGS DE DEPURACAO ---
                logger.info("Resultado da validação do token pelo TokenService: {}", (username != null ? "Sucesso - Username: " + username : "Falha"));
                // -----------------------


                if (username != null) {
                    // Carrega UserDetails usando o UsuarioService (que implementa UserDetailsService)
                    UserDetails userDetails = usuarioService.loadUserByUsername(username);
                    // --- LOGS DE DEPURACAO ---
                    logger.info("UserDetails carregado para username '{}': {}", username, (userDetails != null ? "Sucesso" : "Falha"));
                    if (userDetails != null) {
                         logger.info("Autoridades carregadas para '{}': {}", username, userDetails.getAuthorities());
                    }
                     // -----------------------


                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        // --- LOGS DE DEPURACAO ---
                        logger.info("Usuário '{}' autenticado e setado no SecurityContextHolder.", username);
                         // -----------------------
                    } else {
                        // --- LOGS DE DEPURACAO ---
                         logger.warn("UserDetails nulo para username '{}' após validação do token.", username);
                        // -----------------------
                    }
                } else {
                     // --- LOGS DE DEPURACAO ---
                     logger.warn("Token JWT inválido ou expirado para URI: {}", request.getRequestURI());
                     // -----------------------
                }
            } else {
                 // --- LOGS DE DEPURACAO ---
                 logger.info("Nenhum token JWT encontrado na requisição para URI: {}", request.getRequestURI());
                 // -----------------------
            }

        } catch (Exception e) {
            // --- LOGS DE DEPURACAO ---
            logger.error("Erro inesperado no SecurityFilter ao processar requisição para URI {}: {}", request.getRequestURI(), e.getMessage(), e); // Logar exceções detalhadamente
            // -----------------------
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
