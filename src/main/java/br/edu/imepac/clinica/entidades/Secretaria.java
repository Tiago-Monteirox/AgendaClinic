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
public class Secretaria extends Pessoa {
    
    private String pis;
    private String turno;
    private String setor;
    
    @Override
    public void validar() throws ValidationException {
        super.validar();
        Validators.notBlank(pis, "PIS");
        Validators.notBlank(turno, "Turno");
        Validators.notBlank(setor, "Setor");        
    }
    
    public String getPis(){
        return pis;
    }
    
    public void setPis(String pis){
        this.pis = pis;
    }
    
    public String getTurno() {
        return turno;
    }
    
    public void setTurno(String turno) {
        this.turno = turno;
    }
    
    public String getSetor() {
        return setor;
    }
    
    public void setSetor(String setor) {
        this.setor = setor;
    }
    
}


