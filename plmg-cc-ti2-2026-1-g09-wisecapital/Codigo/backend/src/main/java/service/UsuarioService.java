package service;

import dao.UsuarioDAO;
import model.Usuario;
import spark.Request;
import spark.Response;

public class UsuarioService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Realiza o registro (inserção) de um novo usuário no sistema.
     */
    public Object insert(Request request, Response response) {
        String login = request.queryParams("login");
        String senha = request.queryParams("senha");
        String nome = request.queryParams("nome");
        String email = request.queryParams("email");

        // Inicializamos com streak 0 e nível 1 (padrão para novos usuários)
        Usuario usuario = new Usuario(-1, login, senha, nome, email, 0, 1);

        if (usuarioDAO.insert(usuario)) {
            response.status(201); // 201 Created
            return "Usuário " + nome + " registrado com sucesso!";
        } else {
            response.status(500); // Internal Server Error
            return "Erro ao registrar usuário.";
        }
    }

    /**
     * Realiza a autenticação (login) do usuário.
     */
    public Object login(Request request, Response response) {
        String login = request.queryParams("login");
        String senha = request.queryParams("senha");

        // Chama o método de autenticação que criamos no UsuarioDAO
        Usuario usuario = usuarioDAO.autenticar(login, senha);

        if (usuario != null) {
            response.status(200); // OK
            // Retorna os dados básicos para o frontend (poderia ser um JSON)
            return "Login bem-sucedido! Bem-vindo, " + usuario.getNome();
        } else {
            response.status(401); // 401 Unauthorized
            return "Usuário ou senha inválidos.";
        }
    }

    /**
     * Método para buscar um usuário específico (ex: para exibir no perfil).
     */
    public Object get(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        Usuario usuario = usuarioDAO.get(id);

        if (usuario != null) {
            response.status(200);
            return "Usuário: " + usuario.getNome() + " | Streak: " + usuario.getStreakDays() + " dias";
        } else {
            response.status(404); // Not Found
            return "Usuário não encontrado.";
        }
    }
}