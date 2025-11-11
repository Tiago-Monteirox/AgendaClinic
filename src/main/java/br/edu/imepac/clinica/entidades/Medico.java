/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.entidades;

/**
 *
 * @author evertonhf
 */
public class Medico {

    private long id;
    private String nome;
    private String crm;

    private long especialidadeId;
    private Especialidade especialidade;

    public Medico() {
    }

    public Medico(String nome, String crm, Especialidade especialidade) {
        this(nome, crm, especialidade.getId());
    }

    public Medico(String nome, String crm, long especialidadeId) {
        this.nome = nome;
        this.crm = crm;
        this.especialidadeId = especialidadeId;
    }

    public Medico(long id, String nome, String crm, long especialidadeId) {
        this.id = id;
        this.nome = nome;
        this.crm = crm;
        this.especialidadeId = especialidadeId;
    }

    public Medico(long id, String nome, String crm, Especialidade especialidade) {
        this(id, nome, crm, especialidade.getId());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public long getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(long especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String toString() {
        return nome;
    }
}
