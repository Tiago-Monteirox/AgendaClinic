package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class MedicoService {

    private final MedicoDao medicoDao = new MedicoDao();
    private final ConsultaDao consultaDao = new ConsultaDao();

    public List<Medico> listarTodos() throws SQLException {
        return medicoDao.listarTodos();
    }

    public void salvar(Medico medico) throws SQLException, ValidationException {
        medico.validar();
        medicoDao.salvar(medico);
    }

    public boolean excluirOuInativar(Long id) throws SQLException, ValidationException {
        if (id == null) {
            throw new ValidationException("Selecione um médico.");
        }

        Medico medico = medicoDao.buscarPorId(id);
        if (medico == null) {
            throw new ValidationException("Médico não encontrado.");
        }

        boolean possuiConsultas = consultaDao.existeConsultaPorMedico(id);

        if (possuiConsultas) {
            // apenas inativa
            medico.setAtivo(false);
            medicoDao.atualizar(medico);
            // retorna false pra tela saber que NÃO excluiu, só inativou
            return false;
        }

        // exclui de verdade
        medicoDao.excluir(id);
        return true;
    }
}
