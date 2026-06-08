package service;

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

        ForumTopico topico = new ForumTopico(-1, usuarioId, titulo, conteudo, null);

        if (forumDAO.insert(topico)) {
            response.status(201);
            return "Tópico criado com sucesso!";
        } else {
            response.status(500);
            return "Erro ao criar tópico.";
        }
    }

    public Object listarTodos(Request request, Response response) {
        List<ForumTopico> topicos = forumDAO.getAll();
        response.type("application/json");
        // Recomenda-se o uso de Gson para converter a lista em JSON real
        return topicos.toString();
    }
}
