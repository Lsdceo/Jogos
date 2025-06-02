package armazem.jogos.config;

import armazem.jogos.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy; // Importe @Lazy
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Opção 1: Mantenha @Autowired para UsuarioService se ele for injetado em outro lugar.
    // Se não, podemos remover e usar o método configureGlobal.
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder; // Injete PasswordEncoder aqui

    // Usar injeção via construtor
    // Usar @Lazy na injeção do UsuarioService no construtor para quebrar o ciclo.
    // PasswordEncoder pode ser injetado diretamente.
    public SecurityConfig(@Lazy UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    // O Bean PasswordEncoder é crucial e deve ser definido aqui.
    @Bean
    public static PasswordEncoder passwordEncoderBean() { // Tornar static ajuda em alguns cenários de ciclo
        return new BCryptPasswordEncoder();
    }

    // Maneira moderna de expor AuthenticationManager como um bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Alternativa: configurar o DaoAuthenticationProvider explicitamente
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder); // Use o PasswordEncoder injetado
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/auth/registrar").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/jogos/**").hasAnyRole("ADMIN", "USUARIO")
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
                                // .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                // Explicitamente adicionar nosso DaoAuthenticationProvider
                .authenticationProvider(authenticationProvider());


        // http.headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()));

        return http.build();
    }
}