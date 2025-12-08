package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.enums.EnumStatusConsulta;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

import java.time.LocalDateTime;

public class Consulta extends BaseEntity {

    private LocalDateTime dataHora;
    private boolean retorno;
    private String carteiraConvenio;
    private String observacao;
    private String status; // AGENDADA, REALIZADA, CANCELADA

    private Paciente paciente;
    private Medico medico;
    private Convenio convenio;

    @Override
    public void validar() throws ValidationException {
        if (dataHora == null) {
            throw new ValidationException("Data/hora da consulta é obrigatória.");
        }

        if (paciente == null || paciente.getId() == null) {
            throw new ValidationException("Paciente é obrigatório.");
        }

        if (medico == null || medico.getId() == null) {
            throw new ValidationException("Médico é obrigatório.");
        }

        // convênio pode ser opcional, então aqui só valida se seu negócio exigir
        // if (convenio == null || convenio.getId() == null) { ... }

        Validators.notBlank(status, "Status da consulta");
    }

    // ===== getters e setters =====

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    public String getCarteiraConvenio() {
        return carteiraConvenio;
    }

    public void setCarteiraConvenio(String carteiraConvenio) {
        this.carteiraConvenio = carteiraConvenio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    // ===== integração com EnumStatusConsulta =====

    public EnumStatusConsulta getStatusEnum() {
        return EnumStatusConsulta.fromString(status);
    }

    public void setStatusEnum(EnumStatusConsulta statusEnum) {
        if (statusEnum != null) {
            this.status = statusEnum.name();
        }
    }
}
