package br.edu.imepac.clinica.session;

import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumFuncionalidade;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SessionContext {

    private static SessionContext instance;

    private Usuario usuarioLogado;
    private Perfil perfil;
    private Set<EnumFuncionalidade> funcionalidades = new HashSet<>();

    private SessionContext() {}

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public boolean isLogado() {
        // não inventa moda: logado = tem usuário
        return usuarioLogado != null;
    }

    public void limpar() {
        usuarioLogado = null;
        perfil = null;
        funcionalidades.clear();
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Set<EnumFuncionalidade> getFuncionalidades() {
        return Collections.unmodifiableSet(funcionalidades);
    }

    public void setFuncionalidades(Set<EnumFuncionalidade> funcionalidades) {
        this.funcionalidades.clear();
        if (funcionalidades != null) {
            this.funcionalidades.addAll(funcionalidades);
        }
    }

    public boolean possuiFuncionalidade(EnumFuncionalidade funcionalidade) {
        return funcionalidades.contains(funcionalidade);
    }
}
