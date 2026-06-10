package service;

import dao.UsuarioDAO;
import model.Usuario;
import spark.Request;
import spark.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.mindrot.jbcrypt.BCrypt; 

public class UsuarioService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    public Object avaliarDiagnostico(Request request, Response response) {
        String respostasJson = request.queryParams("respostas_json");

        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.trim().isEmpty()) {
            githubToken = "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"; 
        }
        
        String githubEndpoint = "https://models.inference.ai.azure.com/chat/completions";
        String modelo = "gpt-4o-mini";

        String corpoRequisicao = "{"
            + "\"model\": \"" + modelo + "\","
            + "\"messages\": ["
            + "  {\"role\": \"system\", \"content\": \"Você é um analista financeiro. Avalie as respostas de investimentos do usuário e retorne APENAS um número inteiro de 1 a 3 (1=Iniciante, 2=Intermediário, 3=Avançado). Não escreva mais nenhuma palavra além do número.\"},"
            + "  {\"role\": \"user\", \"content\": " + respostasJson + "}"
            + "],"
            + "\"max_tokens\": 5,"
            + "\"temperature\": 0.1" 
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
            String corpoResposta = respostaIA.body();
            
            int nivelSugerido = 1; 
            if (corpoResposta.contains("\"content\":\"2\"") || corpoResposta.contains("\"content\": \"2\"")) {
                nivelSugerido = 2;
            } else if (corpoResposta.contains("\"content\":\"3\"") || corpoResposta.contains("\"content\": \"3\"")) {
                nivelSugerido = 3;
            }

            
            Integer usuarioId = request.session().attribute("usuario_logado");
            if (usuarioId != null) {
                usuarioDAO.salvarQuestionario(usuarioId, respostasJson, nivelSugerido);
            } else {
                response.status(401);
                return "{\"success\": false, \"message\": \"Usuário não autenticado.\"}";
            }

            response.type("application/json");
            response.status(200);
            return "{\"success\": true, \"nivelSugerido\": " + nivelSugerido + "}";

        } catch (Exception e) {
            System.err.println("Erro ao chamar IA: " + e.getMessage());
            response.status(500);
            return "{\"success\": false, \"message\": \"Erro ao procesAsar o diagnóstico com a IA.\"}";
        }
    }

    public Object insert(Request request, Response response) {
        String login = request.queryParams("login");
        String senhaPura = request.queryParams("senha");
        String nome = request.queryParams("nome");
        String email = request.queryParams("email");
        String senhaCriptografada = BCrypt.hashpw(senhaPura, BCrypt.gensalt());
        Usuario usuario = new Usuario(-1, login, senhaCriptografada, nome, email, 0, 1, false);
        if (usuarioDAO.insert(usuario)) {
            response.status(201); 
            return "Usuário " + nome + " registrado com sucesso!";
        } else {
            response.status(500); 
            return "Erro ao registrar usuário.";
        }
    }
    public Object listar(Request request, Response response) {
        response.type("application/json");
        return "[{\"message\": \"Lista de Usuarios\"}]";
    }

    public Object update(Request request, Response response) {
        return "{\"message\": \"Usuario atualizado\"}";
    }

    public Object remove(Request request, Response response) {
        return "{\"message\": \"Usuario removido\"}";
    }

    public Object login(Request request, Response response) {
        String login = request.queryParams("login");
        String senhaDigitada = request.queryParams("senha");
        Usuario usuario = usuarioDAO.getByLogin(login);
        response.type("application/json");
        if (usuario != null && BCrypt.checkpw(senhaDigitada, usuario.getSenha())) {
            request.session(true); 
            request.session().attribute("usuario_logado", usuario.getId());
            boolean primeiroAcesso = !usuarioDAO.jaRespondeuQuestionario(usuario.getId());
            response.status(200); 
            return "{\"success\": true, \"primeiroAcesso\": " + primeiroAcesso + ", \"nome\": \"" + usuario.getNome() + "\", \"adm\": " + usuario.isAdm() + "}";
        } else {
            response.status(401); 
            return "{\"success\": false, \"message\": \"Usuário ou senha inválidos.\"}";
        }
    }
    
    public Object getUsuarioAtual(Request request, Response response) {
        response.type("application/json");
        Integer idLogado = request.session().attribute("usuario_logado");
        
        if (idLogado != null) {
            Usuario usuario = usuarioDAO.get(idLogado);
            if (usuario != null) {
                response.status(200);
                return "{\"logged\": true, \"id\": " + usuario.getId() + ", \"nome\": \"" + usuario.getNome() + "\", \"adm\": " + usuario.isAdm() + "}";
            }
        }
        
        
        response.status(401); 
        return "{\"logged\": false}";
    }

    
    public Object logout(Request request, Response response) {
        response.type("application/json");
        
      
        request.session().invalidate();
        
        response.status(200);
        return "{\"success\": true}";
    }
    
    
    public Object get(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        Usuario usuario = usuarioDAO.get(id);

        if (usuario != null) {
            response.status(200);
            return "Usuário: " + usuario.getNome() + " | Streak: " + usuario.getStreakDays() + " dias";
        } else {
            response.status(404); 
            return "Usuário não encontrado.";
        }
    }
    
}