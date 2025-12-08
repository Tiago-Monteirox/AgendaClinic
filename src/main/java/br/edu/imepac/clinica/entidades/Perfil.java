package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends BaseEntity {

    private String nome;
    private String descricao;
    private int nivelAcesso;
    private List<PerfilFuncionalidade> funcionalidades = new ArrayList<>();

    @Override
    public void validar() throws ValidationException {
        Validators.notBlank(nome, "Nome do perfil");
    }

    public void adicionarFuncionalidade(PerfilFuncionalidade func) {
        if (func != null) {
            funcionalidades.add(func);
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public List<PerfilFuncionalidade> getFuncionalidades() {
        return funcionalidades;
    }

    public void setFuncionalidades(List<PerfilFuncionalidade> funcionalidades) {
        this.funcionalidades = funcionalidades;
    }
    
@Override
public String toString() {
    return getNome() + " (nível " + getNivelAcesso() + ")";
}

}
