package model;

public class Quiz {
    private int id;
    private int nivelId;
    private String titulo;

    public Quiz(int id, int nivelId, String titulo) {
        this.id = id;
        this.nivelId = nivelId;
        this.titulo = titulo;
    }

    public int getId() { return id; }
    public int getNivelId() { return nivelId; }
    public String getTitulo() { return titulo; }
}