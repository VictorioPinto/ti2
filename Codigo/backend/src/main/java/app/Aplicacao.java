package app;

import static spark.Spark.*;

import service.FaqService;
import service.TrilhaService;
import service.UsuarioService;
<<<<<<< HEAD
import service.QuizService;
=======
import model.ForumTopico;
import service.ForumService;
>>>>>>> e77cfb23c9aa90638fd156ded1ce3d4adde4696e

public class Aplicacao {
    
    private static UsuarioService usuarioService = new UsuarioService();
    private static TrilhaService trilhaService = new TrilhaService();
<<<<<<< HEAD
    private static QuizService quizService = new QuizService();

    public static void main(String[] args) {
        port(8080); 
=======
    private static FaqService faqService = new FaqService();

    public static void main(String[] args) {
        // Define a porta do servidor
        port(8081); 

        // Configura o Spark para servir arquivos estáticos (frontend)
        // Certifique-se de que sua pasta 'frontend' está em src/main/resources/public
>>>>>>> e77cfb23c9aa90638fd156ded1ce3d4adde4696e
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
<<<<<<< HEAD

        post("/questionario/salvar", (request, response) -> usuarioService.avaliarDiagnostico(request, response));
        get("/trilha/quizzes", (request, response) -> trilhaService.listarQuizzesDoUsuario(request, response));
=======
       
     // --- Rotas FAQ ---

        post("/faq/insert", (request, response) ->
            faqService.insert(request, response));

        get("/faq/listar", (request, response) ->
            faqService.listar(request, response));

        // DEIXA O :id DEPOIS
        get("/faq/:id", (request, response) ->
            faqService.get(request, response));

        put("/faq/update/:id", (request, response) ->
            faqService.update(request, response));

        get("/faq/delete/:id", (request, response) ->
            faqService.delete(request, response));
        
        //---------rota foruns----------
        ForumService forumService = new ForumService();

     // Ativando as rotas do fórum
     post("/forum", (req, res) -> forumService.insert(req, res));
     
     get("/forum", (req, res) -> forumService.listarTodos(req, res));
>>>>>>> e77cfb23c9aa90638fd156ded1ce3d4adde4696e
        
        System.out.println("Servidor Wise Capital rodando em http://localhost:8080");
    }
}