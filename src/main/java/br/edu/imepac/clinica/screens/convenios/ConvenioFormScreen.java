package br.edu.imepac.clinica.screens.convenios;

import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.ConvenioService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ConvenioFormScreen extends JDialog {

    private final ConvenioService convenioService = new ConvenioService();
    private Convenio convenioEdicao;

    private JTextField txtNome;
    private JTextField txtCodigo;
    private JCheckBox chkAtivo;
    private JButton btnSalvar;
    private JButton btnCancelar;

    public ConvenioFormScreen(Frame owner, Convenio convenioEdicao) {
        super(owner, true);
        this.convenioEdicao = convenioEdicao;
        initComponents();
        preencherSeEdicao();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle(convenioEdicao == null ? "Novo Convênio" : "Editar Convênio");
        setSize(400, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtNome = new JTextField(25);
        txtCodigo = new JTextField(15);
        chkAtivo = new JCheckBox("Ativo", true);

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        JPanel painelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelCampos.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        painelCampos.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelCampos.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        painelCampos.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelCampos.add(new JLabel("Situação:"), gbc);
        gbc.gridx = 1;
        painelCampos.add(chkAtivo, gbc);

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
        if (convenioEdicao == null) return;

        txtNome.setText(convenioEdicao.getNome());
        txtCodigo.setText(convenioEdicao.getCodigo());
        chkAtivo.setSelected(convenioEdicao.isAtivo());
    }

    private void salvar() {
        if (convenioEdicao == null) {
            convenioEdicao = new Convenio();
        }

        convenioEdicao.setNome(txtNome.getText());
        convenioEdicao.setCodigo(txtCodigo.getText());
        convenioEdicao.setAtivo(chkAtivo.isSelected());

        try {
            convenioService.salvar(convenioEdicao);
            dispose();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar convênio: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
