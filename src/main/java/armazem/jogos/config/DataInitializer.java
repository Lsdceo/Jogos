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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Para codificar senhas

    @Override
    @Transactional // Garante que as operações sejam atômicas
    public void run(String... args) throws Exception {
        // Criar Roles se não existirem (usando o prefixo "ROLE_")
        Role adminRole = roleRepository.findByNome("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setNome("ROLE_ADMIN"); // Salva como "ROLE_ADMIN"
                    return roleRepository.save(newRole);
                });

        Role userRole = roleRepository.findByNome("ROLE_USUARIO")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setNome("ROLE_USUARIO"); // Salva como "ROLE_USUARIO"
                    return roleRepository.save(newRole);
                });

        // --- Inicialização do Usuário Admin ---
        String adminUsername = "admin@example.com";
        // Verifica se o usuário admin já existe pelo username "admin@example.com"
        Optional<Usuario> existingAdmin = usuarioRepository.findByUsername(adminUsername);

        if (existingAdmin.isEmpty()) {
            // Se o usuário admin NÃO existe, cria
            Usuario adminUser = new Usuario();
            adminUser.setUsername(adminUsername);
            // Codifica a senha "adminpass" usando o passwordEncoder.
            // O hash gerado será diferente a cada execução, mas BCryptPasswordEncoder saberá comparar.
            adminUser.setPassword(passwordEncoder.encode("adminpass"));

            adminUser.setEmail("admin@example.com");
            adminUser.setNomeCompleto("Administrador do Sistema");
            adminUser.setEnabled(true);

            // Associa a role "ROLE_ADMIN"
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            usuarioRepository.save(adminUser);
            System.out.println(">>> Usuário admin (" + adminUsername + ") criado com senha 'adminpass'");

        } else {
            // Se o usuário admin JÁ existe, verifica e adiciona o papel se necessário
            System.out.println(">>> Usuário admin (" + adminUsername + ") já existe. Verificando papéis.");
            Usuario adminExistente = existingAdmin.get();

            // Verifica se o papel ROLE_ADMIN já está associado
            if (!adminExistente.getRoles().contains(adminRole)) {
                adminExistente.getRoles().add(adminRole);
                usuarioRepository.save(adminExistente); // Salva para atualizar a lista de papéis
                System.out.println(">>> Papel ROLE_ADMIN adicionado ao usuário admin existente.");
            }
             // Opcional: Logar o hash da senha existente para verificação, se necessário
            // System.out.println(">>> Hash da senha do usuário admin existente: " + adminExistente.getPassword());
        }

        // --- Inicialização do Usuário Comum de Teste ---
        String commonUsername = "usuario";
         if (!usuarioRepository.existsByUsername(commonUsername)) { // Continua usando existsByUsername para o comum
            Usuario commonUser = new Usuario();
            commonUser.setUsername(commonUsername);
            commonUser.setPassword(passwordEncoder.encode("usuariopass")); // Codifica "usuariopass"
            commonUser.setEmail("usuario@example.com");
            commonUser.setNomeCompleto("Usuário Comum");
            commonUser.setEnabled(true);
            commonUser.setRoles(Set.of(userRole)); // Associa a role "ROLE_USUARIO"

            usuarioRepository.save(commonUser);
            System.out.println(">>> Usuário comum '" + commonUsername + "' criado com senha 'usuariopass'");
        } else {
             System.out.println(">>> Usuário comum '" + commonUsername + "' já existe.");
         }

        // ... continue com outras inicializações se houver ...
    }
}
