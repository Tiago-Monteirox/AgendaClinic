package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.PacienteDao;
import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class PacienteService {

    private final PacienteDao pacienteDao = new PacienteDao();
    private final ConsultaDao consultaDao = new ConsultaDao();

    public List<Paciente> listarTodos() throws SQLException {
        return pacienteDao.listarTodos();
    }

    public List<Paciente> buscarPorNome(String nome) throws SQLException {
        if (nome == null || nome.isBlank()) {
            return listarTodos();
        }
        return pacienteDao.buscarPorNome(nome);
    }

    public Paciente buscarPorCpf(String cpf) throws SQLException {
        return pacienteDao.buscarPorCpf(cpf);
    }

    public void salvar(Paciente p) throws SQLException, ValidationException {
        pacienteDao.salvar(p);
    }

    public void excluirOuInativar(Long idPaciente) throws SQLException, ValidationException {
        // se tiver consulta, inativa; se não tiver, exclui
        boolean temConsulta = consultaDao.existeConsultaPorPaciente(idPaciente);

        if (temConsulta) {
            Paciente p = pacienteDao.buscarPorId(idPaciente);
            if (p == null) return;
            p.setAtivo(false);
            pacienteDao.atualizar(p);
        } else {
            pacienteDao.excluir(idPaciente);
        }
    }
}
