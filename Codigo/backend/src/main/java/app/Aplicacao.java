package app;

import static spark.Spark.*;
import service.TrilhaService;
import service.UsuarioService;
import service.QuizService;

public class Aplicacao {
    
    private static UsuarioService usuarioService = new UsuarioService();
    private static TrilhaService trilhaService = new TrilhaService();
    private static QuizService quizService = new QuizService();

    public static void main(String[] args) {
        port(8080); 
        staticFiles.location("/public"); 
     

        get("/quiz/:id/perguntas", (request, response) -> quizService.getPerguntasQuiz(request, response));
        post("/questionario/avaliar-aberta", (request, response) -> quizService.avaliarRespostaAberta(request, response));
        post("/quiz/cadastrar", (request, response) -> quizService.cadastrar(request, response));
        
        post("/usuario/insert", (request, response) -> usuarioService.insert(request, response));
        post("/usuario/login", (request, response) -> usuarioService.login(request, response));
        post("/usuario/logout", (request, response) -> usuarioService.logout(request, response));
        get("/usuario/atual", (request, response) -> usuarioService.getUsuarioAtual(request, response));
        get("/usuario/:id", (request, response) -> usuarioService.get(request, response));

        get("/trilha/niveis", (request, response) -> trilhaService.listarNiveis(request, response));
        post("/trilha/avancar", (request, response) -> trilhaService.avancarNivel(request, response));

        post("/questionario/salvar", (request, response) -> usuarioService.avaliarDiagnostico(request, response));
        get("/trilha/quizzes", (request, response) -> trilhaService.listarQuizzesDoUsuario(request, response));
        
        System.out.println("Servidor Wise Capital rodando em http://localhost:8080");
    }
}