package br.edu.imepac.clinica.screens.agenda;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.services.ConsultaService;
import br.edu.imepac.clinica.services.MedicoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AgendaSecretariaScreen extends BaseScreen {

    private final ConsultaService consultaService = new ConsultaService();
    private final MedicoService medicoService = new MedicoService();

    private JFormattedTextField txtData;
    private JComboBox<Medico> comboMedicos;
    private JTable tabelaConsultas;

    private JButton btnAtualizar;
    private JButton btnAgendar;
    private JButton btnRemarcar;
    private JButton btnCancelar;

    private final DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public AgendaSecretariaScreen() {
        initComponents();
        carregarMedicos();
        // abre com todas as consultas (a partir de hoje) de todos os médicos
        carregarConsultas(null, null);
        centralizar();
    }

    private void initComponents() {
        setTitle("Agenda da Clínica");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");

        // Campo com máscara de data dd/MM/yyyy
        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mf);
        } catch (ParseException e) {
            txtData = new JFormattedTextField();
        }
        txtData.setColumns(10);
        // Se quiser abrir já com hoje:
        // txtData.setText(LocalDate.now().format(dataFormatter));

        JLabel lblMedico = new JLabel("Médico:");
        comboMedicos = new JComboBox<>();

        btnAtualizar = new JButton("Atualizar");
        btnAgendar = new JButton("Agendar");
        btnRemarcar = new JButton("Remarcar");
        btnCancelar = new JButton("Cancelar");

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(lblData);
        topo.add(txtData);
        topo.add(lblMedico);
        topo.add(comboMedicos);
        topo.add(btnAtualizar);

        tabelaConsultas = new JTable();
        tabelaConsultas.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Data", "Hora", "Médico", "Paciente", "Convênio", "Retorno", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaConsultas);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnAgendar);
        botoes.add(btnRemarcar);
        botoes.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout());
        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        // listeners
        btnAtualizar.addActionListener(e -> {
            LocalDate data = obterDataFiltro();   // pode ser null
            Long medicoId = obterMedicoFiltro();  // pode ser null
            carregarConsultas(data, medicoId);
        });

        btnAgendar.addActionListener(e -> abrirFormAgendamento(null));
        btnRemarcar.addActionListener(e -> remarcarConsultaSelecionada());
        btnCancelar.addActionListener(e -> cancelarConsultaSelecionada());
    }

    private void carregarMedicos() {
        try {
            comboMedicos.removeAllItems();

            // item "Todos"
            comboMedicos.addItem(null);

            List<Medico> medicos = medicoService.listarTodos();
            for (Medico m : medicos) {
                comboMedicos.addItem(m);
            }

            comboMedicos.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                JLabel lbl = new JLabel();
                if (value == null) {
                    lbl.setText("[Todos os médicos]");
                } else {
                    lbl.setText(value.getNome());
                }
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
                    "Erro ao carregar médicos: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lê o campo de data:
     * - se estiver vazio (só com '_' e '/'), retorna null -> sem filtro de data
     * - se tiver algo, tenta fazer parse dd/MM/yyyy
     */
    private LocalDate obterDataFiltro() {
        String texto = txtData.getText().trim();

        String soDigitos = texto.replace("_", "")
                                .replace("/", "")
                                .trim();

        if (soDigitos.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(texto, dataFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data inválida. Use o formato dd/MM/yyyy.",
                    "Data inválida",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private Long obterMedicoFiltro() {
        Medico selecionado = (Medico) comboMedicos.getSelectedItem();
        return (selecionado != null ? selecionado.getId() : null);
    }

    /**
     * Carrega a tabela:
     * - se data != null -> usa listarAgendaSecretaria(data, medicoId)  (apenas aquele dia)
     * - se data == null -> usa listarTodasPorMedico(medicoId)         (a partir de hoje)
     */
    private void carregarConsultas(LocalDate dataFiltro, Long medicoId) {
        try {
            List<Consulta> consultas;

            if (dataFiltro != null) {
                consultas = consultaService.listarAgendaSecretaria(dataFiltro, medicoId);
            } else {
                consultas = consultaService.listarTodasPorMedico(medicoId);
            }

            DefaultTableModel model = (DefaultTableModel) tabelaConsultas.getModel();
            model.setRowCount(0);

            for (Consulta c : consultas) {
                String dataStr = c.getDataHora() != null
                        ? c.getDataHora().toLocalDate().format(dataFormatter)
                        : "";

                String hora = c.getDataHora() != null
                        ? c.getDataHora().toLocalTime().format(horaFormatter)
                        : "";

                String nomeMedico   = (c.getMedico()   != null ? c.getMedico().getNome()   : "");
                String nomePaciente = (c.getPaciente() != null ? c.getPaciente().getNome() : "");
                String nomeConvenio = (c.getConvenio() != null ? c.getConvenio().getNome() : "");
                String retorno      = c.isRetorno() ? "Sim" : "Não";

                model.addRow(new Object[]{
                        c.getId(),
                        dataStr,
                        hora,
                        nomeMedico,
                        nomePaciente,
                        nomeConvenio,
                        retorno,
                        c.getStatus()
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar agenda: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long getConsultaIdSelecionada() {
        int row = tabelaConsultas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma consulta na tabela.",
                    "Nenhuma seleção",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Object value = tabelaConsultas.getValueAt(row, 0);
        if (value instanceof Long) {
            return (Long) value;
        } else {
            return Long.valueOf(value.toString());
        }
    }

    private void abrirFormAgendamento(Consulta consultaEdicao) {
        ConsultaFormScreen form = new ConsultaFormScreen(this, consultaEdicao);
        form.setVisible(true);

        LocalDate data = obterDataFiltro();
        Long medicoId = obterMedicoFiltro();
        carregarConsultas(data, medicoId);
    }

    private void remarcarConsultaSelecionada() {
        Long consultaId = getConsultaIdSelecionada();
        if (consultaId == null) return;

        try {
            ConsultaDao consultaDao = new ConsultaDao();
            Consulta consulta = consultaDao.buscarPorId(consultaId);
            if (consulta == null) {
                JOptionPane.showMessageDialog(this,
                        "Consulta não encontrada.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            abrirFormAgendamento(consulta);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar consulta para remarcar: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarConsultaSelecionada() {
        Long consultaId = getConsultaIdSelecionada();
        if (consultaId == null) return;

        String motivo = JOptionPane.showInputDialog(
                this,
                "Informe o motivo do cancelamento:",
                "Cancelar consulta",
                JOptionPane.QUESTION_MESSAGE
        );
        if (motivo == null) {
            return;
        }

        try {
            consultaService.cancelarConsulta(consultaId, motivo);

            LocalDate data = obterDataFiltro();
            Long medicoId = obterMedicoFiltro();
            carregarConsultas(data, medicoId);

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao cancelar consulta: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
