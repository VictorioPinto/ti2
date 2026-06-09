package dao;
import model.ForumComentario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO extends DAO {
    public ComentarioDAO() { super(); conectar(); }

    public boolean insert(ForumComentario c) {
        boolean status = false;
        try {
            String sql = "INSERT INTO forum_comentarios (topico_id, usuario_id, conteudo, data_criacao) VALUES (?, ?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, c.getTopicoId());
            st.setInt(2, c.getUsuarioId());
            st.setString(3, c.getConteudo());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }

    public List<ForumComentario> getByTopicoId(int topicoId) {
        List<ForumComentario> comentarios = new ArrayList<>();
        try {
            String sql = "SELECT * FROM forum_comentarios WHERE topico_id = ? ORDER BY data_criacao ASC";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, topicoId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                comentarios.add(new ForumComentario(rs.getInt("id"), rs.getInt("topico_id"), rs.getInt("usuario_id"), rs.getString("conteudo"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getTimestamp("data_criacao")));
            }
            st.close();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return comentarios;
    }

    public boolean interagir(int id, String tipo) {
        boolean status = false;
        try {
            String coluna = tipo.equals("like") ? "likes" : "dislikes";
            String sql = "UPDATE forum_comentarios SET " + coluna + " = " + coluna + " + 1 WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) { throw new RuntimeException(u); }
        return status;
    }
}