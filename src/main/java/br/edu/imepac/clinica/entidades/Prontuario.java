package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

public class Prontuario extends BaseEntity {

    private String resumo;
    private String anotacoes;
    private String arquivoPdf; // caminho no disco ou nome do arquivo
    private Consulta consulta;

    @Override
    public void validar() throws ValidationException {
        if (consulta == null) {
            throw new ValidationException("Consulta associada é obrigatória");
        }
        Validators.notBlank(resumo, "Resumo do prontuário");
    }

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
