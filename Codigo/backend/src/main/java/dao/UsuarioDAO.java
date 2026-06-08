package dao;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends DAO {

    public UsuarioDAO() {
        super();
        conectar();
    }

    public void finalize() {
        close();
    }

    public boolean insert(Usuario usuario) {
        boolean status = false;
        try {
            String sql = "INSERT INTO usuarios (login, senha, nome, email, streak_days, nivel_atual_id, adm) VALUES (?, ?, ?, ?, ?, ?, ?);";
            
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, usuario.getLogin());
            st.setString(2, usuario.getSenha());
            st.setString(3, usuario.getNome());
            st.setString(4, usuario.getEmail());
            st.setInt(5, usuario.getStreakDays());
            st.setInt(6, usuario.getNivelAtualId());
            st.setBoolean(7, usuario.isAdm()); 
            
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) {  
            throw new RuntimeException(u);
        }
        return status;
    }

    public Usuario get(int id) {
        Usuario usuario = null;
        try {
            String sql = "SELECT * FROM usuarios WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getInt("streak_days"),
                    rs.getInt("nivel_atual_id"),
                    rs.getBoolean("adm")
                );
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return usuario;
    }

    public List<Usuario> get() {
        return get("");
    }

    public List<Usuario> getOrderByID() {
        return get("id");
    }

    public List<Usuario> getOrderByLogin() {
        return get("login");
    }

    public List<Usuario> getOrderByStreak() {
        return get("streak_days DESC");
    }

    private List<Usuario> get(String orderBy) {
        List<Usuario> usuarios = new ArrayList<Usuario>();
        try {
            String sql = "SELECT * FROM usuarios" + ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getInt("streak_days"),
                    rs.getInt("nivel_atual_id"),
                    rs.getBoolean("adm") // Puxando o ADM do banco
                );
                usuarios.add(u);
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return usuarios;
    }

    public boolean update(Usuario usuario) {
        boolean status = false;
        try {
            // Atualizado para incluir o ADM no update
            String sql = "UPDATE usuarios SET login = ?, senha = ?, nome = ?, email = ?, streak_days = ?, nivel_atual_id = ?, data_ultimo_acesso = ?, adm = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, usuario.getLogin());
            st.setString(2, usuario.getSenha());
            st.setString(3, usuario.getNome());
            st.setString(4, usuario.getEmail());
            st.setInt(5, usuario.getStreakDays());
            st.setInt(6, usuario.getNivelAtualId());
            st.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            st.setBoolean(8, usuario.isAdm()); // Atualizando o ADM
            st.setInt(9, usuario.getId());
            
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
        return status;
    }

    public boolean delete(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM usuarios WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
        return status;
    }
    
    
    public Usuario getByLogin(String login) {
        Usuario usuario = null;
        try {
            String sql = "SELECT * FROM usuarios WHERE login = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getInt("streak_days"),
                    rs.getInt("nivel_atual_id"),
                    rs.getBoolean("adm") // Puxando o ADM do banco
                );
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return usuario;
    }

    public boolean jaRespondeuQuestionario(int usuarioId) {
        boolean respondeu = false;
        try {
            String sql = "SELECT COUNT(*) FROM questionario_diagnostico WHERE usuario_id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, usuarioId);
            ResultSet rs = st.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                respondeu = true; 
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao verificar questionário: " + e.getMessage());
        }
        return respondeu;
    }

    public boolean salvarQuestionario(int usuarioId, String respostasJson, int nivelSugerido) {
        boolean status = false;
        try {
            
            String sql = "INSERT INTO questionario_diagnostico (usuario_id, respostas_json, nivel_sugerido_ia) VALUES (?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, usuarioId);
            st.setString(2, respostasJson);
            st.setInt(3, nivelSugerido);
            st.executeUpdate();
            st.close();
            
            
            String sqlUpdate = "UPDATE usuarios SET nivel_atual_id = ? WHERE id = ?";
            PreparedStatement stUpdate = conexao.prepareStatement(sqlUpdate);
            stUpdate.setInt(1, nivelSugerido);
            stUpdate.setInt(2, usuarioId);
            stUpdate.executeUpdate();
            stUpdate.close();
            
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar questionário: " + e.getMessage());
        }
        return status;
    }
}