package br.edu.imepac.clinica.screens.pacientes;

import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.PacienteService;

import javax.swing.*;
import java.awt.*;

public class PacienteFormScreen extends JDialog {

    private final PacienteService pacienteService = new PacienteService();
    private Paciente pacienteEdicao;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEndereco;
    private JTextArea txtObservacoes;
    private JCheckBox chkAtivo;

    private JButton btnSalvar;
    private JButton btnCancelar;

    public PacienteFormScreen(Frame owner, Paciente pacienteEdicao) {
        super(owner, true);
        this.pacienteEdicao = pacienteEdicao;
        initComponents();
        preencherSeEdicao();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle(pacienteEdicao == null ? "Novo Paciente" : "Editar Paciente");
        setSize(450, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtNome = new JTextField(30);
        txtCpf = new JTextField(14);
        txtTelefone = new JTextField(15);
        txtEndereco = new JTextField(40);
        txtObservacoes = new JTextArea(4, 30);
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);

        chkAtivo = new JCheckBox("Ativo", true);

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEndereco, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtObservacoes), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Situação:"), gbc);
        gbc.gridx = 1;
        panel.add(chkAtivo, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(botoes, gbc);

        add(panel);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void preencherSeEdicao() {
        if (pacienteEdicao == null) return;

        txtNome.setText(pacienteEdicao.getNome());
        txtCpf.setText(pacienteEdicao.getCpf());
        txtTelefone.setText(pacienteEdicao.getTelefone());
        txtEndereco.setText(pacienteEdicao.getEndereco());
        txtObservacoes.setText(pacienteEdicao.getObservacoes());
        chkAtivo.setSelected(pacienteEdicao.isAtivo());
    }

    private void salvar() {
        try {
            if (pacienteEdicao == null) {
                pacienteEdicao = new Paciente();
            }

            pacienteEdicao.setNome(txtNome.getText());
            pacienteEdicao.setCpf(txtCpf.getText());
            pacienteEdicao.setTelefone(txtTelefone.getText());
            pacienteEdicao.setEndereco(txtEndereco.getText());
            pacienteEdicao.setObservacoes(txtObservacoes.getText());
            pacienteEdicao.setAtivo(chkAtivo.isSelected());

            pacienteService.salvar(pacienteEdicao);

            JOptionPane.showMessageDialog(this,
                    "Paciente salvo com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar paciente: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
