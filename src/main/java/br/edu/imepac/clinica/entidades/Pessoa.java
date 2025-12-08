/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

/**
 *
 * @author tiago-monteiro
 */
public abstract class Pessoa extends BaseEntity {
    private String nome;
    private String cpf;
    private String telefone;

@Override
public void validar() throws ValidationException {
    Validators.notBlank(nome, "Nome");
    Validators.notBlank(telefone,"Telefone");
}   
    
    public String getNome() {
        return nome; 
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }
            
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

}


