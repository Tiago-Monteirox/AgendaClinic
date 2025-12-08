/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

/**
 *
 * @author evertonhf
 */
public class Especialidade extends BaseEntity{
    
    private String nome;
    private String area;
    private String descricao;
    
    @Override
    public void validar() throws ValidationException {
        Validators.notBlank(nome, "Nome da especialidade");
        Validators.notBlank(descricao, "Descrição da especialidade");
    }
    

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
@Override
public String toString() {
    return nome;
}

    public void setAtivo(boolean aBoolean) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}