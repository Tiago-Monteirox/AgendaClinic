package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.ConvenioDao;
import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ConvenioService {

    private final ConvenioDao convenioDao = new ConvenioDao();
    private final ConsultaDao consultaDao = new ConsultaDao();

    public List<Convenio> listarTodos() throws SQLException {
        return convenioDao.listarTodos();
    }

    // Útil para combo na tela de agendamento
    public List<Convenio> listarApenasAtivos() throws SQLException {
        return convenioDao.listarTodos()
                .stream()
                .filter(Convenio::isAtivo)
                .collect(Collectors.toList());
    }

    public void salvar(Convenio convenio) throws SQLException, ValidationException {
        convenio.validar(); // assume que Convenio estende BaseEntity e já tem validar()
        convenioDao.salvar(convenio);
    }

    public void excluirOuInativar(Long id) throws SQLException, ValidationException {
        boolean usadoEmConsulta = consultaDao.existeConsultaParaConvenio(id);

        if (usadoEmConsulta) {
            Convenio c = convenioDao.buscarPorId(id);
            if (c == null) {
                throw new ValidationException("Convênio não encontrado para inativar.");
            }
            c.setAtivo(false);
            convenioDao.atualizar(c);
            throw new ValidationException("Convênio possui consultas e foi marcado como inativo.");
        }

        convenioDao.excluir(id);
    }
}
