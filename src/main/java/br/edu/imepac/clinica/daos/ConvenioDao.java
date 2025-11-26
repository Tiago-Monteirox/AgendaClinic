package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConvenioDao extends BaseDao implements GenericDao<Convenio> {

    @Override
    public boolean inserir(Convenio convenio) throws SQLException, ValidationException {
        convenio.validar();

        String sql = "INSERT INTO convenio (nome, codigo, descricao, ativo) " +
                     "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, convenio.getNome());
            stmt.setString(2, convenio.getCodigo());
            stmt.setString(3, convenio.getDescricao());
            stmt.setBoolean(4, convenio.isAtivo());

            int linhas = stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                convenio.setId(rs.getLong(1));
            }

            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Convenio convenio) throws SQLException, ValidationException {
        convenio.validar();

        String sql = "UPDATE convenio SET nome = ?, codigo = ?, descricao = ?, ativo = ? " +
                     "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, convenio.getNome());
            stmt.setString(2, convenio.getCodigo());
            stmt.setString(3, convenio.getDescricao());
            stmt.setBoolean(4, convenio.isAtivo());
            stmt.setLong(5, convenio.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM convenio WHERE id = ?";

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
    public Convenio buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, codigo, descricao, ativo " +
                     "FROM convenio WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Convenio c = new Convenio();
                c.setId(rs.getLong("id"));
                c.setNome(rs.getString("nome"));
                c.setCodigo(rs.getString("codigo"));
                c.setDescricao(rs.getString("descricao"));
                c.setAtivo(rs.getBoolean("ativo"));
                return c;
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Convenio> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, codigo, descricao, ativo FROM convenio";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Convenio> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Convenio c = new Convenio();
                c.setId(rs.getLong("id"));
                c.setNome(rs.getString("nome"));
                c.setCodigo(rs.getString("codigo"));
                c.setDescricao(rs.getString("descricao"));
                c.setAtivo(rs.getBoolean("ativo"));
                lista.add(c);
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    // opcional, se quiser manter padrão salvar()
    public boolean salvar(Convenio convenio) throws SQLException, ValidationException {
        if (convenio.getId() == null) {
            return inserir(convenio);
        }
        return atualizar(convenio);
    }
}
