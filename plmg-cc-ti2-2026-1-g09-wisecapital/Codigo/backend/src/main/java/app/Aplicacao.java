package app;

import static spark.Spark.*;
import service.TrilhaService;
import service.UsuarioService;

public class Aplicacao {
    // Ambas as instâncias de serviço devem ser estáticas para serem usadas no main
    private static UsuarioService usuarioService = new UsuarioService();
    private static TrilhaService trilhaService = new TrilhaService();

    public static void main(String[] args) {
        // Define a porta do servidor
        port(8080); 

        // Configura o Spark para servir arquivos estáticos (frontend)
        // Certifique-se de que sua pasta 'frontend' está em src/main/resources/public
        staticFiles.location("/public"); 

        // --- Rotas de Usuário ---
        // Registro de novo usuário
        post("/usuario/insert", (request, response) -> usuarioService.insert(request, response));

        // Autenticação de login
        post("/usuario/login", (request, response) -> usuarioService.login(request, response));

        // Busca dados de um usuário específico
        get("/usuario/:id", (request, response) -> usuarioService.get(request, response));

        // --- Rotas da Trilha de Conhecimento ---
        // Carrega os níveis para exibir na página da trilha
        get("/trilha/niveis", (request, response) -> trilhaService.listarNiveis(request, response));

        // Avança o nível do usuário após completar um quiz
        post("/trilha/avancar", (request, response) -> trilhaService.avancarNivel(request, response));
        
        System.out.println("Servidor Wise Capital rodando em http://localhost:8080");
    }
}