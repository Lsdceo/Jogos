package armazem.jogos.config;

import armazem.jogos.entities.Role;
import armazem.jogos.entities.Usuario;
import armazem.jogos.repositories.RoleRepository;
import armazem.jogos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Criar Roles se não existirem (agora sem o prefixo "ROLE_")
        Role adminRole = roleRepository.findByNome("ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setNome("ADMIN"); // Salva como "ADMIN"
            return roleRepository.save(newRole);
        });

        Role userRole = roleRepository.findByNome("USUARIO").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setNome("USUARIO"); // Salva como "USUARIO"
            return roleRepository.save(newRole);
        });

        // Criar Usuário Admin se não existir
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario adminUser = new Usuario();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpass")); // Senha padrão, mude em produção!
            adminUser.setEmail("admin@example.com");
            adminUser.setNomeCompleto("Administrador do Sistema");
            adminUser.setEnabled(true);
            adminUser.setRoles(Set.of(adminRole)); // Associa a role "ADMIN"
            usuarioRepository.save(adminUser);
            System.out.println(">>> Usuário admin criado com senha 'adminpass'");
        }

        // Criar Usuário Comum de Teste se não existir
        if (!usuarioRepository.existsByUsername("usuario")) {
            Usuario commonUser = new Usuario();
            commonUser.setUsername("usuario");
            commonUser.setPassword(passwordEncoder.encode("usuariopass")); // Senha padrão
            commonUser.setEmail("usuario@example.com");
            commonUser.setNomeCompleto("Usuário Comum");
            commonUser.setEnabled(true);
            commonUser.setRoles(Set.of(userRole)); // Associa a role "USUARIO"
            usuarioRepository.save(commonUser);
            System.out.println(">>> Usuário comum 'usuario' criado com senha 'usuariopass'");
        }
    }
}