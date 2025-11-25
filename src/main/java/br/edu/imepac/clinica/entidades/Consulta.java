/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.time.LocalDateTime;

/**
 *
 * @author tiago-monteiro
 */
public class Consulta extends BaseEntity {
    
    private LocalDateTime dataHora;
    private boolean retorno;
    private String carteiraConvenio;
    private String observacao;
    private String status;
    private Paciente paciente;
    private Medico medico;
    private Convenio convenio;
    
   @Override
    public void validar() throws ValidationException {
        if (dataHora == null) {
            throw new ValidationException("Data e hora da consulta são obrigatórias");
        }
        if (paciente == null) {
            throw new ValidationException("Paciente é obrigatório");
        }
        paciente.validar();

        if (medico == null) {
            throw new ValidationException("Médico é obrigatório");
        }
        medico.validar();

        if (!medico.isAtivo()) {
            throw new ValidationException("Médico está inativo");
        }

        if (convenio != null && !convenio.isAtivo()) {
            throw new ValidationException("Convênio selecionado está inativo");
        }
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    public String getCarteiraConvenio() {
        return carteiraConvenio;
    }

    public void setCarteiraConvenio(String carteiraConvenio) {
        this.carteiraConvenio = carteiraConvenio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }
}