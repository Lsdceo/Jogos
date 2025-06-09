package armazem.jogos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // Opcional: para o cliente saber quando expira
    private String username; // Opcional: para conveniência do frontend
    // private List<String> roles; // Opcional: se o frontend precisar das roles
}