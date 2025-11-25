package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDao extends BaseDao implements GenericDao<Medico> {

    private final EspecialidadeDao especialidadeDao = new EspecialidadeDao();

    @Override
    public void inserir(Medico medico) throws SQLException, ValidationException {
        medico.validar();

        String sql = "INSERT INTO medico " +
                "(nome, cpf, telefone, crm, tempo_experiencia, formacao, especialidade_id, ativo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, medico.getNome());
            stmt.setString(2, medico.getCpf());
            stmt.setString(3, medico.getTelefone());
            stmt.setString(4, medico.getCrm());
            stmt.setInt(5, medico.getTempoExperiencia());
            stmt.setString(6, medico.getFormacao());
            stmt.setLong(7, medico.getEspecialidade().getId());
            stmt.setBoolean(8, medico.isAtivo());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                medico.setId(rs.getLong(1));
            }

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Medico medico) throws SQLException, ValidationException {
        medico.validar();

        String sql = "UPDATE medico SET " +
                "nome = ?, cpf = ?, telefone = ?, crm = ?, tempo_experiencia = ?, " +
                "formacao = ?, especialidade_id = ?, ativo = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, medico.getNome());
            stmt.setString(2, medico.getCpf());
            stmt.setString(3, medico.getTelefone());
            stmt.setString(4, medico.getCrm());
            stmt.setInt(5, medico.getTempoExperiencia());
            stmt.setString(6, medico.getFormacao());
            stmt.setLong(7, medico.getEspecialidade().getId());
            stmt.setBoolean(8, medico.isAtivo());
            stmt.setLong(9, medico.getId());

            stmt.executeUpdate();

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM medico WHERE id = ?";

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
    public Medico buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, crm, tempo_experiencia, " +
                     "formacao, especialidade_id, ativo " +
                     "FROM medico WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearMedico(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Medico> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, crm, tempo_experiencia, " +
                     "formacao, especialidade_id, ativo FROM medico";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Medico> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearMedico(rs));
            }

            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Medico mapearMedico(ResultSet rs) throws SQLException {
        Medico m = new Medico();
        m.setId(rs.getLong("id"));
        m.setNome(rs.getString("nome"));
        m.setCpf(rs.getString("cpf"));
        m.setTelefone(rs.getString("telefone"));
        m.setCrm(rs.getString("crm"));
        m.setTempoExperiencia(rs.getInt("tempo_experiencia"));
        m.setFormacao(rs.getString("formacao"));
        m.setAtivo(rs.getBoolean("ativo"));

        Long especialidadeId = rs.getLong("especialidade_id");
        Especialidade esp = especialidadeDao.buscarPorId(especialidadeId);
        m.setEspecialidade(esp);

        return m;
    }
}
