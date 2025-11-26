package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.Secretaria;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao extends BaseDao implements GenericDao<Usuario> {

    @Override
    public boolean inserir(Usuario usuario) throws SQLException, ValidationException {
        usuario.validar();

        String sql = "INSERT INTO usuario " +
                "(nome_login, senha, status, ultimo_login, bloqueado, tentativas_falhas, secretaria_id, perfil_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, usuario.getNomeLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getStatus());

            LocalDateTime ult = usuario.getUltimoLogin();
            if (ult != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(ult));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setBoolean(5, usuario.isBloqueado());
            stmt.setInt(6, usuario.getTentativasFalhas());

            if (usuario.getSecretaria() != null &&
                usuario.getSecretaria().getId() != null) {
                stmt.setLong(7, usuario.getSecretaria().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            if (usuario.getPerfil() != null &&
                usuario.getPerfil().getId() != null) {
                stmt.setLong(8, usuario.getPerfil().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) throws SQLException, ValidationException {
        usuario.validar();

        String sql = "UPDATE usuario SET " +
                "nome_login = ?, senha = ?, status = ?, ultimo_login = ?, " +
                "bloqueado = ?, tentativas_falhas = ?, secretaria_id = ?, perfil_id = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getNomeLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getStatus());

            LocalDateTime ult = usuario.getUltimoLogin();
            if (ult != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(ult));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setBoolean(5, usuario.isBloqueado());
            stmt.setInt(6, usuario.getTentativasFalhas());

            if (usuario.getSecretaria() != null &&
                usuario.getSecretaria().getId() != null) {
                stmt.setLong(7, usuario.getSecretaria().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            if (usuario.getPerfil() != null &&
                usuario.getPerfil().getId() != null) {
                stmt.setLong(8, usuario.getPerfil().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            stmt.setLong(9, usuario.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public Usuario buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome_login, senha, status, ultimo_login, bloqueado, " +
                     "tentativas_falhas, secretaria_id, perfil_id " +
                     "FROM usuario WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT id, nome_login, senha, status, ultimo_login, bloqueado, " +
                     "tentativas_falhas, secretaria_id, perfil_id FROM usuario";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Usuario> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNomeLogin(rs.getString("nome_login"));
        u.setSenha(rs.getString("senha"));
        u.setStatus(rs.getString("status"));

        Timestamp ts = rs.getTimestamp("ultimo_login");
        if (ts != null) {
            u.setUltimoLogin(ts.toLocalDateTime());
        }

        u.setBloqueado(rs.getBoolean("bloqueado"));
        u.setTentativasFalhas(rs.getInt("tentativas_falhas"));

        Long secId = rs.getLong("secretaria_id");
        if (!rs.wasNull()) {
            Secretaria s = new Secretaria();
            s.setId(secId);
            u.setSecretaria(s);
        }

        Long perfilId = rs.getLong("perfil_id");
        if (!rs.wasNull()) {
            Perfil p = new Perfil();
            p.setId(perfilId);
            u.setPerfil(p);
        }

        return u;
    }

    public boolean salvar(Usuario usuario) throws SQLException, ValidationException {
        if (usuario.getId() == null) {
            return inserir(usuario);
        }
        return atualizar(usuario);
    }
}
