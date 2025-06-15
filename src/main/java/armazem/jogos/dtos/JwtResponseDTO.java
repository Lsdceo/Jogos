package armazem.jogos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // Optional: for the client to know when it expires
    private String username; // Optional: for frontend convenience

    // Add these fields
    private Long id;
    private String nomeCompleto;

    private List<String> roles; // Optional: if the frontend needs the roles
}
