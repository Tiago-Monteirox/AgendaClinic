package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Prontuario;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProntuarioDao extends BaseDao {

    public boolean inserir(Prontuario p) throws SQLException, ValidationException {
        p.validar();

        String sql = "INSERT INTO prontuario " +
                "(consulta_id, resumo, anotacoes, arquivo_pdf) " +
                "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, p.getConsulta().getId());
            stmt.setString(2, p.getResumo());
            stmt.setString(3, p.getAnotacoes());
            stmt.setString(4, p.getArquivoPdf());

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public boolean atualizar(Prontuario p) throws SQLException, ValidationException {
        p.validar();

        String sql = "UPDATE prontuario SET " +
                "resumo = ?, anotacoes = ?, arquivo_pdf = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, p.getResumo());
            stmt.setString(2, p.getAnotacoes());
            stmt.setString(3, p.getArquivoPdf());
            stmt.setLong(4, p.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    public Prontuario buscarPorConsultaId(Long consultaId) throws SQLException {
        String sql = "SELECT id, consulta_id, resumo, anotacoes, arquivo_pdf " +
                     "FROM prontuario WHERE consulta_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, consultaId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearProntuario(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public Prontuario buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, consulta_id, resumo, anotacoes, arquivo_pdf " +
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

    /**
     * US-13: listar prontuários por paciente.
     * Aqui já traz consulta (com data_hora) associada, para facilitar tela de histórico.
     */
    public List<Prontuario> listarPorPaciente(Long pacienteId) throws SQLException {
        String sql = "SELECT pr.id, pr.consulta_id, pr.resumo, pr.anotacoes, pr.arquivo_pdf, " +
                "       c.data_hora " +
                "FROM prontuario pr " +
                "JOIN consulta c ON c.id = pr.consulta_id " +
                "WHERE c.paciente_id = ? " +
                "ORDER BY c.data_hora DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Prontuario> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, pacienteId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Prontuario p = mapearProntuario(rs);

                // monta a consulta básica associada
                Consulta c = new Consulta();
                c.setId(rs.getLong("consulta_id"));
                Timestamp ts = rs.getTimestamp("data_hora");
                if (ts != null) {
                    c.setDataHora(ts.toLocalDateTime());
                }
                p.setConsulta(c);

                lista.add(p);
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Prontuario mapearProntuario(ResultSet rs) throws SQLException {
        Prontuario p = new Prontuario();
        p.setId(rs.getLong("id"));

        Long consultaId = rs.getLong("consulta_id");
        if (!rs.wasNull()) {
            Consulta c = new Consulta();
            c.setId(consultaId);
            p.setConsulta(c);
        }

        p.setResumo(rs.getString("resumo"));
        p.setAnotacoes(rs.getString("anotacoes"));
        p.setArquivoPdf(rs.getString("arquivo_pdf"));

        return p;
    }
}
