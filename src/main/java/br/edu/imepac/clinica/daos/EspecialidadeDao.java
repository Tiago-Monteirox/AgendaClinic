/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.interfaces.Persistente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelas operações CRUD da entidade Especialidade.
 * Utiliza abordagem manual de try/finally com métodos de fechamento da BaseDao.
 *
 * @author everton
 */
public class EspecialidadeDao extends BaseDao implements Persistente<Especialidade> {

    @Override
    public boolean salvar(Especialidade entidade) {
        String sql = "INSERT INTO especialidade (nome, descricao) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entidade.getNome());
            stmt.setString(2, entidade.getDescricao());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar especialidade: " + e.getMessage());
            return false;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean atualizar(Especialidade entidade) {
        String sql = "UPDATE especialidade SET nome = ?, descricao = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entidade.getNome());
            stmt.setString(2, entidade.getDescricao());
            stmt.setLong(3, entidade.getId());

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar especialidade: " + e.getMessage());
            return false;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public boolean excluir(long id) {
        String sql = "DELETE FROM especialidade WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setLong(1, id);

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir especialidade: " + e.getMessage());
            return false;

        } finally {
            fecharRecursos(conn, stmt);
        }
    }

    @Override
    public Especialidade buscarPorId(long id) {
        String sql = "SELECT id, nome, descricao FROM especialidade WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Especialidade especialidade = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                especialidade = new Especialidade();
                especialidade.setId(rs.getLong("id"));
                especialidade.setNome(rs.getString("nome"));
                especialidade.setDescricao(rs.getString("descricao"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar especialidade por ID: " + e.getMessage());

        } finally {
            fecharRecursos(conn, stmt, rs);
        }

        return especialidade;
    }

    @Override
    public List<Especialidade> listarTodos() {
        String sql = "SELECT id, nome, descricao FROM especialidade";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Especialidade> lista = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Especialidade especialidade = new Especialidade();
                especialidade.setId(rs.getLong("id"));
                especialidade.setNome(rs.getString("nome"));
                especialidade.setDescricao(rs.getString("descricao"));
                lista.add(especialidade);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar especialidades: " + e.getMessage());

        } finally {
            fecharRecursos(conn, stmt, rs);
        }

        return lista;
    }
}