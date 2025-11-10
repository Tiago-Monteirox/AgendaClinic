/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.edu.imepac.clinica.interfaces;

/**
 * Interface genérica que define as operações básicas de persistência (CRUD).
 * 
 * @param <T> Tipo da entidade (por exemplo, Medico, Paciente, Consulta, etc.)
 * @author evertonhf
 */
public interface Persistente<T> {
    
    /**
     * Salva uma nova entidade no banco de dados.
     * @param entidade Objeto a ser salvo.
     * @return true se o salvamento foi bem-sucedido, false caso contrário.
     */
    boolean salvar(T entidade);
    
    /**
     * Atualiza uma entidade existente no banco de dados.
     * @param entidade Objeto com os dados atualizados.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    boolean atualizar(T entidade);
    
    /**
     * Remove uma entidade com base no identificador.
     * @param id Identificador único da entidade.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     */
    boolean excluir(long id);
    
    /**
     * Busca uma entidade específica pelo seu identificador.
     * @param id Identificador único da entidade.
     * @return A entidade encontrada ou null se não existir.
     */
    T buscarPorId(long id);
    
    /**
     * Lista todas as entidades persistidas.
     * @return Uma lista contendo todas as entidades.
     */
    java.util.List<T> listarTodos();
}