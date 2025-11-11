/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.interfaces.Persistente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelas operações CRUD da entidade Medico. Utiliza abordagem
 * manual de try/finally com métodos de fechamento da BaseDao.
 *
 * @author everton
 */
public class MedicoDao extends BaseDao implements Persistente<Medico> {

    @Override
    public boolean salvar(Medico entidade) {
        String sql = "INSERT INTO medicos (nome, crm, especialidade_id) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entidade.getNome());
            stmt.setString(2, entidade.getCrm());
            stmt.setLong(3, entidade.getEspecialidadeId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar medicos: " + e.getMessage());
            return false;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean atualizar(Medico entidade) {
        String sql = "UPDATE medicos SET nome = ?, crm = ?, especialidade_id = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entidade.getNome());
            stmt.setString(2, entidade.getCrm());
            stmt.setLong(3, entidade.getEspecialidadeId());
            stmt.setLong(4, entidade.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar medicos: " + e.getMessage());
            return false;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(long id) {
        String sql = "DELETE FROM medicos WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setLong(1, id);

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir medicos: " + e.getMessage());
            return false;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public Medico buscarPorId(long id) {
        String sql
                = "SELECT m.id, m.nome, m.crm, m.especialidade_id, "
                + "       e.id, e.nome, e.descricao "
                + "FROM medicos m "
                + "JOIN especialidades e ON e.id = m.especialidade_id "
                + "WHERE m.id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Medico medico = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                Especialidade especialidade = new Especialidade(rs.getLong("e.id"), rs.getString("e.nome"), rs.getString("e.descricao"));
                medico = new Medico(rs.getLong("m.id"), rs.getString("m.nome"), rs.getString("m.crm"), especialidade);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar medicos por ID: " + e.getMessage());

        } finally {
            fecharRecursos(conn, stmt, rs);
        }

        return medico;
    }

    @Override
    public List<Medico> listarTodos() {
        String sql =  "SELECT m.id, m.nome, m.crm, m.especialidade_id," +
            "       e.id, e.nome, e.descricao " +
            "FROM medicos m " +
            "JOIN especialidades e ON e.id = m.especialidade_id " +
            "ORDER BY m.nome";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<Medico> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Especialidade especialidade = new Especialidade(rs.getLong("e.id"), rs.getString("e.nome"), rs.getString("e.descricao"));
                
                Medico medicos = new Medico();
                medicos.setId(rs.getLong("id"));
                medicos.setNome(rs.getString("nome"));
                medicos.setCrm(rs.getString("crm"));
                medicos.setEspecialidade(especialidade);
                
                lista.add(medicos);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar medicoss: " + e.getMessage());

        } finally {
            fecharRecursos(conn, stmt, rs);
        }

        return lista;
    }
}
