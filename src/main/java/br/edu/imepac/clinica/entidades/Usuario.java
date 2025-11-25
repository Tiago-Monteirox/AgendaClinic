/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

import java.time.LocalDateTime;

/**
 *
 * @author tiago-monteiro
 */
public class Usuario extends BaseEntity {
    
    private String nomeLogin;
    private String senha;
    private String status;
    private LocalDateTime ultimoLogin;
    private boolean bloqueado;
    private int tentativasFalhas;
    private Secretaria secretaria;
    private Perfil perfil;
    
    @Override
    public void validar() throws ValidationException {
        Validators.notBlank(nomeLogin, "Login");
        Validators.minLenght(senha, 6, "senha");
    
        if (perfil == null) {
            throw new ValidationException("Perfil do usuário é obrigatório");
        }    
    }
    
    public void registrarFalhaLogin() {
        tentativasFalhas++;
        if (tentativasFalhas >= 5) {
            bloqueado = true;
        }
    }
    
    public void resetarTentativas() {
        tentativasFalhas = 0;
    }
    
    public boolean isAtivo() {
        return "ATIVO".equalsIgnoreCase(status) && !bloqueado;
    }
    
    public String getNomeLogin() {
        return nomeLogin;
    }
    
    public void setNomeLogin(String nomeLogin) {
        this.nomeLogin = nomeLogin;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getTentativasFalhas() {
        return tentativasFalhas;
    }

    public void setTentativasFalhas(int tentativasFalhas) {
        this.tentativasFalhas = tentativasFalhas;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        this.secretaria = secretaria;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}

    
    
    
    
    
}

