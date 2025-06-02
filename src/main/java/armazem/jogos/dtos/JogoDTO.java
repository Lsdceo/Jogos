package armazem.jogos.dtos;

import armazem.jogos.entities.Jogo;

public class JogoDTO {

    private String titulo;
    private String plataforma;
    private String midia; // física ou digital
    private String genero;
    private int estoque;

    public JogoDTO(String titulo, String plataforma,
                   String midia, String genero, int estoque) {
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.midia = midia;
        this.genero = genero;
        this.estoque = estoque;
    }

    public JogoDTO (){

    }

    public JogoDTO(Jogo jogo) {
        this.titulo = jogo.getTitulo();
        this.plataforma = jogo.getPlataforma();
        this.midia = jogo.getMidia();
        this.genero = jogo.getGenero();
        this.estoque = jogo.getEstoque();
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
