package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

public class Prontuario extends BaseEntity {

    private String resumo;
    private String anotacoes;
    private String arquivoPdf;

    private Consulta consulta;

    @Override
    public void validar() throws ValidationException {
        if (consulta == null || consulta.getId() == null) {
            throw new ValidationException("Consulta é obrigatória no prontuário.");
        }
        // campo obrigatório segundo o modelo: resumo
        Validators.notBlank(resumo, "Resumo do prontuário");
    }

    // ===== getters/setters =====

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public String getArquivoPdf() {
        return arquivoPdf;
    }

    public void setArquivoPdf(String arquivoPdf) {
        this.arquivoPdf = arquivoPdf;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}
