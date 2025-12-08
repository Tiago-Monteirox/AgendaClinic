package br.edu.imepac.clinica.services;

import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumStatusUsuario;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioDao usuarioDao = new UsuarioDao();

    public Usuario criarUsuario(Usuario usuario) throws SQLException, ValidationException {
        usuario.setStatusEnum(EnumStatusUsuario.ATIVO);
        usuario.setBloqueado(false);
        usuario.setTentativasFalhas(0);
        usuarioDao.inserir(usuario);
        return usuario;
    }

    public boolean atualizarUsuario(Usuario usuario) throws SQLException, ValidationException {
        return usuarioDao.atualizar(usuario);
    }

    public boolean inativarUsuario(Long id) throws SQLException, ValidationException {
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario == null) {
            throw new ValidationException("Usuário não encontrado.");
        }
        usuario.setStatusEnum(EnumStatusUsuario.INATIVO);
        return usuarioDao.atualizar(usuario);
    }

    public Usuario buscarPorId(Long id) throws SQLException {
        return usuarioDao.buscarPorId(id);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDao.listarTodos();
    }
}
