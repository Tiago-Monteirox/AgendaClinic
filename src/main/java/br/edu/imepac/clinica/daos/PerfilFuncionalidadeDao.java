package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.enums.EnumFuncionalidade;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class PerfilFuncionalidadeDao extends BaseDao {

    /**
     * Retorna todas as funcionalidades associadas a um perfil.
     * Este método é essencial para o controle de permissões
     * no SessionContext e MainMenu.
     */
    public Set<EnumFuncionalidade> buscarFuncionalidadesPorPerfil(Long perfilId) throws SQLException {
        String sql = "SELECT funcionalidade FROM perfil_funcionalidade WHERE perfil_id = ?";

        Set<EnumFuncionalidade> funcionalidades = new HashSet<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, perfilId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String func = rs.getString("funcionalidade");

                // Converte string para EnumFuncionalidade
                try {
                    funcionalidades.add(EnumFuncionalidade.valueOf(func));
                } catch (IllegalArgumentException e) {
                    System.err.println("⚠️ Funcionalidade inválida no banco: " + func);
                }
            }
            return funcionalidades;

        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }
}
