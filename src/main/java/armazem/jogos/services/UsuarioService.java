package armazem.jogos.services;

import armazem.jogos.dtos.RegistroUsuarioRequestDTO;
import armazem.jogos.dtos.UsuarioDTO;
import armazem.jogos.entities.Role;
import armazem.jogos.entities.Usuario;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.RoleRepository;
import armazem.jogos.repositories.UsuarioRepository;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority; // Importar GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class); // Adicionar Logger


    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // --- LOGS DE DEPURACAO ---
        logger.info("Attempting to load user by username: {}", username);
        // -----------------------
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));
        // --- LOGS DE DEPURACAO ---
        logger.info("User '{}' found. Authorities: {}", username, usuario.getAuthorities());
        // -----------------------
        return usuario;
    }

    @Transactional
    public Usuario registrarNovoUsuario(RegistroUsuarioRequestDTO registroDTO, String defaultRoleName) {
        // --- LOGS DE DEPURACAO ---
        logger.info("Attempting to register new user with username: {}", registroDTO.getUsername());
        // -----------------------
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
             // --- LOGS DE DEPURACAO ---
            logger.warn("Registration failed: Username already in use: {}", registroDTO.getUsername());
             // -----------------------
            throw new IllegalArgumentException("Erro: Username já está em uso!");
        }
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
             // --- LOGS DE DEPURACAO ---
            logger.warn("Registration failed: Email already in use: {}", registroDTO.getEmail());
             // -----------------------
            throw new IllegalArgumentException("Erro: Email já está em uso!");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setEmail(registroDTO.getEmail());
        usuario.setNomeCompleto(registroDTO.getNomeCompleto());
        usuario.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByNome(defaultRoleName)
                .orElseThrow(() -> {
                    // --- LOGS DE DEPURACAO ---
                    logger.error("Registration failed: Default role '{}' not found.", defaultRoleName);
                    // -----------------------
                    return new RuntimeException("Erro: Role padrão " + defaultRoleName + " não encontrada.");
                });
        roles.add(userRole);
        usuario.setRoles(roles);

        Usuario savedUser = usuarioRepository.save(usuario);
         // --- LOGS DE DEPURACAO ---
        logger.info("User '{}' registered successfully with roles: {}", savedUser.getUsername(), savedUser.getRoles().stream().map(Role::getNome).collect(Collectors.toSet()));
        // -----------------------
        return savedUser;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodosUsuarios() {
         // --- LOGS DE DEPURACAO ---
        logger.info("Listing all users.");
        // -----------------------
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorId(Long id) {
         // --- LOGS DE DEPURACAO ---
        logger.info("Searching for user with id: {}", id);
        // -----------------------
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                     // --- LOGS DE DEPURACAO ---
                    logger.warn("User not found with id: {}", id);
                     // -----------------------
                    return new ResourceNotFoundException("Usuário não encontrado com id: " + id);
                });
        // --- LOGS DE DEPURACAO ---
        logger.info("User found with id: {}", id);
        // -----------------------
        return convertToDto(usuario);
    }

    @Transactional
    public UsuarioDTO atualizarRolesUsuario(Long usuarioId, Set<String> nomesDasRoles) {
         // --- LOGS DE DEPURACAO ---
        logger.info("Attempting to update roles for user id {}. New roles: {}", usuarioId, nomesDasRoles);
         // -----------------------
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    // --- LOGS DE DEPURACAO ---
                    logger.warn("User not found with id {} for role update.", usuarioId);
                    // -----------------------
                    return new ResourceNotFoundException("Usuário não encontrado com id: " + usuarioId);
                });

        Set<Role> novasRoles = new HashSet<>();
        for (String nomeRole : nomesDasRoles) {
            Role role = roleRepository.findByNome(nomeRole)
                    .orElseThrow(() -> {
                         // --- LOGS DE DEPURACAO ---
                        logger.error("Role '{}' not found for user id {}.", nomeRole, usuarioId);
                         // -----------------------
                        return new ResourceNotFoundException("Role '" + nomeRole + "' não encontrada.");
                    });
            novasRoles.add(role);
        }
        usuario.setRoles(novasRoles);
        usuarioRepository.save(usuario);
        // --- LOGS DE DEPURACAO ---
        logger.info("Roles updated successfully for user id {}. New roles: {}", usuarioId, usuario.getRoles().stream().map(Role::getNome).collect(Collectors.toSet()));
        // -----------------------
        return convertToDto(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id) {
         // --- LOGS DE DEPURACAO ---
        logger.info("Attempting to delete user with id: {}", id);
         // -----------------------
        if (!usuarioRepository.existsById(id)) {
             // --- LOGS DE DEPURACAO ---
            logger.warn("User not found with id {} for deletion.", id);
             // -----------------------
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
        usuarioRepository.deleteById(id);
         // --- LOGS DE DEPURACAO ---
        logger.info("User with id {} deleted successfully.", id);
         // -----------------------
    }


    public UsuarioDTO convertToDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setEnabled(usuario.isEnabled());
        // Convertendo authorities para Set<String> para o DTO
        dto.setRoles(usuario.getAuthorities().stream()
                         .map(GrantedAuthority::getAuthority)
                         .collect(Collectors.toSet())); // Usando getAuthority() que inclui "ROLE_"
        return dto;
    }
}
