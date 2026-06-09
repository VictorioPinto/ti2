package model;
import java.sql.Timestamp;

public class ForumTopico {
    private int id;
    private int usuarioId;
    private String titulo;
    private String conteudo;
    private String imagemUrl; // Novo campo
    private Timestamp dataCriacao;

    public ForumTopico(int id, int usuarioId, String titulo, String conteudo, String imagemUrl, Timestamp dataCriacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.imagemUrl = imagemUrl;
        this.dataCriacao = dataCriacao;
    }
    
    public ForumTopico() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
}