package model;

public class Usuario {
    
    // Atributos da classe
    private int id;
    private String login;
    private String senha;
    private String nome;
    private String email;
    private int streakDays;
    private int nivelAtualId;
    // Construtor padrão (vazio)
    public Usuario() {
    }

    // Construtor com todos os parâmetros
    public Usuario(int id, String login, String senha, String nome, String email, int streakDays, int nivelAtualId) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
        this.streakDays = streakDays;
        this.nivelAtualId = nivelAtualId;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public int getNivelAtualId() {
        return nivelAtualId;
    }

    public void setNivelAtualId(int nivelAtualId) {
        this.nivelAtualId = nivelAtualId;
    }
}