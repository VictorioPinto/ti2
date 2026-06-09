package service;
import com.google.gson.Gson; 
import dao.ForumDAO;
import model.ForumTopico;
import spark.Request;
import spark.Response;
import java.util.List;

public class ForumService {
    private ForumDAO forumDAO = new ForumDAO();


    public Object insert(Request request, Response response) {
        int usuarioId = Integer.parseInt(request.queryParams("usuarioId"));
        String titulo = request.queryParams("titulo");
        String conteudo = request.queryParams("conteudo");
        String imagemUrl = request.queryParams("imagemUrl"); // Pegando a imagem

        ForumTopico topico = new ForumTopico(-1, usuarioId, titulo, conteudo, imagemUrl, null);

        if (forumDAO.insert(topico)) {
            response.status(201);
            return "{\"success\": true}";
        } else {
            response.status(500);
            return "{\"success\": false}";
        }
    }

    public Object listarTodos(Request request, Response response) {
        List<ForumTopico> topicos = forumDAO.getAll();
        response.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(topicos); // Agora retorna um JSON de verdade!
    }
}
