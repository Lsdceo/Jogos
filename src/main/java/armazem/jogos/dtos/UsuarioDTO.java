package armazem.jogos.dtos;

import armazem.jogos.entities.Usuario;

public class UsuarioDTO {

    private String username;
    private String password;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UsuarioDTO(Usuario usuario) {
        this.username = usuario.getUsername();
        this.password = usuario.getPassword();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
