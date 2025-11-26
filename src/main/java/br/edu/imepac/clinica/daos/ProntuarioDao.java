package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Prontuario;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProntuarioDao extends BaseDao implements GenericDao<Prontuario> {

    @Override
    public boolean inserir(Prontuario prontuario) throws SQLException, ValidationException {
        prontuario.validar();

        String sql = "INSERT INTO prontuario (resumo, anotacoes, arquivo_pdf, consulta_id) " +
                     "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, prontuario.getResumo());
            stmt.setString(2, prontuario.getAnotacoes());
            stmt.setString(3, prontuario.getArquivoPdf());
            stmt.setLong(4, prontuario.getConsulta().getId());

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                prontuario.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Prontuario prontuario) throws SQLException, ValidationException {
        prontuario.validar();

        String sql = "UPDATE prontuario SET resumo = ?, anotacoes = ?, arquivo_pdf = ?, " +
                     "consulta_id = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, prontuario.getResumo());
            stmt.setString(2, prontuario.getAnotacoes());
            stmt.setString(3, prontuario.getArquivoPdf());
            stmt.setLong(4, prontuario.getConsulta().getId());
            stmt.setLong(5, prontuario.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM prontuario WHERE id = ?";

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
    public Prontuario buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, resumo, anotacoes, arquivo_pdf, consulta_id " +
                     "FROM prontuario WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearProntuario(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Prontuario> listarTodos() throws SQLException {
        String sql = "SELECT id, resumo, anotacoes, arquivo_pdf, consulta_id FROM prontuario";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Prontuario> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearProntuario(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Prontuario mapearProntuario(ResultSet rs) throws SQLException {
        Prontuario p = new Prontuario();
        p.setId(rs.getLong("id"));
        p.setResumo(rs.getString("resumo"));
        p.setAnotacoes(rs.getString("anotacoes"));
        p.setArquivoPdf(rs.getString("arquivo_pdf"));

        Consulta c = new Consulta();
        c.setId(rs.getLong("consulta_id"));
        p.setConsulta(c);

        return p;
    }

    public boolean salvar(Prontuario prontuario) throws SQLException, ValidationException {
        if (prontuario.getId() == null) {
            return inserir(prontuario);
        }
        return atualizar(prontuario);
    }
}
