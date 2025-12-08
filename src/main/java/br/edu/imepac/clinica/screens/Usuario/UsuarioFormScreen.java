package br.edu.imepac.clinica.screens.usuarios;

import br.edu.imepac.clinica.daos.PerfilDao;
import br.edu.imepac.clinica.daos.SecretariaDao;
import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.Secretaria;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumStatusUsuario;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.services.MedicoService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UsuarioFormScreen extends BaseScreen {

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final PerfilDao perfilDao = new PerfilDao();
    private final SecretariaDao secretariaDao = new SecretariaDao();
    private final MedicoService medicoService = new MedicoService();

    private Usuario usuarioEdicao;

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<EnumStatusUsuario> cbStatus;
    private JComboBox<Perfil> cbPerfil;
    private JComboBox<Secretaria> cbSecretaria;

    // novo: vínculo com médico
    private JLabel lblMedico;
    private JComboBox<Medico> cbMedico;

    private JButton btnSalvar;
    private JButton btnCancelar;

    public UsuarioFormScreen(Usuario usuarioEdicao) {
        this.usuarioEdicao = usuarioEdicao;

        setTitle(usuarioEdicao == null ? "Novo Usuário" : "Editar Usuário");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        carregarCombos();

        if (usuarioEdicao != null) {
            preencherFormulario();
        }

        // ajusta estado inicial do campo médico conforme perfil
        atualizarCampoMedico();

        pack();
        centralizar();
    }

    private void initComponents() {
        JLabel lblLogin = new JLabel("Login:");
        JLabel lblSenha = new JLabel("Senha:");
        JLabel lblStatus = new JLabel("Status:");
        JLabel lblPerfil = new JLabel("Perfil:");
        JLabel lblSecretaria = new JLabel("Secretária:");
        lblMedico = new JLabel("Médico:");

        txtLogin = new JTextField(20);
        txtSenha = new JPasswordField(20);
        cbStatus = new JComboBox<>(EnumStatusUsuario.values());
        cbPerfil = new JComboBox<>();
        cbSecretaria = new JComboBox<>();
        cbMedico = new JComboBox<>();

        // começa desabilitado; só habilita se perfil for médico
        lblMedico.setEnabled(false);
        cbMedico.setEnabled(false);

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        // quando mudar o perfil, avalia se precisa habilitar o combo de médico
        cbPerfil.addActionListener(e -> atualizarCampoMedico());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int linha = 0;

        // Linha 0 - Login
        gbc.gridx = 0; gbc.gridy = linha;
        panel.add(lblLogin, gbc);
        gbc.gridx = 1;
        panel.add(txtLogin, gbc);
        linha++;

        // Linha 1 - Senha
        gbc.gridx = 0; gbc.gridy = linha;
        panel.add(lblSenha, gbc);
        gbc.gridx = 1;
        panel.add(txtSenha, gbc);
        linha++;

        // Linha 2 - Status
        gbc.gridx = 0; gbc.gridy = linha;
        panel.add(lblStatus, gbc);
        gbc.gridx = 1;
        panel.add(cbStatus, gbc);
        linha++;

        // Linha 3 - Perfil
        gbc.gridx = 0; gbc.gridy = linha;
        panel.add(lblPerfil, gbc);
        gbc.gridx = 1;
        panel.add(cbPerfil, gbc);
        linha++;

        // Linha 4 - Secretaria
        gbc.gridx = 0; gbc.gridy = linha;
        panel.add(lblSecretaria, gbc);
        gbc.gridx = 1;
        panel.add(cbSecretaria, gbc);
        linha++;

        // Linha 5 - Médico (apenas para perfil médico)
        gbc.gridx = 0; gbc.gridy = linha;
        panel.add(lblMedico, gbc);
        gbc.gridx = 1;
        panel.add(cbMedico, gbc);
        linha++;

        // Linha 6 - Botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoesPanel.add(btnSalvar);
        botoesPanel.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = linha;
        gbc.gridwidth = 2;
        panel.add(botoesPanel, gbc);

        getContentPane().add(panel);
    }

    private void carregarCombos() {
        try {
            // Perfis
            cbPerfil.removeAllItems();
            List<Perfil> perfis = perfilDao.listarTodos();
            for (Perfil p : perfis) {
                cbPerfil.addItem(p);
            }

            // Secretarias
            cbSecretaria.removeAllItems();
            List<Secretaria> secretarias = secretariaDao.listarTodos();
            for (Secretaria s : secretarias) {
                cbSecretaria.addItem(s);
            }

            // Médicos
            carregarMedicos();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar perfis/secretarias/médicos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void carregarMedicos() {
        try {
            cbMedico.removeAllItems();
            List<Medico> medicos = medicoService.listarTodos();
            for (Medico m : medicos) {
                cbMedico.addItem(m);
            }

            // renderer bonitinho
            cbMedico.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                String texto = (value == null)
                        ? ""
                        : value.getNome() + " (ID: " + value.getId() + ")";
                JLabel lbl = new JLabel(texto);
                if (isSelected) {
                    lbl.setOpaque(true);
                    lbl.setBackground(list.getSelectionBackground());
                    lbl.setForeground(list.getSelectionForeground());
                }
                return lbl;
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar médicos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void preencherFormulario() {
        txtLogin.setText(usuarioEdicao.getNomeLogin());
        // por segurança, deixa o campo senha vazio em edição
        cbStatus.setSelectedItem(usuarioEdicao.getStatusEnum());

        if (usuarioEdicao.getPerfil() != null) {
            for (int i = 0; i < cbPerfil.getItemCount(); i++) {
                Perfil p = cbPerfil.getItemAt(i);
                if (p.getId().equals(usuarioEdicao.getPerfil().getId())) {
                    cbPerfil.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (usuarioEdicao.getSecretaria() != null) {
            for (int i = 0; i < cbSecretaria.getItemCount(); i++) {
                Secretaria s = cbSecretaria.getItemAt(i);
                if (s.getId().equals(usuarioEdicao.getSecretaria().getId())) {
                    cbSecretaria.setSelectedIndex(i);
                    break;
                }
            }
        }

        // médico vinculado (se houver)
        if (usuarioEdicao.getMedico() != null &&
                usuarioEdicao.getMedico().getId() != null) {

            atualizarCampoMedico(); // garante que o combo está habilitado

            Long idMedicoUsuario = usuarioEdicao.getMedico().getId();
            for (int i = 0; i < cbMedico.getItemCount(); i++) {
                Medico m = cbMedico.getItemAt(i);
                if (m != null && m.getId().equals(idMedicoUsuario)) {
                    cbMedico.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean isPerfilMedico(Perfil perfil) {
        if (perfil == null || perfil.getNome() == null) return false;
        String nome = perfil.getNome().toUpperCase();
        return nome.contains("MEDICO"); // PERFIL_MEDICO, MEDICO, etc.
    }

    private void atualizarCampoMedico() {
        Perfil perfilSelecionado = (Perfil) cbPerfil.getSelectedItem();
        boolean ehMedico = isPerfilMedico(perfilSelecionado);

        lblMedico.setEnabled(ehMedico);
        cbMedico.setEnabled(ehMedico);

        if (!ehMedico) {
            cbMedico.setSelectedItem(null);
        }
    }

    private void salvar() {
        try {
            String login = txtLogin.getText().trim();
            String senhaDigitada = new String(txtSenha.getPassword());
            EnumStatusUsuario statusSelecionado = (EnumStatusUsuario) cbStatus.getSelectedItem();
            Perfil perfilSelecionado = (Perfil) cbPerfil.getSelectedItem();
            Secretaria secretariaSelecionada = (Secretaria) cbSecretaria.getSelectedItem();

            if (login.isBlank()) {
                JOptionPane.showMessageDialog(this, "Login é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (usuarioEdicao == null && senhaDigitada.isBlank()) {
                JOptionPane.showMessageDialog(this, "Senha é obrigatória para novo usuário.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (perfilSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Perfil é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario u = (usuarioEdicao == null ? new Usuario() : usuarioEdicao);

            u.setNomeLogin(login);
            if (!senhaDigitada.isBlank()) {
                // aqui poderia aplicar hash de senha, se você decidir implementar
                u.setSenha(senhaDigitada);
            }
            if (statusSelecionado != null) {
                u.setStatusEnum(statusSelecionado);
            }
            u.setPerfil(perfilSelecionado);
            u.setSecretaria(secretariaSelecionada);

            // vínculo médico <-> usuário, se o perfil for médico
            if (isPerfilMedico(perfilSelecionado)) {
                Medico medicoSelecionado = (Medico) cbMedico.getSelectedItem();
                if (medicoSelecionado == null || medicoSelecionado.getId() == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Selecione o médico vinculado a este usuário.",
                            "Validação",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                u.setMedico(medicoSelecionado);
            } else {
                u.setMedico(null);
            }

            boolean novo = (u.getId() == null);
            boolean ok = usuarioDao.salvar(u);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        novo ? "Usuário cadastrado com sucesso." : "Usuário atualizado com sucesso."
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Nenhuma linha afetada ao salvar usuário.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
