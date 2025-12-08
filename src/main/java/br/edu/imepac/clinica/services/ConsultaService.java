package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.enums.EnumStatusConsulta;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultaService {

    private final ConsultaDao consultaDao = new ConsultaDao();

    // ========= CRUD padrão / acesso simples =========

    public Consulta buscarPorId(Long id) throws SQLException {
        return consultaDao.buscarPorId(id);
    }

    public List<Consulta> listarTodos() throws SQLException {
        return consultaDao.listarTodos();
    }

    // ========= US-07 – Agendar consulta =========

    public Consulta agendarConsulta(Consulta consulta)
            throws SQLException, ValidationException {

        if (consulta.getDataHora() == null) {
            throw new ValidationException("Data e hora da consulta são obrigatórias.");
        }
        if (consulta.getMedico() == null || consulta.getMedico().getId() == null) {
            throw new ValidationException("Selecione um médico.");
        }
        if (consulta.getPaciente() == null || consulta.getPaciente().getId() == null) {
            throw new ValidationException("Selecione um paciente.");
        }

        // regra de conflito de horário
        boolean conflito = consultaDao.existeConflitoHorario(
                consulta.getMedico().getId(),
                consulta.getDataHora()
        );

        if (conflito) {
            throw new ValidationException(
                    "Já existe consulta para este médico nesse dia/horário.");
        }

        // status inicial = AGENDADA
        consulta.setStatusEnum(EnumStatusConsulta.AGENDADA);

        consultaDao.inserir(consulta);
        return consulta;
    }

    // ========= US-08 – Remarcar consulta =========

    public void remarcarConsulta(Long consultaId, LocalDateTime novaDataHora)
            throws SQLException, ValidationException {

        if (novaDataHora == null) {
            throw new ValidationException("Nova data/hora é obrigatória.");
        }

        Consulta existente = consultaDao.buscarPorId(consultaId);
        if (existente == null) {
            throw new ValidationException("Consulta não encontrada.");
        }

        EnumStatusConsulta statusAtual = existente.getStatusEnum();
        if (statusAtual == EnumStatusConsulta.CANCELADA
                || statusAtual == EnumStatusConsulta.REALIZADA) {
            throw new ValidationException("Não é permitido remarcar consulta " + statusAtual);
        }

        boolean conflito = consultaDao.existeConflitoHorario(
                existente.getMedico().getId(),
                novaDataHora
        );
        if (conflito) {
            throw new ValidationException("Conflito de horário para o médico.");
        }

        existente.setDataHora(novaDataHora);
        existente.setStatusEnum(EnumStatusConsulta.AGENDADA);

        consultaDao.atualizar(existente);
    }

    // ========= US-09 – Cancelar consulta =========

    public void cancelarConsulta(Long consultaId, String motivo)
            throws SQLException, ValidationException {

        Consulta c = consultaDao.buscarPorId(consultaId);
        if (c == null) {
            throw new ValidationException("Consulta não encontrada.");
        }

        if (c.getStatusEnum() == EnumStatusConsulta.CANCELADA) {
            throw new ValidationException("Consulta já está cancelada.");
        }
        if (c.getStatusEnum() == EnumStatusConsulta.REALIZADA) {
            throw new ValidationException("Consulta já foi realizada; não pode ser cancelada.");
        }

        c.setStatusEnum(EnumStatusConsulta.CANCELADA);

        // truquezinho: guardar o motivo na observação
        if (motivo != null && !motivo.isBlank()) {
            String obs = c.getObservacao();
            if (obs == null) obs = "";
            obs += "\n[Cancelamento] " + motivo;
            c.setObservacao(obs);
        }

        consultaDao.atualizar(c);
    }

    // ========= US-10/11 – Listagens de agenda =========

    /** Agenda da clínica (secretária): visão de um dia só, opcionalmente filtrando por médico */
    public List<Consulta> listarAgendaSecretaria(LocalDate data, Long medicoId)
            throws SQLException {

        if (data == null) {
            data = LocalDate.now();
        }
        return consultaDao.listarPorDataEMedico(data, medicoId);
    }

    /**
     * Agenda do médico:
     *  - se data != null: consultas daquele dia
     *  - se data == null: TODAS as consultas AGENDADAS do médico
     */
    public List<Consulta> listarAgendaMedico(LocalDate data, Long medicoId)
            throws SQLException, ValidationException {

        if (medicoId == null) {
            throw new ValidationException("Id do médico é obrigatório para listar sua agenda.");
        }

        if (data == null) {
            // visão "geral" para o médico – todas as AGENDADAS
            return consultaDao.listarAgendadasPorMedico(medicoId);
        }

        // visão diária
        return consultaDao.listarPorDataEMedico(data, medicoId);
    }

    // ========= Visão geral a partir de hoje (opcional por médico) =========

    /**
     * Lista todas as consultas a partir de hoje (dia atual + futuras),
     * opcionalmente filtrando por médico.
     *
     * - medicoId == null  -> todas as consultas de todos os médicos
     * - medicoId != null  -> só consultas daquele médico
     */
    public List<Consulta> listarTodasPorMedico(Long medicoId) throws SQLException {
        LocalDate hoje = LocalDate.now();
        return consultaDao.listarAPartirDeDataPorMedico(hoje, medicoId);
    }
}
