package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

public class PerfilFuncionalidade extends BaseEntity {

    private Perfil perfil;
    private String funcionalidade;

    @Override
    public void validar() throws ValidationException {
        if (perfil == null) {
            throw new ValidationException("Perfil é obrigatório");
        }
        Validators.notBlank(funcionalidade, "Funcionalidade");
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getFuncionalidade() {
        return funcionalidade;
    }

    public void setFuncionalidade(String funcionalidade) {
        this.funcionalidade = funcionalidade;
    }
}
