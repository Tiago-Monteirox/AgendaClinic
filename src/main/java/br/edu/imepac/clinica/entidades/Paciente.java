package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

import java.time.LocalDate;

public class Paciente extends Pessoa { // supondo que já estenda Pessoa
    // já herdado de Pessoa: id, nome, cpf, telefone...

    // Campos extras usados pelo DAO / telas
    private String planoSaude;
    private String tipoSanguineo;
    private LocalDate dataNascimento;
    private String sexo;

    private String endereco;
    private String observacoes;
    private boolean ativo = true;

    private Convenio convenio;

    @Override
    public void validar() throws ValidationException {
        Validators.notBlank(getNome(), "Nome");
        Validators.notBlank(getCpf(), "CPF");
 
    }



    public String getPlanoSaude() {
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

    // --- getters / setters usados pelas telas (endereço, observações, ativo) ---

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
