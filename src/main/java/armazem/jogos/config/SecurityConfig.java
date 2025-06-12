package armazem.jogos.config;

import armazem.jogos.security.SecurityFilter; // Importe seu novo filtro
import armazem.jogos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // Import para SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Mantido, caso use @PreAuthorize em algum lugar
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityFilter securityFilter; // Injeção do seu novo filtro

    // Construtor atualizado para injetar o SecurityFilter
    public SecurityConfig(@Lazy UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder,
                          SecurityFilter securityFilter) { // Adicionar injeção
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.securityFilter = securityFilter;
    }

    // Bean PasswordEncoder (pode já existir no seu código original, só garantir que é static ou que não há ciclo)
    @Bean
    public static PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService); // Seu UsuarioService que implementa UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder); // PasswordEncoder injetado
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Mantém configuração CORS
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF (comum para APIs stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ESSENCIAL PARA JWT
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/auth/registrar", "/api/auth/login").permitAll() // Endpoints de autenticação públicos
                                // Suas regras de autorização existentes:
                                // ALTERADO: Permite acesso público a GET /api/jogos/** para depuração
                                .requestMatchers(HttpMethod.GET, "/api/jogos/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/jogos").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/jogos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/jogos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/plataformas/**").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers(HttpMethod.POST, "/api/plataformas").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/plataformas/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/plataformas/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/depositos/**").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers(HttpMethod.POST, "/api/depositos").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/depositos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/depositos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/estoque/consultar").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers(HttpMethod.GET, "/api/estoque/valor-total").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers(HttpMethod.POST, "/api/estoque/movimentar").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/estoque/transferir").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/relatorios/**").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers("/api/auth/usuarios/**").hasRole("ADMIN")
                                .requestMatchers("/api/auth/me").authenticated()
                                .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
                )
                // .httpBasic(AbstractHttpConfigurer::disable) // DESABILITAR HTTP BASIC
                .authenticationProvider(authenticationProvider()) // Adiciona seu provedor de autenticação
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class); // ADICIONA SEU FILTRO JWT

        return http.build();
    }

    // Bean CorsConfigurationSource (mantido como estava ou como você configurou)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:5173",
                "http://localhost:8081",
                "https://majestic-lebkuchen-9305f3.netlify.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "X-Requested-With",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
