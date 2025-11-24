/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;
import br.edu.imepac.clinica.exceptions.ValidationException;

/**
 *
 * @author tiago-monteiro
 */
public abstract class BaseEntity {
    
    protected Long id;
    
    public abstract void validar() throws ValidationException;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
}
