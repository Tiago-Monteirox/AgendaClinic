package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

public class Convenio extends BaseEntity {

    private String nome;
    private String codigo;
    private String descricao;
    private boolean ativo = true;

    @Override
    public void validar() throws ValidationException {
        Validators.notBlank(nome, "Nome do convênio");
        Validators.notBlank(codigo, "Código do convênio");
        // descrição pode ser opcional
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
