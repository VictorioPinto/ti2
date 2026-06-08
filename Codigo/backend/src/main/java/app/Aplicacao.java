zpackage app;

import static spark.Spark.*;

import service.FaqService;
import service.TrilhaService;
import service.UsuarioService;
import service.QuizService;
import service.ForumService;

public class Aplicacao {
    
    private static UsuarioService usuarioService = new UsuarioService();
    private static TrilhaService trilhaService = new TrilhaService();
    private static QuizService quizService = new QuizService();
    private static FaqService faqService = new FaqService();
    private static ForumService forumService = new ForumService();

    public static void main(String[] args) {
        // Define a porta do servidor
        port(8080); 

        // Configura o Spark para servir arquivos estáticos (frontend)
        // Certifique-se de que sua pasta 'frontend' está em src/main/resources/public
        staticFiles.location("/public"); 

        // --- Rotas de Usuário ---
        post("/usuario/insert", (request, response) -> usuarioService.insert(request, response));
        post("/usuario/login", (request, response) -> usuarioService.login(request, response));
        post("/usuario/logout", (request, response) -> usuarioService.logout(request, response));
        
        // A rota /atual DEVE vir antes da rota /:id
        get("/usuario/atual", (request, response) -> usuarioService.getUsuarioAtual(request, response));
        get("/usuario/:id", (request, response) -> usuarioService.get(request, response));

        // --- Rotas de Trilha ---
        get("/trilha/niveis", (request, response) -> trilhaService.listarNiveis(request, response));
        post("/trilha/avancar", (request, response) -> trilhaService.avancarNivel(request, response));

        // --- Rotas de Quiz e Questionário ---
        get("/quiz/:id/perguntas", (request, response) -> quizService.getPerguntasQuiz(request, response));
        post("/quiz/cadastrar", (request, response) -> quizService.cadastrar(request, response));
        post("/questionario/avaliar-aberta", (request, response) -> quizService.avaliarRespostaAberta(request, response));
        post("/questionario/salvar", (request, response) -> usuarioService.avaliarDiagnostico(request, response));

        // --- Rotas FAQ ---
        post("/faq/insert", (request, response) -> faqService.insert(request, response));
        get("/faq/listar", (request, response) -> faqService.listar(request, response));
        get("/faq/:id", (request, response) -> faqService.get(request, response));
        put("/faq/update/:id", (request, response) -> faqService.update(request, response));
        get("/faq/delete/:id", (request, response) -> faqService.delete(request, response));
        
        // --- Rotas de Fórum ---
        post("/forum", (req, res) -> forumService.insert(req, res));
        get("/forum", (req, res) -> forumService.listarTodos(req, res));
        
        // Corrigido para refletir a porta 8081 configurada no topo do arquivo
        System.out.println("Servidor Wise Capital rodando em http://localhost:8080");
    }
}