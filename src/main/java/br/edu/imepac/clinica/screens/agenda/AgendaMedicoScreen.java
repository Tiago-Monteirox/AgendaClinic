package br.edu.imepac.clinica.screens.agenda;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.screens.prontuario.ProntuarioFormScreen;
import br.edu.imepac.clinica.services.ConsultaService;

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

public class AgendaMedicoScreen extends BaseScreen {

    private final ConsultaService consultaService = new ConsultaService();
    private final Medico medicoLogado;

    private JFormattedTextField txtData;
    private JTable tabelaConsultas;

    private JButton btnAtualizar;
    private JButton btnAtender;
    private JButton btnFechar;

    private final DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public AgendaMedicoScreen(Medico medicoLogado) {
        this.medicoLogado = medicoLogado;
        initComponents();
        carregarConsultas();   // carrega tudo que está AGENDADA pro médico
        centralizar();
    }

    private void initComponents() {
        String nomeMedico = (medicoLogado != null && medicoLogado.getNome() != null)
                ? medicoLogado.getNome()
                : "";
        setTitle("Minha Agenda - " + nomeMedico);
        setSize(750, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");

        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mf);
        } catch (ParseException e) {
            txtData = new JFormattedTextField();
        }
        txtData.setColumns(10);
        // deixo em branco; se preencher, filtra por dia, senão mostra tudo
        txtData.setText("");

        btnAtualizar = new JButton("Atualizar");
        btnAtender = new JButton("Atender consulta");
        btnFechar = new JButton("Fechar");

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(lblData);
        topo.add(txtData);
        topo.add(btnAtualizar);

        tabelaConsultas = new JTable();
        tabelaConsultas.setModel(new DefaultTableModel(
                new Object[][]{},
                // agora com coluna Data
                new String[]{"ID", "Data", "Hora", "Paciente", "Convênio", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaConsultas);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnAtender);
        botoes.add(btnFechar);

        getContentPane().setLayout(new BorderLayout());
        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        // listeners
        btnAtualizar.addActionListener(e -> carregarConsultas());
        txtData.addActionListener(e -> carregarConsultas());
        btnAtender.addActionListener(e -> atenderConsultaSelecionada());
        btnFechar.addActionListener(e -> dispose());
    }

    /**
     * Lê a data digitada.
     * - Se estiver toda em branco -> retorna null (significa “sem filtro de dia”)
     * - Se tiver algo -> tenta converter para LocalDate.
     */
    private LocalDate obterDataFiltro() {
        String texto = txtData.getText().trim();
        String soDigitos = texto.replace("_", "").replace("/", "").trim();

        if (soDigitos.isEmpty()) {
            return null; // sem filtro, mostra tudo
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

    /**
     * Se tiver data → agenda daquele dia.
     * Se campo vazio → todas as consultas AGENDADAS do médico (a partir de hoje).
     */
    private void carregarConsultas() {
        if (medicoLogado == null || medicoLogado.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Médico não informado para carregar agenda.",
                    "Erro de configuração",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate data = obterDataFiltro();
            List<Consulta> consultas;

            if (data == null) {
                // visão geral do médico
                consultas = consultaService.listarTodasPorMedico(medicoLogado.getId());
            } else {
                // só do dia selecionado
                consultas = consultaService.listarAgendaMedico(data, medicoLogado.getId());
            }

            DefaultTableModel model = (DefaultTableModel) tabelaConsultas.getModel();
            model.setRowCount(0);

            for (Consulta c : consultas) {
                String dataStr = "";
                String horaStr = "";

                if (c.getDataHora() != null) {
                    dataStr = c.getDataHora().toLocalDate().format(dataFormatter);
                    horaStr = c.getDataHora().toLocalTime().format(horaFormatter);
                }

                String nomePaciente = c.getPaciente() != null ? c.getPaciente().getNome() : "";
                String nomeConvenio = c.getConvenio() != null ? c.getConvenio().getNome() : "[Particular]";

                model.addRow(new Object[]{
                        c.getId(),
                        dataStr,
                        horaStr,
                        nomePaciente,
                        nomeConvenio,
                        c.getStatus()
                });
            }

        } catch (SQLException | ValidationException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar agenda do médico: " + ex.getMessage(),
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
        Object value = tabelaConsultas.getValueAt(row, 0); // coluna 0 continua sendo o ID
        return Long.valueOf(value.toString());
    }

    private void atenderConsultaSelecionada() {
        Long consultaId = getConsultaIdSelecionada();
        if (consultaId == null) return;

        try {
            Consulta consulta = consultaService.buscarPorId(consultaId);
            if (consulta == null) {
                JOptionPane.showMessageDialog(this,
                        "Consulta não encontrada.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Abre tela de atendimento / prontuário
            ProntuarioFormScreen form = new ProntuarioFormScreen(this, consulta, medicoLogado);
            form.setVisible(true);

            // Recarrega a lista com o filtro atual
            carregarConsultas();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao abrir atendimento: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
