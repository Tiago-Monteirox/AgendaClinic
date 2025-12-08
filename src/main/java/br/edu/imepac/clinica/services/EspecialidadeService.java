package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.EspecialidadeDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class EspecialidadeService {

    private final EspecialidadeDao especialidadeDao = new EspecialidadeDao();
    private final MedicoDao medicoDao = new MedicoDao();

    public List<Especialidade> listarTodos() throws SQLException {
        return especialidadeDao.listarTodos();
    }

    public void salvar(Especialidade esp) throws SQLException, ValidationException {
        esp.validar(); // assume que Especialidade valida nome obrigatório
        especialidadeDao.salvar(esp);
    }

    public void excluirOuBloquear(Long id) throws SQLException, ValidationException {
        boolean temMedicos = medicoDao.existeMedicoParaEspecialidade(id);

        if (temMedicos) {
            throw new ValidationException(
                    "Não é possível excluir: existem médicos vinculados a essa especialidade."
            );
        }

        especialidadeDao.excluir(id);
    }
}
