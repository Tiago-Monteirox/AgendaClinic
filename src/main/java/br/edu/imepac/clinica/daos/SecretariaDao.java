package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Secretaria;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecretariaDao extends BaseDao implements GenericDao<Secretaria> {

    @Override
    public boolean inserir(Secretaria secretaria) throws SQLException, ValidationException {
        secretaria.validar();

        String sql = "INSERT INTO secretaria " +
                "(nome, cpf, telefone, pis, turno, setor) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, secretaria.getNome());
            stmt.setString(2, secretaria.getCpf());
            stmt.setString(3, secretaria.getTelefone());
            stmt.setString(4, secretaria.getPis());
            stmt.setString(5, secretaria.getTurno());
            stmt.setString(6, secretaria.getSetor());

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                secretaria.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(Secretaria secretaria) throws SQLException, ValidationException {
        secretaria.validar();

        String sql = "UPDATE secretaria SET " +
                "nome = ?, cpf = ?, telefone = ?, pis = ?, turno = ?, setor = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, secretaria.getNome());
            stmt.setString(2, secretaria.getCpf());
            stmt.setString(3, secretaria.getTelefone());
            stmt.setString(4, secretaria.getPis());
            stmt.setString(5, secretaria.getTurno());
            stmt.setString(6, secretaria.getSetor());
            stmt.setLong(7, secretaria.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM secretaria WHERE id = ?";

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
    public Secretaria buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, pis, turno, setor " +
                     "FROM secretaria WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearSecretaria(rs);
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Secretaria> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, pis, turno, setor FROM secretaria";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Secretaria> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearSecretaria(rs));
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    private Secretaria mapearSecretaria(ResultSet rs) throws SQLException {
        Secretaria s = new Secretaria();
        s.setId(rs.getLong("id"));
        s.setNome(rs.getString("nome"));
        s.setCpf(rs.getString("cpf"));
        s.setTelefone(rs.getString("telefone"));
        s.setPis(rs.getString("pis"));
        s.setTurno(rs.getString("turno"));
        s.setSetor(rs.getString("setor"));
        return s;
    }

    public boolean salvar(Secretaria secretaria) throws SQLException, ValidationException {
        if (secretaria.getId() == null) {
            return inserir(secretaria);
        }
        return atualizar(secretaria);
    }
}
