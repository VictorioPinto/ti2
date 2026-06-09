package dao;
import model.ForumTopico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumDAO extends DAO {
    public ForumDAO() { super(); conectar(); }

    public boolean insert(ForumTopico topico) {
        boolean status = false;
        try {
            String sql = "INSERT INTO forum_topicos (usuario_id, titulo, conteudo, imagem_url, data_criacao) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, topico.getUsuarioId());
            st.setString(2, topico.getTitulo());
            st.setString(3, topico.getConteudo());
            st.setString(4, topico.getImagemUrl());
            st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }

    public List<ForumTopico> getAll() {
        List<ForumTopico> topicos = new ArrayList<>();
        try {
            String sql = "SELECT t.*, (SELECT COUNT(*) FROM forum_comentarios c WHERE c.topico_id = t.id) AS qtd_comentarios FROM forum_topicos t ORDER BY t.data_criacao DESC";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                topicos.add(new ForumTopico(rs.getInt("id"), rs.getInt("usuario_id"), rs.getString("titulo"), rs.getString("conteudo"), rs.getString("imagem_url"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getInt("qtd_comentarios"), rs.getTimestamp("data_criacao")));
            }
            st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return topicos;
    }

    public ForumTopico getById(int id) {
        ForumTopico topico = null;
        try {
            String sql = "SELECT t.*, (SELECT COUNT(*) FROM forum_comentarios c WHERE c.topico_id = t.id) AS qtd_comentarios FROM forum_topicos t WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                topico = new ForumTopico(rs.getInt("id"), rs.getInt("usuario_id"), rs.getString("titulo"), rs.getString("conteudo"), rs.getString("imagem_url"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getInt("qtd_comentarios"), rs.getTimestamp("data_criacao"));
            }
            st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return topico;
    }

    public boolean interagir(int id, String tipo) {
        boolean status = false;
        try {
            String coluna = tipo.equals("like") ? "likes" : "dislikes";
            String sql = "UPDATE forum_topicos SET " + coluna + " = " + coluna + " + 1 WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }
}