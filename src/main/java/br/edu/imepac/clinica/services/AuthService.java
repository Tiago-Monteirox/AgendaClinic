package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumStatusUsuario;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;

public class AuthService {

    private final UsuarioDao usuarioDao = new UsuarioDao();

    public Usuario autenticar(String login, String senha) throws SQLException, ValidationException {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            throw new ValidationException("Usuário e senha são obrigatórios.");
        }

        Usuario usuario = usuarioDao.buscarPorLogin(login);

        if (usuario == null) {
            throw new ValidationException("Usuário ou senha inválidos.");
        }

        // aqui você pode depois trocar pra hash, por enquanto comparando texto puro:
        if (!usuario.getSenha().equals(senha)) {
            throw new ValidationException("Usuário ou senha inválidos.");
        }

        EnumStatusUsuario status = usuario.getStatusEnum();
        if (status == EnumStatusUsuario.INATIVO || status == EnumStatusUsuario.BLOQUEADO) {
            throw new ValidationException("Usuário não está ativo no sistema.");
        }

        // se passou por tudo:
        return usuario;
    }
}
