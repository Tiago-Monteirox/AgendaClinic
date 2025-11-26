package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.*;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDao extends BaseDao implements GenericDao<Consulta> {

    @Override
    public boolean inserir(Consulta consulta) throws SQLException, ValidationException {
        consulta.validar();

        String sql = "INSERT INTO consulta " +
                "(data_hora, retorno, carteira_convenio, observacao, status, " +
                "paciente_id, medico_id, convenio_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setBoolean(2, consulta.isRetorno());
            stmt.setString(3, consulta.getCarteiraConvenio());
            stmt.setString(4, consulta.getObservacao());
            stmt.setString(5, consulta.getStatus());
            stmt.setLong(6, consulta.getPaciente().getId());
            stmt.setLong(7, consulta.getMedico().getId());

            if (consulta.getConvenio() != null &&
                consulta.getConvenio().getId() != null) {
                stmt.setLong(8, consulta.getConvenio().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                consulta.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Consulta consulta) throws SQLException, ValidationException {
        consulta.validar();

        String sql = "UPDATE consulta SET " +
                "data_hora = ?, retorno = ?, carteira_convenio = ?, observacao = ?, status = ?, " +
                "paciente_id = ?, medico_id = ?, convenio_id = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setBoolean(2, consulta.isRetorno());
            stmt.setString(3, consulta.getCarteiraConvenio());
            stmt.setString(4, consulta.getObservacao());
            stmt.setString(5, consulta.getStatus());
            stmt.setLong(6, consulta.getPaciente().getId());
            stmt.setLong(7, consulta.getMedico().getId());

            if (consulta.getConvenio() != null &&
                consulta.getConvenio().getId() != null) {
                stmt.setLong(8, consulta.getConvenio().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            stmt.setLong(9, consulta.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM consulta WHERE id = ?";

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
    public Consulta buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, data_hora, retorno, carteira_convenio, observacao, status, " +
                     "paciente_id, medico_id, convenio_id " +
                     "FROM consulta WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearConsulta(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Consulta> listarTodos() throws SQLException {
        String sql = "SELECT id, data_hora, retorno, carteira_convenio, observacao, status, " +
                     "paciente_id, medico_id, convenio_id FROM consulta";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Consulta> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearConsulta(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Consulta mapearConsulta(ResultSet rs) throws SQLException {
        Consulta c = new Consulta();
        c.setId(rs.getLong("id"));

        Timestamp ts = rs.getTimestamp("data_hora");
        if (ts != null) {
            c.setDataHora(ts.toLocalDateTime());
        }

        c.setRetorno(rs.getBoolean("retorno"));
        c.setCarteiraConvenio(rs.getString("carteira_convenio"));
        c.setObservacao(rs.getString("observacao"));
        c.setStatus(rs.getString("status"));

        Paciente p = new Paciente();
        p.setId(rs.getLong("paciente_id"));
        c.setPaciente(p);

        Medico m = new Medico();
        m.setId(rs.getLong("medico_id"));
        c.setMedico(m);

        Long convId = rs.getLong("convenio_id");
        if (!rs.wasNull()) {
            Convenio conv = new Convenio();
            conv.setId(convId);
            c.setConvenio(conv);
        }

        return c;
    }

    public boolean salvar(Consulta consulta) throws SQLException, ValidationException {
        if (consulta.getId() == null) {
            return inserir(consulta);
        }
        return atualizar(consulta);
    }
}
