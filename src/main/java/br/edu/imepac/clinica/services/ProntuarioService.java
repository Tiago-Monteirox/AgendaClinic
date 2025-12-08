package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.ProntuarioDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Prontuario;
import br.edu.imepac.clinica.enums.EnumStatusConsulta;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class ProntuarioService {

    private final ProntuarioDao prontuarioDao = new ProntuarioDao();
    private final ConsultaDao consultaDao = new ConsultaDao();

    public Prontuario buscarPorConsulta(Long consultaId) throws SQLException {
        return prontuarioDao.buscarPorConsultaId(consultaId);
    }

    public List<Prontuario> listarHistoricoPorPaciente(Long pacienteId) throws SQLException {
        return prontuarioDao.listarPorPaciente(pacienteId);
    }

    /**
     * Salva (cria ou atualiza) prontuário e marca consulta como REALIZADA.
     */
    public void salvarAtendimento(Consulta consulta, Prontuario prontuario)
            throws SQLException, ValidationException {

        if (consulta == null || consulta.getId() == null) {
            throw new ValidationException("Consulta inválida para salvar prontuário.");
        }

        if (prontuario == null) {
            throw new ValidationException("Dados de prontuário não informados.");
        }

        if (prontuario.getResumo() == null || prontuario.getResumo().isBlank()) {
            throw new ValidationException("Resumo do atendimento é obrigatório.");
        }

        // vincula à consulta
        prontuario.setConsulta(consulta);

        // 1:1 – cria ou atualiza
        Prontuario existente = prontuarioDao.buscarPorConsultaId(consulta.getId());
        if (existente == null) {
            prontuarioDao.inserir(prontuario);
        } else {
            prontuario.setId(existente.getId());
            prontuarioDao.atualizar(prontuario);
        }

        // Atualiza status da consulta -> REALIZADA
        consulta.setStatusEnum(EnumStatusConsulta.REALIZADA);
        consultaDao.atualizar(consulta);
    }
}
