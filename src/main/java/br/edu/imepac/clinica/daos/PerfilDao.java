package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilDao extends BaseDao implements GenericDao<Perfil> {

    @Override
    public boolean inserir(Perfil perfil) throws SQLException, ValidationException {
        perfil.validar();

        String sql = "INSERT INTO perfil (nome, descricao, nivel_acesso) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, perfil.getNome());
            stmt.setString(2, perfil.getDescricao());
            stmt.setInt(3, perfil.getNivelAcesso());

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                perfil.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Perfil perfil) throws SQLException, ValidationException {
        perfil.validar();

        String sql = "UPDATE perfil SET nome = ?, descricao = ?, nivel_acesso = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, perfil.getNome());
            stmt.setString(2, perfil.getDescricao());
            stmt.setInt(3, perfil.getNivelAcesso());
            stmt.setLong(4, perfil.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM perfil WHERE id = ?";

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
    public Perfil buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, descricao, nivel_acesso FROM perfil WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Perfil p = new Perfil();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setNivelAcesso(rs.getInt("nivel_acesso"));
                return p;
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Perfil> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, descricao, nivel_acesso FROM perfil";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Perfil> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Perfil p = new Perfil();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setNivelAcesso(rs.getInt("nivel_acesso"));
                lista.add(p);
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public boolean salvar(Perfil perfil) throws SQLException, ValidationException {
        if (perfil.getId() == null) {
            return inserir(perfil);
        }
        return atualizar(perfil);
    }
}
