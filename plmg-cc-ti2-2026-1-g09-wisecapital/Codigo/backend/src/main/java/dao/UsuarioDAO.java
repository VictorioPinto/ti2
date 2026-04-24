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
            String sql = "INSERT INTO usuarios (login, senha, nome, email, streak_days, nivel_atual_id, data_ultimo_acesso) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, usuario.getLogin());
            st.setString(2, usuario.getSenha());
            st.setString(3, usuario.getNome());
            st.setString(4, usuario.getEmail());
            st.setInt(5, usuario.getStreakDays());
            st.setInt(6, usuario.getNivelAtualId());
            st.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            
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
                    rs.getInt("nivel_atual_id")
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
                    rs.getInt("nivel_atual_id")
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
            String sql = "UPDATE usuarios SET login = ?, senha = ?, nome = ?, email = ?, streak_days = ?, nivel_atual_id = ?, data_ultimo_acesso = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, usuario.getLogin());
            st.setString(2, usuario.getSenha());
            st.setString(3, usuario.getNome());
            st.setString(4, usuario.getEmail());
            st.setInt(5, usuario.getStreakDays());
            st.setInt(6, usuario.getNivelAtualId());
            st.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            st.setInt(8, usuario.getId());
            
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
    
    // Método extra para autenticação no login
    public Usuario autenticar(String login, String senha) {
        Usuario usuario = null;
        try {
            String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, login);
            st.setString(2, senha);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getInt("streak_days"),
                    rs.getInt("nivel_atual_id")
                );
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return usuario;
    }
}