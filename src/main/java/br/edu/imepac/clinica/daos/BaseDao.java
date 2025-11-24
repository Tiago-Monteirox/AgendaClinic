/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe base para todos os DAOs.
 * 
 * Responsável por gerenciar a conexão com o banco de dados
 * e disponibilizar métodos utilitários para os DAOs específicos.
 * 
 * @author evertonhf
 */
public abstract class BaseDao {
    
    private static final String URL = "jdbc:mysql://localhost:3306/clinica";
    private static final String USUARIO = "tiago-monteiro";
    private static final String SENHA = "Benz*ina123";
    
    /**
     * Obtém uma conexão ativa com o banco de dados.
     * 
     * @return Objeto Connection ativo.
     * @throws SQLException Caso haja erro na conexão.
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
    
    /**
     * Fecha os recursos de forma segura, evitando leaks de memória.
     * 
     * @param conn conexão com o banco
     * @param stmt comando SQL preparado
     * @param rs conjunto de resultados (opcional)
     */
    protected void fecharRecursos(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar ResultSet: " + e.getMessage());
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar Connection: " + e.getMessage());
        }
    }
    
    /**
     * Fecha apenas a conexão e o PreparedStatement (para operações sem ResultSet).
     * 
     * @param conn conexão com o banco
     * @param stmt comando SQL preparado
     */
    protected void fecharRecursos(Connection conn, PreparedStatement stmt) {
        fecharRecursos(conn, stmt, null);
    }
}