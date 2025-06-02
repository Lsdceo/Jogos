package armazem.jogos.services;

import armazem.jogos.dtos.UsuarioDTO;
import armazem.jogos.entities.Usuario;
import armazem.jogos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario salvar(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setPassword(new BCryptPasswordEncoder().encode(usuarioDTO.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> atualizar(Long id, UsuarioDTO usuarioDTO) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setUsername(usuarioDTO.getUsername());
            usuario.setPassword(new BCryptPasswordEncoder().encode(usuarioDTO.getPassword()));
            return usuarioRepository.save(usuario);
        });
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }
}

