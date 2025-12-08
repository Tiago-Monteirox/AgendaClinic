package br.edu.imepac.clinica.screens.agenda;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.enums.EnumStatusConsulta;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.ConsultaService;
import br.edu.imepac.clinica.services.ConvenioService;
import br.edu.imepac.clinica.services.MedicoService;
import br.edu.imepac.clinica.services.PacienteService;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ConsultaFormScreen extends JDialog {

    private final ConsultaService consultaService = new ConsultaService();
    private final MedicoService medicoService = new MedicoService();
    private final PacienteService pacienteService = new PacienteService();
    private final ConvenioService convenioService = new ConvenioService();

    private Consulta consultaEdicao;

    private JComboBox<Paciente> comboPaciente;
    private JComboBox<Medico> comboMedico;
    private JComboBox<Convenio> comboConvenio;
    private JFormattedTextField txtDataHora;
    private JCheckBox chkRetorno;
    private JTextArea txtObservacao;

    private JButton btnSalvar;
    private JButton btnCancelar;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ConsultaFormScreen(Frame owner, Consulta consultaEdicao) {
        super(owner, true);
        this.consultaEdicao = consultaEdicao;
        initComponents();
        carregarCombos();
        preencherSeEdicao();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle(consultaEdicao == null ? "Agendar consulta" : "Editar consulta");
        setSize(500, 380);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        comboPaciente = new JComboBox<>();
        comboMedico = new JComboBox<>();
        comboConvenio = new JComboBox<>();

        // Campo com máscara dd/MM/yyyy HH:mm
        try {
            MaskFormatter mf = new MaskFormatter("##/##/#### ##:##");
            mf.setPlaceholderCharacter('_');
            txtDataHora = new JFormattedTextField(mf);
        } catch (ParseException e) {
            // fallback se der ruim na máscara (não deve)
            txtDataHora = new JFormattedTextField();
        }
        txtDataHora.setColumns(16);

        chkRetorno = new JCheckBox("Retorno");

        txtObservacao = new JTextArea(4, 30);
        txtObservacao.setLineWrap(true);
        txtObservacao.setWrapStyleWord(true);

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Paciente
        gbc.gridx = 0; gbc.gridy = y;
        panelCampos.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(comboPaciente, gbc);
        y++;

        // Médico
        gbc.gridx = 0; gbc.gridy = y;
        panelCampos.add(new JLabel("Médico:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(comboMedico, gbc);
        y++;

        // Convênio
        gbc.gridx = 0; gbc.gridy = y;
        panelCampos.add(new JLabel("Convênio:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(comboConvenio, gbc);
        y++;

        // Data/hora
        gbc.gridx = 0; gbc.gridy = y;
        panelCampos.add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1;
        panelCampos.add(txtDataHora, gbc);
        y++;

        // Retorno
        gbc.gridx = 0; gbc.gridy = y;
        panelCampos.add(new JLabel("Retorno:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(chkRetorno, gbc);
        y++;

        // Observação
        gbc.gridx = 0; gbc.gridy = y;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelCampos.add(new JLabel("Observação:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(new JScrollPane(txtObservacao), gbc);
        y++;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout());
        add(panelCampos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarCombos() {
        try {
            comboPaciente.removeAllItems();
            comboMedico.removeAllItems();
            comboConvenio.removeAllItems();

            // Pacientes
            List<Paciente> pacientes = pacienteService.listarTodos();
            for (Paciente p : pacientes) comboPaciente.addItem(p);

            // Médicos
            List<Medico> medicos = medicoService.listarTodos();
            for (Medico m : medicos) comboMedico.addItem(m);

            // Convênios (primeiro "particular" = null)
            comboConvenio.addItem(null);
            List<Convenio> convenios = convenioService.listarTodos();
            for (Convenio c : convenios) comboConvenio.addItem(c);

            // Renderers
            comboPaciente.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                JLabel lbl = new JLabel(value == null ? "" : value.getNome());
                if (isSelected) {
                    lbl.setOpaque(true);
                    lbl.setBackground(list.getSelectionBackground());
                    lbl.setForeground(list.getSelectionForeground());
                }
                return lbl;
            });

            comboMedico.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                JLabel lbl = new JLabel(value == null ? "" : value.getNome());
                if (isSelected) {
                    lbl.setOpaque(true);
                    lbl.setBackground(list.getSelectionBackground());
                    lbl.setForeground(list.getSelectionForeground());
                }
                return lbl;
            });

            comboConvenio.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                String texto = (value == null ? "[Particular]" : value.getNome());
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
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar listas: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherSeEdicao() {
        if (consultaEdicao == null) return;

        // Selecionar paciente
        if (consultaEdicao.getPaciente() != null) {
            for (int i = 0; i < comboPaciente.getItemCount(); i++) {
                Paciente p = comboPaciente.getItemAt(i);
                if (p != null && p.getId().equals(consultaEdicao.getPaciente().getId())) {
                    comboPaciente.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Selecionar médico
        if (consultaEdicao.getMedico() != null) {
            for (int i = 0; i < comboMedico.getItemCount(); i++) {
                Medico m = comboMedico.getItemAt(i);
                if (m != null && m.getId().equals(consultaEdicao.getMedico().getId())) {
                    comboMedico.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Selecionar convênio (ou null = particular)
        if (consultaEdicao.getConvenio() != null &&
            consultaEdicao.getConvenio().getId() != null) {
            for (int i = 0; i < comboConvenio.getItemCount(); i++) {
                Convenio c = comboConvenio.getItemAt(i);
                if (c != null && c.getId().equals(consultaEdicao.getConvenio().getId())) {
                    comboConvenio.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            comboConvenio.setSelectedIndex(0); // [Particular]
        }

        // Data/hora
        if (consultaEdicao.getDataHora() != null) {
            txtDataHora.setText(consultaEdicao.getDataHora().format(formatter));
        } else {
            txtDataHora.setText("");
        }

        chkRetorno.setSelected(consultaEdicao.isRetorno());
        txtObservacao.setText(consultaEdicao.getObservacao());
    }

    private void salvar() {
    if (consultaEdicao == null) {
        consultaEdicao = new Consulta();
    }

    Paciente paciente = (Paciente) comboPaciente.getSelectedItem();
    Medico medico = (Medico) comboMedico.getSelectedItem();
    Convenio convenio = (Convenio) comboConvenio.getSelectedItem();

    try {
        if (paciente == null) {
            throw new ValidationException("Selecione um paciente.");
        }
        if (medico == null) {
            throw new ValidationException("Selecione um médico.");
        }

        String textoData = txtDataHora.getText().trim();
        String soDigitos = textoData.replace("_", "").replace(" ", "");
        if (soDigitos.isEmpty()) {
            throw new ValidationException("Informe a data e hora no formato dd/MM/yyyy HH:mm.");
        }

        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(textoData, formatter);
        } catch (DateTimeParseException dtpe) {
            throw new ValidationException("Data/hora inválida. Use o formato dd/MM/yyyy HH:mm.");
        }

        consultaEdicao.setPaciente(paciente);
        consultaEdicao.setMedico(medico);
        consultaEdicao.setConvenio(convenio); // null = particular
        consultaEdicao.setDataHora(dataHora);
        consultaEdicao.setRetorno(chkRetorno.isSelected());
        consultaEdicao.setObservacao(txtObservacao.getText());

        if (consultaEdicao.getId() == null) {
            // nova consulta: status AGENDADA, regra de conflito no service
            consultaEdicao.setStatusEnum(EnumStatusConsulta.AGENDADA);
            consultaService.agendarConsulta(consultaEdicao);
        } else {
            // remarcar usando a assinatura existente (id + nova data/hora)
            consultaService.remarcarConsulta(consultaEdicao.getId(), consultaEdicao.getDataHora());
        }

        dispose();

    } catch (ValidationException ve) {
        JOptionPane.showMessageDialog(this,
                ve.getMessage(),
                "Dados inválidos",
                JOptionPane.WARNING_MESSAGE);
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Erro ao salvar consulta: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}

}
