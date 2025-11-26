package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.exceptions.ValidationException;
import java.sql.SQLException;
import java.util.List;
        
/**
 *
 * @author tiago-monteiro
 */
public interface GenericDao<T> {
    
    boolean inserir(T entidade) throws SQLException, ValidationException;
    
    boolean atualizar(T entidade) throws SQLException, ValidationException;
    
    boolean excluir(Long id) throws SQLException;
    
    T buscarPorId(Long id) throws SQLException;
    
    List<T> listarTodos() throws SQLException;
    
}
