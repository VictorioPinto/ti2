package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.ForumTopico;

public class ForumDAO extends DAO {
    public ForumDAO() {
        super();
        conectar();
    }

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public List<ForumTopico> getAll() {
        List<ForumTopico> topicos = new ArrayList<>();
        try {
            String sql = "SELECT * FROM forum_topicos ORDER BY data_criacao DESC";
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
            	topicos.add(new ForumTopico(
            		    rs.getInt("id"),
            		    rs.getInt("usuario_id"),
            		    rs.getString("titulo"),
            		    rs.getString("conteudo"),
            		    rs.getString("imagem_url"), // Pegando a imagem do banco
            		    rs.getTimestamp("data_criacao")
            		));
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return topicos;
    }
}