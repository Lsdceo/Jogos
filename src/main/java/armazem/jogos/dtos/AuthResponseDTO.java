package armazem.jogos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String mensagem;
    private Long usuarioId; // Opcional
    private String username; // Opcional
}