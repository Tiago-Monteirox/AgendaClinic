package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.PerfilFuncionalidade;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilFuncionalidadeDao extends BaseDao implements GenericDao<PerfilFuncionalidade> {

    @Override
    public boolean inserir(PerfilFuncionalidade pf) throws SQLException, ValidationException {
        pf.validar();

        String sql = "INSERT INTO perfil_funcionalidade (perfil_id, funcionalidade) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, pf.getPerfil().getId());
            stmt.setString(2, pf.getFuncionalidade());

            int linhas = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pf.setId(rs.getLong(1));
            }
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean atualizar(PerfilFuncionalidade pf) throws SQLException, ValidationException {
        pf.validar();

        String sql = "UPDATE perfil_funcionalidade SET perfil_id = ?, funcionalidade = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setLong(1, pf.getPerfil().getId());
            stmt.setString(2, pf.getFuncionalidade());
            stmt.setLong(3, pf.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        String sql = "DELETE FROM perfil_funcionalidade WHERE id = ?";

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
    public PerfilFuncionalidade buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, perfil_id, funcionalidade FROM perfil_funcionalidade WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                PerfilFuncionalidade pf = new PerfilFuncionalidade();
                pf.setId(rs.getLong("id"));

                Perfil p = new Perfil();
                p.setId(rs.getLong("perfil_id"));
                pf.setPerfil(p);

                pf.setFuncionalidade(rs.getString("funcionalidade"));
                return pf;
            }
            return null;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<PerfilFuncionalidade> listarTodos() throws SQLException {
        String sql = "SELECT id, perfil_id, funcionalidade FROM perfil_funcionalidade";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<PerfilFuncionalidade> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                PerfilFuncionalidade pf = new PerfilFuncionalidade();
                pf.setId(rs.getLong("id"));

                Perfil p = new Perfil();
                p.setId(rs.getLong("perfil_id"));
                pf.setPerfil(p);

                pf.setFuncionalidade(rs.getString("funcionalidade"));
                lista.add(pf);
            }
            return lista;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public boolean salvar(PerfilFuncionalidade pf) throws SQLException, ValidationException {
        if (pf.getId() == null) {
            return inserir(pf);
        }
        return atualizar(pf);
    }
}
