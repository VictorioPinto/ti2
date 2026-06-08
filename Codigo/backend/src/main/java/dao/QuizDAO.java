package dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Quiz; // Importante: Garanta que esta importação existe
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends DAO {
    public QuizDAO() {
        super();
        conectar();
    }

    // --- MÉTODO QUE ESTAVA FALTANDO ---
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        try {
            String sql = "SELECT * FROM quizzes ORDER BY nivel_id ASC";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                quizzes.add(new Quiz(rs.getInt("id"), rs.getInt("nivel_id"), rs.getString("titulo")));
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao buscar quizzes: " + e.getMessage());
        }
        return quizzes;
    }

    public boolean cadastrarQuizCompleto(JsonObject dados) {
        try {
            conexao.setAutoCommit(false); 
            
            String sqlQuiz = "INSERT INTO quizzes (nivel_id, titulo) VALUES (?, ?) RETURNING id";
            PreparedStatement stQuiz = conexao.prepareStatement(sqlQuiz);
            stQuiz.setInt(1, dados.get("nivel_id").getAsInt());
            stQuiz.setString(2, dados.get("titulo").getAsString());
            ResultSet rsQuiz = stQuiz.executeQuery();
            rsQuiz.next();
            int quizId = rsQuiz.getInt(1);

            
            JsonArray perguntas = dados.getAsJsonArray("perguntas");
            for (JsonElement pEl : perguntas) {
                JsonObject p = pEl.getAsJsonObject();
                String sqlP = "INSERT INTO perguntas (quiz_id, pergunta, tipo, explicacao) VALUES (?, ?, ?, ?) RETURNING id";
                PreparedStatement stP = conexao.prepareStatement(sqlP);
                stP.setInt(1, quizId);
                stP.setString(2, p.get("texto").getAsString());
                stP.setString(3, p.get("tipo").getAsString());
                stP.setString(4, p.get("explicacao").getAsString());
                ResultSet rsP = stP.executeQuery();
                rsP.next();
                int perguntaId = rsP.getInt(1);

                
                if (p.get("tipo").getAsString().equals("FECHADA")) {
                    JsonArray opcoes = p.getAsJsonArray("opcoes");
                    int idOpcaoCorreta = -1;
                    for (JsonElement oEl : opcoes) {
                        JsonObject o = oEl.getAsJsonObject();
                        String sqlO = "INSERT INTO opcoes_pergunta (pergunta_id, texto) VALUES (?, ?) RETURNING id";
                        PreparedStatement stO = conexao.prepareStatement(sqlO);
                        stO.setInt(1, perguntaId);
                        stO.setString(2, o.get("texto").getAsString());
                        ResultSet rsO = stO.executeQuery();
                        rsO.next();
                        int opcaoId = rsO.getInt(1);
                        
                        if (o.get("correta").getAsBoolean()) idOpcaoCorreta = opcaoId;
                    }
                    
                    String sqlUpdateP = "UPDATE perguntas SET correta = ? WHERE id = ?";
                    PreparedStatement stUp = conexao.prepareStatement(sqlUpdateP);
                    stUp.setInt(1, idOpcaoCorreta);
                    stUp.setInt(2, perguntaId);
                    stUp.executeUpdate();
                }
            }

            conexao.commit();
            return true;
        } catch (Exception e) {
            try { conexao.rollback(); } catch (SQLException ex) {}
            System.err.println("Erro ao cadastrar quiz: " + e.getMessage());
            return false;
        }
    }

    public JsonArray getPerguntasDoQuiz(int quizId) {
        JsonArray perguntasArray = new JsonArray();
        try {
            String sql = "SELECT * FROM perguntas WHERE quiz_id = ? ORDER BY id";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, quizId);
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                JsonObject perguntaJson = new JsonObject();
                int perguntaId = rs.getInt("id");
                String tipo = rs.getString("tipo");
                
                perguntaJson.addProperty("id", perguntaId);
                perguntaJson.addProperty("pergunta", rs.getString("pergunta"));
                perguntaJson.addProperty("tipo", tipo);
                perguntaJson.addProperty("explicacao", rs.getString("explicacao"));
                
                if ("FECHADA".equals(tipo)) {
                    perguntaJson.addProperty("correta", rs.getInt("correta"));
                    JsonArray opcoesArray = new JsonArray();
                    String sqlOpcoes = "SELECT * FROM opcoes_pergunta WHERE pergunta_id = ?";
                    PreparedStatement stOp = conexao.prepareStatement(sqlOpcoes);
                    stOp.setInt(1, perguntaId);
                    ResultSet rsOp = stOp.executeQuery();
                    
                    while (rsOp.next()) {
                        JsonObject opcao = new JsonObject();
                        opcao.addProperty("id", rsOp.getInt("id"));
                        opcao.addProperty("texto", rsOp.getString("texto"));
                        opcoesArray.add(opcao);
                    }
                    stOp.close();
                    perguntaJson.add("opcoes", opcoesArray);
                }
                perguntasArray.add(perguntaJson);
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao buscar perguntas: " + e.getMessage());
        }
        return perguntasArray;
    }
}