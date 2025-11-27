package br.edu.imepac.clinica.session;

import br.edu.imepac.clinica.entidades.Usuario;

public class SessionContext {

    private static SessionContext instance;

    private Usuario usuarioLogado;

    private SessionContext() {
    }

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void limpar() {
        this.usuarioLogado = null;
    }

    public boolean isLogado() {
        return this.usuarioLogado != null;
    }
}
