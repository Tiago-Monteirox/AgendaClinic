package br.edu.imepac.clinica.screens.prontuario;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Prontuario;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.ProntuarioService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ProntuarioFormScreen extends JDialog {

    private final ProntuarioService prontuarioService = new ProntuarioService();

    private final Consulta consulta;
    private final Medico medicoLogado;
    private final boolean somenteLeitura;

    private JLabel lblPaciente;
    private JLabel lblDataHora;
    private JLabel lblRetorno;
    private JLabel lblConvenio;

    private JTextArea txtResumo;
    private JTextArea txtAnotacoes;
    private JTextField txtArquivoPdf;

    private JButton btnSalvar;
    private JButton btnCancelar;
    private JButton btnHistoricoPaciente;

    private final DateTimeFormatter dataHoraFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // construtor padrão (edição / atendimento)
    public ProntuarioFormScreen(Frame owner, Consulta consulta, Medico medicoLogado) {
        this(owner, consulta, medicoLogado, false);
    }

    // construtor alternativo para modo leitura
    public ProntuarioFormScreen(Frame owner,
                                Consulta consulta,
                                Medico medicoLogado,
                                boolean somenteLeitura) {
        super(owner, true);
        this.consulta = consulta;
        this.medicoLogado = medicoLogado;
        this.somenteLeitura = somenteLeitura;
        initComponents();
        carregarDadosConsulta();
        carregarProntuarioExistente();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setTitle(somenteLeitura ? "Prontuário - Visualização" : "Atendimento - Prontuário");
        setSize(700, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel topo = new JPanel(new GridLayout(2, 2, 10, 5));
        lblPaciente = new JLabel();
        lblDataHora = new JLabel();
        lblRetorno = new JLabel();
        lblConvenio = new JLabel();

        topo.add(new JLabel("Paciente:"));
        topo.add(lblPaciente);
        topo.add(new JLabel("Data/Hora:"));
        topo.add(lblDataHora);
        topo.add(new JLabel("Retorno:"));
        topo.add(lblRetorno);
        topo.add(new JLabel("Convênio:"));
        topo.add(lblConvenio);

        txtResumo = new JTextArea(3, 50);
        txtAnotacoes = new JTextArea(5, 50);
        txtArquivoPdf = new JTextField(40);

        txtResumo.setLineWrap(true);
        txtResumo.setWrapStyleWord(true);
        txtAnotacoes.setLineWrap(true);
        txtAnotacoes.setWrapStyleWord(true);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(criarBloco("Resumo * (diagnóstico/conduta resumidos)", txtResumo));
        centro.add(criarBloco("Anotações", txtAnotacoes));

        JPanel arquivoPanel = new JPanel(new BorderLayout());
        arquivoPanel.setBorder(BorderFactory.createTitledBorder("Arquivo PDF (opcional - caminho/URL)"));
        arquivoPanel.add(txtArquivoPdf, BorderLayout.NORTH);
        centro.add(arquivoPanel);

        btnSalvar = new JButton("Salvar atendimento");
        btnCancelar = new JButton("Fechar");
        btnHistoricoPaciente = new JButton("Histórico do paciente");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnHistoricoPaciente);
        if (!somenteLeitura) {
            botoes.add(btnSalvar);
        }
        botoes.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout());
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(centro), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
        btnHistoricoPaciente.addActionListener(e -> abrirHistoricoPaciente());

        if (somenteLeitura) {
            txtResumo.setEditable(false);
            txtAnotacoes.setEditable(false);
            txtArquivoPdf.setEditable(false);
            btnSalvar.setEnabled(false);
        }
    }

    private JPanel criarBloco(String titulo, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void carregarDadosConsulta() {
        lblPaciente.setText(consulta.getPaciente() != null ? consulta.getPaciente().getNome() : "");
        lblDataHora.setText(consulta.getDataHora() != null
                ? consulta.getDataHora().format(dataHoraFormatter)
                : "");
        lblRetorno.setText(consulta.isRetorno() ? "Sim" : "Não");
        lblConvenio.setText(consulta.getConvenio() != null
                ? consulta.getConvenio().getNome()
                : "[Particular]");
    }

    private void carregarProntuarioExistente() {
        try {
            Prontuario existente = prontuarioService.buscarPorConsulta(consulta.getId());
            if (existente != null) {
                txtResumo.setText(existente.getResumo());
                txtAnotacoes.setText(existente.getAnotacoes());
                txtArquivoPdf.setText(existente.getArquivoPdf());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar prontuário existente: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        if (somenteLeitura) {
            return;
        }

        Prontuario p = new Prontuario();
        p.setResumo(txtResumo.getText());
        p.setAnotacoes(txtAnotacoes.getText());
        p.setArquivoPdf(txtArquivoPdf.getText());

        try {
            prontuarioService.salvarAtendimento(consulta, p);
            JOptionPane.showMessageDialog(this,
                    "Atendimento salvo com sucesso.",
                    "OK",
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
                    "Erro ao salvar atendimento: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirHistoricoPaciente() {
        if (consulta == null || consulta.getPaciente() == null) {
            JOptionPane.showMessageDialog(this,
                    "Paciente não identificado para histórico.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        HistoricoPacienteScreen tela =
                new HistoricoPacienteScreen(owner, consulta.getPaciente());
        tela.setVisible(true);
    }
}
