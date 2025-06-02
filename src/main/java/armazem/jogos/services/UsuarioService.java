package armazem.jogos.services;

import armazem.jogos.dtos.RegistroUsuarioRequestDTO;
import armazem.jogos.dtos.UsuarioDTO;
import armazem.jogos.entities.Role;
import armazem.jogos.entities.Usuario;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.RoleRepository;
import armazem.jogos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));
    }

    @Transactional
    public Usuario registrarNovoUsuario(RegistroUsuarioRequestDTO registroDTO, String defaultRoleName) {
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
            throw new IllegalArgumentException("Erro: Username já está em uso!");
        }
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
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
                .orElseThrow(() -> new RuntimeException("Erro: Role padrão " + defaultRoleName + " não encontrada."));
        roles.add(userRole);
        usuario.setRoles(roles);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        return convertToDto(usuario);
    }

    @Transactional
    public UsuarioDTO atualizarRolesUsuario(Long usuarioId, Set<String> nomesDasRoles) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + usuarioId));

        Set<Role> novasRoles = new HashSet<>();
        for (String nomeRole : nomesDasRoles) {
            Role role = roleRepository.findByNome(nomeRole)
                    .orElseThrow(() -> new ResourceNotFoundException("Role '" + nomeRole + "' não encontrada."));
            novasRoles.add(role);
        }
        usuario.setRoles(novasRoles);
        usuarioRepository.save(usuario);
        return convertToDto(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
        // Adicionar validações: não permitir deletar o último admin, etc.
        usuarioRepository.deleteById(id);
    }


    public UsuarioDTO convertToDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setEnabled(usuario.isEnabled());
        dto.setRoles(usuario.getRoles().stream().map(Role::getNome).collect(Collectors.toSet()));
        return dto;
    }
}