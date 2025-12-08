package br.edu.imepac.clinica.screens.especialidades;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.EspecialidadeService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EspecialidadeFormScreen extends JDialog {

    private final EspecialidadeService especialidadeService = new EspecialidadeService();
    private Especialidade especialidadeEdicao;

    private JTextField txtNome;
    private JTextArea txtDescricao;
    private JButton btnSalvar;
    private JButton btnCancelar;

    public EspecialidadeFormScreen(Frame owner, Especialidade especialidadeEdicao) {
        super(owner, true);
        this.especialidadeEdicao = especialidadeEdicao;
        initComponents();
        preencherSeEdicao();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle(especialidadeEdicao == null ? "Nova Especialidade" : "Editar Especialidade");
        setSize(420, 260);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtNome = new JTextField(25);
        txtDescricao = new JTextArea(4, 25);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        JPanel painelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0 - Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCampos.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        painelCampos.add(txtNome, gbc);

        // Linha 1 - Descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painelCampos.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        painelCampos.add(scrollDescricao, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout());
        add(painelCampos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void preencherSeEdicao() {
        if (especialidadeEdicao == null) {
            return;
        }
        txtNome.setText(especialidadeEdicao.getNome());
        // só preenche se o campo existir na entidade
        txtDescricao.setText(especialidadeEdicao.getDescricao());
    }

    private void salvar() {
        if (especialidadeEdicao == null) {
            especialidadeEdicao = new Especialidade();
        }

        especialidadeEdicao.setNome(txtNome.getText());
        especialidadeEdicao.setDescricao(txtDescricao.getText()); // aqui entra a descrição 💡

        try {
            especialidadeService.salvar(especialidadeEdicao);
            JOptionPane.showMessageDialog(
                    this,
                    "Especialidade salva com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(
                    this,
                    ve.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar especialidade: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
