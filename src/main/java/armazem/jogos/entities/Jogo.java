package armazem.jogos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Jogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A plataforma é obrigatória")
    private String plataforma;

    @NotBlank(message = "O tipo de mídia é obrigatório")
    private String midia; // física ou digital

    @NotBlank(message = "O gênero é obrigatório")
    private String genero;

    @Min(value = 0, message = "O estoque não pode ser negativo")
    private int estoque;

    public Jogo() {
    }

    public Jogo(Long id, String titulo, String plataforma,
                String midia, String genero, int estoque) {
        this.id = id;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.midia = midia;
        this.genero = genero;
        this.estoque = estoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getMidia() {
        return midia;
    }

    public void setMidia(String midia) {
        this.midia = midia;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}
