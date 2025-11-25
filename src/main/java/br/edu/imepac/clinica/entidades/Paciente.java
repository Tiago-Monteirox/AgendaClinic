/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

import java.time.LocalDate;

/**
 *
 * @author tiago-monteiro
 */
public class Paciente extends Pessoa {
    
    private String planoSaude;
    private String tipoSanguineo;
    private LocalDate dataNascimento; 
    private String sexo;
    private Convenio convenio;
    
    @Override
    public void validar() throws ValidationException {
        super.validar();
        Validators.futureDate(dataNascimento, "Data de nascimento");
        
        if (convenio != null && !convenio.isAtivo()) {
            throw new ValidationException("Convênio selecionado está inativo");
        }
    }
    
    public String getPlanoSaude(){
        return planoSaude;
    }
    
    public void setPlanoSaude(String planoSaude) {
        this.planoSaude = planoSaude;
    }
    
    public String getTipoSanguineo() {
        return tipoSanguineo;
    }
    
    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public String getSexo() {
        return sexo;
    }
        
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }    
    
    public Convenio getConvenio() {
        return convenio;
    }
    
    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }
}
