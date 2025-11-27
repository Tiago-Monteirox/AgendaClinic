package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

public class Medico extends Pessoa {

    private String crm;
    private int tempoExperiencia; // em anos
    private String formacao;
    private Especialidade especialidade;
    private boolean ativo = true;

    @Override
    public void validar() throws ValidationException {
        super.validar();
        Validators.notBlank(crm, "CRM");

        if (tempoExperiencia < 0) {
            throw new ValidationException("Tempo de experiência não pode ser negativo");
        }

        if (especialidade == null) {
            throw new ValidationException("Especialidade é obrigatória");
        }

        especialidade.validar();
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public int getTempoExperiencia() {
        return tempoExperiencia;
    }

    public void setTempoExperiencia(int tempoExperiencia) {
        this.tempoExperiencia = tempoExperiencia;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
