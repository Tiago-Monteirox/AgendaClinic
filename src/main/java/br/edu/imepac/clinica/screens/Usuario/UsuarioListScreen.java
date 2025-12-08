package br.edu.imepac.clinica.screens.usuarios;

import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumFuncionalidade;
import br.edu.imepac.clinica.enums.EnumStatusUsuario;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.session.SessionContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class UsuarioListScreen extends BaseScreen {

    private final UsuarioDao usuarioDao = new UsuarioDao();

    private JTable tabela;
    private DefaultTableModel tabelaModel;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnInativar;
    private JButton btnFechar;

    public UsuarioListScreen() {
        // segurança de acesso: só quem tem GERENCIAR_USUARIOS
        SessionContext ctx = SessionContext.getInstance();
        if (!ctx.possuiFuncionalidade(EnumFuncionalidade.GERENCIAR_USUARIOS)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Você não possui permissão para gerenciar usuários.",
                    "Acesso negado",
                    JOptionPane.WARNING_MESSAGE
            );
            dispose();
            return;
        }

        setTitle("Usuários do Sistema");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        carregarUsuarios();

        pack();
        centralizar();
    }

    private void initComponents() {
        tabelaModel = new DefaultTableModel(
                new Object[]{"ID", "Login", "Status", "PerfilId", "SecretariaId"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela só leitura
            }
        };

        tabela = new JTable(tabelaModel);
        JScrollPane scroll = new JScrollPane(tabela);

        btnNovo = new JButton("Novo");
        btnEditar = new JButton("Editar");
        btnInativar = new JButton("Inativar");
        btnFechar = new JButton("Fechar");

        btnNovo.addActionListener(e -> abrirFormNovo());
        btnEditar.addActionListener(e -> abrirFormEdicao());
        btnInativar.addActionListener(e -> inativarUsuarioSelecionado());
        btnFechar.addActionListener(e -> dispose());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoesPanel.add(btnNovo);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnInativar);
        botoesPanel.add(btnFechar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(botoesPanel, BorderLayout.SOUTH);
    }

    private void carregarUsuarios() {
        try {
            tabelaModel.setRowCount(0);
            List<Usuario> usuarios = usuarioDao.listarTodos();
            for (Usuario u : usuarios) {
                tabelaModel.addRow(new Object[]{
                        u.getId(),
                        u.getNomeLogin(),
                        u.getStatus(),
                        (u.getPerfil() != null ? u.getPerfil().getId() : null),
                        (u.getSecretaria() != null ? u.getSecretaria().getId() : null)
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar usuários.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Long getIdSelecionado() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um usuário na tabela.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
        return (Long) tabelaModel.getValueAt(row, 0);
    }

    private void abrirFormNovo() {
        UsuarioFormScreen form = new UsuarioFormScreen(null);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                carregarUsuarios();
            }
        });
        form.setVisible(true);
    }

    private void abrirFormEdicao() {
        Long id = getIdSelecionado();
        if (id == null) return;

        try {
            Usuario usuario = usuarioDao.buscarPorId(id);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UsuarioFormScreen form = new UsuarioFormScreen(usuario);
            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    carregarUsuarios();
                }
            });
            form.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inativarUsuarioSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) return;

        int opc = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente inativar este usuário?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (opc != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Usuario usuario = usuarioDao.buscarPorId(id);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            usuario.setStatus(EnumStatusUsuario.INATIVO.name());
            usuarioDao.atualizar(usuario);

            JOptionPane.showMessageDialog(this, "Usuário inativado com sucesso.");
            carregarUsuarios();

        } catch (SQLException | ValidationException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao inativar usuário: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
