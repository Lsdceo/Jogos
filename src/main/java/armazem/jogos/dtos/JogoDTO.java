package armazem.jogos.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class JogoDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private BigDecimal precoSugerido;
    private String genero;
    private String desenvolvedora;
    private String publicadora;
    private String urlImagemCapa;

    public String getUrlImagemCapa() {
        return urlImagemCapa;
    }

    public void setUrlImagemCapa(String urlImagemCapa) {
        this.urlImagemCapa = urlImagemCapa;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoSugerido() {
        return precoSugerido;
    }

    public void setPrecoSugerido(BigDecimal precoSugerido) {
        this.precoSugerido = precoSugerido;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDesenvolvedora() {
        return desenvolvedora;
    }

    public void setDesenvolvedora(String desenvolvedora) {
        this.desenvolvedora = desenvolvedora;
    }

    public String getPublicadora() {
        return publicadora;
    }

    public void setPublicadora(String publicadora) {
        this.publicadora = publicadora;
    }
}