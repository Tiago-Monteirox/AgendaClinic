package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDao extends BaseDao implements GenericDao<Paciente> {

    @Override
    public boolean inserir(Paciente paciente) throws SQLException, ValidationException {
        paciente.validar();

        String sql = "INSERT INTO paciente " +
                "(nome, cpf, telefone, plano_saude, tipo_sanguineo, data_nascimento, sexo, convenio_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setString(3, paciente.getTelefone());
            stmt.setString(4, paciente.getPlanoSaude());
            stmt.setString(5, paciente.getTipoSanguineo());

            LocalDate dataNasc = paciente.getDataNascimento();
            if (dataNasc != null) {
                stmt.setDate(6, Date.valueOf(dataNasc));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, paciente.getSexo());

            if (paciente.getConvenio() != null &&
                paciente.getConvenio().getId() != null) {
                stmt.setLong(8, paciente.getConvenio().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                paciente.setId(rs.getLong(1));
            }

            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Paciente paciente) throws SQLException, ValidationException {
        paciente.validar();

        String sql = "UPDATE paciente SET " +
                "nome = ?, cpf = ?, telefone = ?, plano_saude = ?, tipo_sanguineo = ?, " +
                "data_nascimento = ?, sexo = ?, convenio_id = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setString(3, paciente.getTelefone());
            stmt.setString(4, paciente.getPlanoSaude());
            stmt.setString(5, paciente.getTipoSanguineo());

            LocalDate dataNasc = paciente.getDataNascimento();
            if (dataNasc != null) {
                stmt.setDate(6, Date.valueOf(dataNasc));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, paciente.getSexo());

            if (paciente.getConvenio() != null &&
                paciente.getConvenio().getId() != null) {
                stmt.setLong(8, paciente.getConvenio().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            stmt.setLong(9, paciente.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM paciente WHERE id = ?";

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
    public Paciente buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, plano_saude, tipo_sanguineo, " +
                     "data_nascimento, sexo, convenio_id " +
                     "FROM paciente WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPaciente(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Paciente> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, plano_saude, tipo_sanguineo, " +
                     "data_nascimento, sexo, convenio_id FROM paciente";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Paciente> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearPaciente(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setCpf(rs.getString("cpf"));
        p.setTelefone(rs.getString("telefone"));
        p.setPlanoSaude(rs.getString("plano_saude"));
        p.setTipoSanguineo(rs.getString("tipo_sanguineo"));

        Date dataNasc = rs.getDate("data_nascimento");
        if (dataNasc != null) {
            p.setDataNascimento(dataNasc.toLocalDate());
        }

        p.setSexo(rs.getString("sexo"));

        Long convenioId = rs.getLong("convenio_id");
        if (!rs.wasNull()) {
            Convenio c = new Convenio();
            c.setId(convenioId);
            p.setConvenio(c);
        }

        return p;
    }

    // atalho se quiser padrão "salvar"
    public boolean salvar(Paciente paciente) throws SQLException, ValidationException {
        if (paciente.getId() == null) {
            return inserir(paciente);
        }
        return atualizar(paciente);
    }
    
 
   public List<Paciente> buscarPorNome(String nome) throws SQLException {
    String sql = "SELECT * FROM paciente WHERE nome LIKE ?";
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    List<Paciente> lista = new ArrayList<>();

    try {
        conn = getConnection();
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + nome + "%");
        rs = stmt.executeQuery();

        while (rs.next()) {
            lista.add(mapearPaciente(rs));
        }
        return lista;

    } finally {
        fecharRecursos(conn, stmt, rs);
    }
}

public Paciente buscarPorCpf(String cpf) throws SQLException {
    String sql = "SELECT * FROM paciente WHERE cpf = ?";
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = getConnection();
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, cpf);
        rs = stmt.executeQuery();

        if (rs.next()) {
            return mapearPaciente(rs);
        }
        return null;

    } finally {
        fecharRecursos(conn, stmt, rs);
    }
}

}
