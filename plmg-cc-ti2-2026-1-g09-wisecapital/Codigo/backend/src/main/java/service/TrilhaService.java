package service;

import dao.TrilhaDAO;
import model.NivelTrilha;
import spark.Request;
import spark.Response;
import java.util.List;

public class TrilhaService {
    private TrilhaDAO trilhaDAO = new TrilhaDAO();

    // Retorna a lista de níveis (pode usar Gson para converter para JSON)
    public Object listarNiveis(Request request, Response response) {
        List<NivelTrilha> niveis = trilhaDAO.getAllNiveis();
        response.type("application/json");
        // Aqui você pode usar a biblioteca Gson para retornar JSON real
        return niveis.toString(); 
    }

    // Lógica para avançar o utilizador de nível
    public Object avancarNivel(Request request, Response response) {
        int usuarioId = Integer.parseInt(request.queryParams("usuarioId"));
        int nivelCompletado = Integer.parseInt(request.queryParams("nivelId"));
        
        int proximoNivel = nivelCompletado + 1;
        
        if (trilhaDAO.atualizarNivelUsuario(usuarioId, proximoNivel)) {
            response.status(200);
            return "Parabéns! Você avançou para o nível " + proximoNivel;
        } else {
            response.status(500);
            return "Erro ao atualizar progresso.";
        }
    }
}