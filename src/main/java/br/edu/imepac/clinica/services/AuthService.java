package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.PerfilFuncionalidadeDao;
import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumFuncionalidade;
import br.edu.imepac.clinica.enums.EnumStatusUsuario;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.session.SessionContext;

import java.sql.SQLException;
import java.util.Set;

public class AuthService {

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final PerfilFuncionalidadeDao perfilFuncionalidadeDao = new PerfilFuncionalidadeDao();

    public Usuario autenticar(String login, String senha) throws SQLException, ValidationException {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            throw new ValidationException("Usuário e senha são obrigatórios.");
        }

        Usuario usuario = usuarioDao.buscarPorLogin(login);

        if (usuario == null) {
            throw new ValidationException("Usuário ou senha inválidos.");
        }

        // por enquanto senha em texto puro
        if (!usuario.getSenha().equals(senha)) {
            throw new ValidationException("Usuário ou senha inválidos.");
        }

        EnumStatusUsuario status = usuario.getStatusEnum();
        if (status == EnumStatusUsuario.INATIVO || status == EnumStatusUsuario.BLOQUEADO) {
            throw new ValidationException("Usuário não está ativo no sistema.");
        }

        // === AQUI ENTRA A MÁGICA DO CONTEXTO ===
        Long perfilId = usuario.getPerfil() != null ? usuario.getPerfil().getId() : null;

        Set<EnumFuncionalidade> funcionalidades = Set.of();
        if (perfilId != null) {
            funcionalidades = perfilFuncionalidadeDao
                    .buscarFuncionalidadesPorPerfil(perfilId);
        }

        SessionContext ctx = SessionContext.getInstance();
        ctx.setUsuarioLogado(usuario);
        ctx.setPerfil(usuario.getPerfil());
        ctx.setFuncionalidades(funcionalidades);

        // se chegou até aqui, está logado e contexto preenchido
        return usuario;
    }
}
