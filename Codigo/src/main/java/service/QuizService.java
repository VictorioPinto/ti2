package service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import spark.Request;
import spark.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class QuizService {
	private dao.QuizDAO quizDAO = new dao.QuizDAO();

    // Rota para entregar as perguntas ao frontend
    public Object getPerguntasQuiz(Request request, Response response) {
        response.type("application/json");
        int quizId;
        try {
            quizId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "{\"erro\": \"ID do quiz inválido\"}";
        }

        com.google.gson.JsonArray perguntas = quizDAO.getPerguntasDoQuiz(quizId);
        
        response.status(200);
        return perguntas.toString();
    }
    public Object cadastrar(Request request, Response response) {
        JsonObject dados = JsonParser.parseString(request.body()).getAsJsonObject();
        
        // Opcional: Verificar aqui se o usuário da sessão é ADM por segurança
        
        if (quizDAO.cadastrarQuizCompleto(dados)) {
            response.status(201);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false}";
        }
    }
    public Object avaliarRespostaAberta(Request request, Response response) {
        String perguntaDada = request.queryParams("pergunta");
        String respostaAluno = request.queryParams("resposta_aluno");
        
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.trim().isEmpty()) {
            githubToken = ""; 
        }
        
        String githubEndpoint = "https://models.inference.ai.azure.com/chat/completions";
        
        // Usamos um replace simples para evitar que aspas duplas na resposta do aluno quebrem o JSON da requisição
        String promptLimpo = ("Pergunta: " + perguntaDada + " | Resposta do aluno: " + respostaAluno)
                              .replace("\"", "\\\"");
        
        String corpoRequisicao = "{"
            + "\"model\": \"gpt-4o-mini\","
            + "\"messages\": ["
            + "  {\"role\": \"system\", \"content\": \"Você é um professor de finanças. Avalie a resposta do aluno. Retorne APENAS um JSON estruturado com 'porcentagem' (int de 0 a 100) e 'feedback' (string com a correção ou dicas para 100%). Não use formatação markdown.\"},"
            + "  {\"role\": \"user\", \"content\": \"" + promptLimpo + "\"}"
            + "],"
            + "\"temperature\": 0.2"
            + "}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requisicaoIA = HttpRequest.newBuilder()
                    .uri(URI.create(githubEndpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + githubToken)
                    .POST(HttpRequest.BodyPublishers.ofString(corpoRequisicao))
                    .build();
            HttpResponse<String> respostaIA = client.send(requisicaoIA, HttpResponse.BodyHandlers.ofString());
            
            // ---> ADICIONE O PRINT EXATAMENTE AQUI <---
            System.out.println("Resposta da API: " + respostaIA.body());
            
            // 1. Converte a string de resposta da API do GitHub para um objeto JSON
            JsonObject jsonResponse = JsonParser.parseString(respostaIA.body()).getAsJsonObject();
            
            // 2. Navega no JSON da IA para pegar a string de conteúdo gerada por ela
            String contentString = jsonResponse.getAsJsonArray("choices")
                                               .get(0).getAsJsonObject()
                                               .getAsJsonObject("message")
                                               .get("content").getAsString();
                                               
            // 3. Converte a string que a IA gerou de volta para um objeto JSON (para pegar porcentagem e feedback)
            JsonObject avaliacaoIA = JsonParser.parseString(contentString).getAsJsonObject();
            
            int porcentagem = avaliacaoIA.get("porcentagem").getAsInt();
            String feedback = avaliacaoIA.get("feedback").getAsString();
            boolean aprovado = porcentagem >= 70; // Regra de aprovação que você definiu
            
            // 4. Monta a resposta final estruturada para o seu frontend usando Gson
            JsonObject resultadoFinal = new JsonObject();
            resultadoFinal.addProperty("success", true);
            resultadoFinal.addProperty("porcentagem", porcentagem);
            resultadoFinal.addProperty("aprovado", aprovado);
            resultadoFinal.addProperty("feedback", feedback);
            
            response.type("application/json");
            response.status(200);
            
            return resultadoFinal.toString();

        } catch (Exception e) {
            System.err.println("Erro ao processar a avaliação: " + e.getMessage());
            response.status(500);
            return "{\"success\": false, \"message\": \"Erro interno ao processar a avaliação com a IA.\"}";
        }
    }
    public Object concluirQuiz(Request request, Response response) {
        Integer usuarioId = request.session().attribute("usuario_logado");
        int quizId = Integer.parseInt(request.params(":id"));
        
        if(usuarioId != null) {
            quizDAO.registrarQuizFeito(usuarioId, quizId);
        }
        
        response.status(200);
        return "{\"success\": true}";
    }
    public Object atualizar(Request request, Response response) {
        int quizId;
        try {
            quizId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "{\"success\": false, \"erro\": \"ID inválido\"}";
        }

        JsonObject dados = JsonParser.parseString(request.body()).getAsJsonObject();
        
        if (quizDAO.atualizarQuizCompleto(quizId, dados)) {
            response.status(200);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false}";
        }
    }
 // --- NOVO: ROTA PARA DELETAR QUIZ ---
    public Object deletarQuiz(Request request, Response response) {
        int quizId;
        try {
            quizId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "{\"success\": false, \"erro\": \"ID inválido\"}";
        }

        if (quizDAO.deleteQuiz(quizId)) {
            response.status(200);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false, \"erro\": \"Erro ao deletar o quiz\"}";
        }
    }
}