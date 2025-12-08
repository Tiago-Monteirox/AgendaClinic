package br.edu.imepac.clinica.screens.prontuario;

import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.entidades.Prontuario;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.services.ProntuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoricoPacienteScreen extends JDialog {

    private final ProntuarioService prontuarioService = new ProntuarioService();
    private final Paciente paciente;

    private JTable tabela;
    private JButton btnVerProntuario;
    private JButton btnFechar;

    private List<Prontuario> prontuarios;
    private final DateTimeFormatter dataHoraFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistoricoPacienteScreen(Frame owner, Paciente paciente) {
        super(owner, true);
        this.paciente = paciente;
        initComponents();
        carregarDados();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle("Histórico do paciente - " +
                (paciente != null ? paciente.getNome() : ""));
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tabela = new JTable();
        tabela.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Data", "Médico", "Tipo", "Resumo diagnóstico"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);

        btnVerProntuario = new JButton("Ver prontuário");
        btnFechar = new JButton("Fechar");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnVerProntuario);
        botoes.add(btnFechar);

        getContentPane().setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        btnVerProntuario.addActionListener(e -> abrirProntuarioSelecionado());
        btnFechar.addActionListener(e -> dispose());
    }

    private void carregarDados() {
        if (paciente == null || paciente.getId() == null) {
            return;
        }

        try {
            prontuarios = prontuarioService.listarHistoricoPorPaciente(paciente.getId());

            DefaultTableModel model = (DefaultTableModel) tabela.getModel();
            model.setRowCount(0);

            for (Prontuario p : prontuarios) {
                Consulta c = p.getConsulta();
                String data = (c != null && c.getDataHora() != null)
                        ? c.getDataHora().format(dataHoraFormatter)
                        : "";

                Medico m = (c != null) ? c.getMedico() : null;
                String nomeMedico = (m != null) ? m.getNome() : "";

                String tipo = "[Particular]";
                if (c != null && c.getConvenio() != null) {
                    tipo = c.getConvenio().getNome();
                }

                String resumo = (p.getResumo() != null) ? p.getResumo() : "";

                model.addRow(new Object[]{data, nomeMedico, tipo, resumo});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar histórico do paciente: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirProntuarioSelecionado() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um registro na tabela.",
                    "Nenhuma seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Prontuario p = prontuarios.get(row);
        Consulta c = p.getConsulta();
        Medico m = (c != null) ? c.getMedico() : null;

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ProntuarioFormScreen tela =
                new ProntuarioFormScreen(owner, c, m, true);
        tela.setVisible(true);
    }
}
