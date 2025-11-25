package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDao extends BaseDao implements GenericDao<Especialidade> {

    @Override
    public void inserir(Especialidade especialidade) throws SQLException, ValidationException {
        especialidade.validar();

        String sql = "INSERT INTO especialidade (nome, area, descricao) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, especialidade.getNome());
            stmt.setString(2, especialidade.getArea());
            stmt.setString(3, especialidade.getDescricao());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                especialidade.setId(rs.getLong(1));
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Especialidade especialidade) throws SQLException, ValidationException {
        especialidade.validar();

        String sql = "UPDATE especialidade SET nome = ?, area = ?, descricao = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, especialidade.getNome());
            stmt.setString(2, especialidade.getArea());
            stmt.setString(3, especialidade.getDescricao());
            stmt.setLong(4, especialidade.getId());

            stmt.executeUpdate();
        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM especialidade WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public Especialidade buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, area, descricao FROM especialidade WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Especialidade e = new Especialidade();
                e.setId(rs.getLong("id"));
                e.setNome(rs.getString("nome"));
                e.setArea(rs.getString("area"));
                e.setDescricao(rs.getString("descricao"));
                return e;
            }
            return null;
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Especialidade> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, area, descricao FROM especialidade";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Especialidade> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Especialidade e = new Especialidade();
                e.setId(rs.getLong("id"));
                e.setNome(rs.getString("nome"));
                e.setArea(rs.getString("area"));
                e.setDescricao(rs.getString("descricao"));
                lista.add(e);
            }
            return lista;
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }
}
