import java.io.Serializable;

public class Livro implements Serializable {
    private String titulo;
    private String autor;
    private String genero;
    private int exemplares;

    // Construtor
    public Livro(String titulo, String autor, String genero, int exemplares) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.exemplares = exemplares;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getGenero() {
        return genero;
    }

    public int getExemplares() {
        return exemplares;
    }

    // Setters
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setExemplares(int exemplares) {
        this.exemplares = exemplares;
    }

    @Override
    public String toString() {
        return "Livro [titulo=" + titulo + ", autor=" + autor + ", genero=" + genero + ", exemplares=" + exemplares + "]";
    }
}
