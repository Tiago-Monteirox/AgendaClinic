package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDao extends BaseDao implements GenericDao<Consulta> {

    // ============================================================
    // CRUD
    // ============================================================

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

    public boolean salvar(Consulta consulta) throws SQLException, ValidationException {
        if (consulta.getId() == null) {
            return inserir(consulta);
        }
        return atualizar(consulta);
    }

    // ============================================================
    // Mapeamentos
    // ============================================================

    // mapeia só com IDs (uso interno)
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

    // mapeia já com nomes (para telas)
    private Consulta mapearConsultaComJoins(ResultSet rs) throws SQLException {
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

        // Paciente
        Paciente p = new Paciente();
        p.setId(rs.getLong("paciente_id"));
        p.setNome(rs.getString("paciente_nome"));
        c.setPaciente(p);

        // Médico
        Medico m = new Medico();
        m.setId(rs.getLong("medico_id"));
        m.setNome(rs.getString("medico_nome"));
        c.setMedico(m);

        // Convênio
        Long convId = rs.getLong("convenio_id");
        if (!rs.wasNull()) {
            Convenio conv = new Convenio();
            conv.setId(convId);
            conv.setNome(rs.getString("convenio_nome"));
            c.setConvenio(conv);
        }

        return c;
    }

    // ============================================================
    // Regras de negócio / consultas específicas
    // ============================================================

    public boolean existeConflitoHorario(Long medicoId, LocalDateTime dataHora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM consulta " +
                     "WHERE medico_id = ? AND data_hora = ? AND status <> 'CANCELADA'";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, medicoId);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora));
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    /** Visão de UM dia, opcionalmente filtrando por médico (secretária e médico) */
    public List<Consulta> listarPorDataEMedico(LocalDate data, Long medicoId) throws SQLException {
        StringBuilder sb = new StringBuilder(
            "SELECT c.id, c.data_hora, c.retorno, c.carteira_convenio, c.observacao, c.status, " +
            "       p.id AS paciente_id, p.nome AS paciente_nome, " +
            "       m.id AS medico_id,   m.nome AS medico_nome, " +
            "       conv.id AS convenio_id, conv.nome AS convenio_nome " +
            "FROM consulta c " +
            "JOIN paciente p ON p.id = c.paciente_id " +
            "JOIN medico   m ON m.id = c.medico_id " +
            "LEFT JOIN convenio conv ON conv.id = c.convenio_id " +
            "WHERE DATE(c.data_hora) = ?"
        );

        if (medicoId != null) {
            sb.append(" AND c.medico_id = ?");
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Consulta> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sb.toString());
            stmt.setDate(1, Date.valueOf(data));
            if (medicoId != null) {
                stmt.setLong(2, medicoId);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearConsultaComJoins(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    /** Todas as consultas AGENDADAS de um médico (sem filtro de data) */
    public List<Consulta> listarAgendadasPorMedico(Long medicoId) throws SQLException {
        String sql =
            "SELECT c.id, c.data_hora, c.retorno, c.carteira_convenio, c.observacao, c.status, " +
            "       p.id AS paciente_id, p.nome AS paciente_nome, " +
            "       m.id AS medico_id,   m.nome AS medico_nome, " +
            "       conv.id AS convenio_id, conv.nome AS convenio_nome " +
            "FROM consulta c " +
            "JOIN paciente p ON p.id = c.paciente_id " +
            "JOIN medico   m ON m.id = c.medico_id " +
            "LEFT JOIN convenio conv ON conv.id = c.convenio_id " +
            "WHERE c.medico_id = ? " +
            "  AND c.status = 'AGENDADA' " +
            "ORDER BY c.data_hora";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Consulta> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, medicoId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearConsultaComJoins(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    /** Opcional: todas as consultas a partir de uma data, opcional por médico */
    public List<Consulta> listarAPartirDeDataPorMedico(LocalDate dataInicial, Long medicoId)
            throws SQLException {

        StringBuilder sb = new StringBuilder(
            "SELECT c.id, c.data_hora, c.retorno, c.carteira_convenio, c.observacao, c.status, " +
            "       p.id AS paciente_id, p.nome AS paciente_nome, " +
            "       m.id AS medico_id,   m.nome AS medico_nome, " +
            "       conv.id AS convenio_id, conv.nome AS convenio_nome " +
            "FROM consulta c " +
            "JOIN paciente p ON p.id = c.paciente_id " +
            "JOIN medico   m ON m.id = c.medico_id " +
            "LEFT JOIN convenio conv ON conv.id = c.convenio_id " +
            "WHERE DATE(c.data_hora) >= ?"
        );

        if (medicoId != null) {
            sb.append(" AND c.medico_id = ?");
        }

        sb.append(" ORDER BY c.data_hora");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Consulta> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sb.toString());
            stmt.setDate(1, Date.valueOf(dataInicial));
            if (medicoId != null) {
                stmt.setLong(2, medicoId);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearConsultaComJoins(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    // ============================================================
    // Checks auxiliares
    // ============================================================

    public boolean existeConsultaParaMedico(Long medicoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM consulta WHERE medico_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, medicoId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public boolean existeConsultaParaConvenio(Long convenioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM consulta WHERE convenio_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, convenioId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public boolean existeConsultaPorMedico(Long medicoId) throws SQLException {
        String sql = "SELECT 1 FROM consulta WHERE medico_id = ? LIMIT 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, medicoId);
            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public boolean existeConsultaPorPaciente(Long pacienteId) throws SQLException {
        String sql = "SELECT 1 FROM consulta WHERE paciente_id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
