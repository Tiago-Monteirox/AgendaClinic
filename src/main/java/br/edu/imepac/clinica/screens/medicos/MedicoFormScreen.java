package br.edu.imepac.clinica.screens.medicos;

import br.edu.imepac.clinica.daos.EspecialidadeDao;
import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.MedicoService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicoFormScreen extends JDialog {

    private final MedicoService medicoService = new MedicoService();
    private final EspecialidadeDao especialidadeDao = new EspecialidadeDao();

    private Medico medicoEdicao;

    private JTextField txtNome;
    private JTextField txtCrm;
    private JTextField txtTelefone;
    private JCheckBox chkAtivo;
    private JComboBox<Especialidade> cbEspecialidade;

    private JButton btnSalvar;
    private JButton btnCancelar;

    public MedicoFormScreen(Frame owner, Medico medicoEdicao) {
        super(owner, true);
        this.medicoEdicao = medicoEdicao;
        initComponents();
        carregarEspecialidades();
        preencherSeEdicao();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle(medicoEdicao == null ? "Novo Médico" : "Editar Médico");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtNome = new JTextField(25);
        txtCrm = new JTextField(15);
        txtTelefone = new JTextField(15);
        chkAtivo = new JCheckBox("Ativo", true);
        cbEspecialidade = new JComboBox<>();

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("CRM:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCrm, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Especialidade:"), gbc);
        gbc.gridx = 1;
        panel.add(cbEspecialidade, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Situação:"), gbc);
        gbc.gridx = 1;
        panel.add(chkAtivo, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(botoes, gbc);

        add(panel);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarEspecialidades() {
        cbEspecialidade.removeAllItems();
        try {
            List<Especialidade> especialidades = new EspecialidadeDao().listarTodos();
            for (Especialidade esp : especialidades) {
                cbEspecialidade.addItem(esp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar especialidades: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherSeEdicao() {
        if (medicoEdicao == null) return;

        txtNome.setText(medicoEdicao.getNome());
        txtCrm.setText(medicoEdicao.getCrm());
        txtTelefone.setText(medicoEdicao.getTelefone());
        chkAtivo.setSelected(medicoEdicao.isAtivo());

        if (medicoEdicao.getEspecialidade() != null) {
            for (int i = 0; i < cbEspecialidade.getItemCount(); i++) {
                Especialidade esp = cbEspecialidade.getItemAt(i);
                if (esp.getId().equals(medicoEdicao.getEspecialidade().getId())) {
                    cbEspecialidade.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void salvar() {
        try {
            if (medicoEdicao == null) {
                medicoEdicao = new Medico();
            }

            medicoEdicao.setNome(txtNome.getText());
            medicoEdicao.setCrm(txtCrm.getText());
            medicoEdicao.setTelefone(txtTelefone.getText());
            medicoEdicao.setAtivo(chkAtivo.isSelected());
            medicoEdicao.setEspecialidade((Especialidade) cbEspecialidade.getSelectedItem());

            medicoService.salvar(medicoEdicao);

            JOptionPane.showMessageDialog(this,
                    "Médico salvo com sucesso!",
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
                    "Erro ao salvar médico: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
