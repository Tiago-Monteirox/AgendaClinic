package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.enums.EnumFuncionalidade;
import br.edu.imepac.clinica.screens.agenda.AgendaMedicoScreen;
import br.edu.imepac.clinica.screens.agenda.AgendaSecretariaScreen;
import br.edu.imepac.clinica.screens.convenios.ConvenioListScreen;
import br.edu.imepac.clinica.screens.especialidades.EspecialidadeListScreen;
import br.edu.imepac.clinica.screens.medicos.MedicoListScreen;
import br.edu.imepac.clinica.screens.pacientes.PacienteListScreen;
import br.edu.imepac.clinica.screens.usuarios.UsuarioListScreen;
import br.edu.imepac.clinica.session.SessionContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MainMenu extends BaseScreen {

    private JMenuBar menuBar;

    private JMenu menuCadastros;
    private JMenuItem itemMedicos;
    private JMenuItem itemPacientes;
    private JMenuItem itemConvenios;
    private JMenuItem itemEspecialidades;
    private JMenuItem itemUsuarios;

    private JMenu menuAgenda;
    private JMenuItem itemAgendaSecretaria;
    private JMenuItem itemAgendaMedico;

    private JMenu menuSistema;
    private JMenuItem itemLogout;
    private JMenuItem itemSair;

    // label de fundo
    private JLabel backgroundLabel;

    public MainMenu() {
        initComponents();
        configurarMenuBar();
        aplicarPermissoes();
        ajustarLarguraTela();
        posicionarTopo(0);
        configurarBackground();
    }

    private void initComponents() {
        setTitle("AgendaClinic - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        backgroundLabel = new JLabel();
        backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundLabel.setVerticalAlignment(SwingConstants.CENTER);

        setContentPane(backgroundLabel);
    }

    private void configurarBackground() {
        SwingUtilities.invokeLater(() ->
                setImageIcon("agendaclinic_logo.jpeg", backgroundLabel)
        );
    }

    private void configurarMenuBar() {
        menuBar = new JMenuBar();

        // ==== CADASTROS ====
        menuCadastros = new JMenu("Cadastros");

        itemMedicos = new JMenuItem("Médicos");
        itemMedicos.addActionListener(this::abrirCadastroMedicos);

        itemPacientes = new JMenuItem("Pacientes");
        itemPacientes.addActionListener(this::abrirCadastroPacientes);

        itemConvenios = new JMenuItem("Convênios");
        itemConvenios.addActionListener(this::abrirCadastroConvenios);

        itemEspecialidades = new JMenuItem("Especialidades");
        itemEspecialidades.addActionListener(this::abrirCadastroEspecialidades);

        itemUsuarios = new JMenuItem("Usuários do sistema");
        itemUsuarios.addActionListener(this::abrirCadastroUsuarios);

        menuCadastros.add(itemMedicos);
        menuCadastros.add(itemPacientes);
        menuCadastros.add(itemConvenios);
        menuCadastros.add(itemEspecialidades);
        menuCadastros.addSeparator();
        menuCadastros.add(itemUsuarios);

        // ==== AGENDA ====
        menuAgenda = new JMenu("Agenda");

        itemAgendaSecretaria = new JMenuItem("Agenda da clínica");
        itemAgendaSecretaria.addActionListener(this::abrirAgendaSecretaria);

        itemAgendaMedico = new JMenuItem("Minha agenda (médico)");
        itemAgendaMedico.addActionListener(this::abrirAgendaMedico);

        menuAgenda.add(itemAgendaSecretaria);
        menuAgenda.add(itemAgendaMedico);

        // ==== SISTEMA ====
        menuSistema = new JMenu("Sistema");

        itemLogout = new JMenuItem("Logout");
        itemLogout.setMnemonic(KeyEvent.VK_L);
        itemLogout.addActionListener(this::fazerLogout);

        itemSair = new JMenuItem("Sair");
        itemSair.setMnemonic(KeyEvent.VK_S);
        itemSair.addActionListener(e -> System.exit(0));

        menuSistema.add(itemLogout);
        menuSistema.addSeparator();
        menuSistema.add(itemSair);

        menuBar.add(menuCadastros);
        menuBar.add(menuAgenda);
        menuBar.add(menuSistema);

        setJMenuBar(menuBar);
    }

    private void aplicarPermissoes() {
        SessionContext ctx = SessionContext.getInstance();

        System.out.println(">> [MainMenu] isLogado = " + ctx.isLogado());
        System.out.println(">> [MainMenu] funcionalidades = " + ctx.getFuncionalidades());

        if (!ctx.isLogado()) {
            menuCadastros.setEnabled(false);
            menuAgenda.setEnabled(false);
            return;
        }

        itemMedicos.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.GERENCIAR_MEDICOS));
        itemPacientes.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.GERENCIAR_PACIENTES));
        itemConvenios.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.GERENCIAR_CONVENIOS));
        itemEspecialidades.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.GERENCIAR_ESPECIALIDADES));
        itemUsuarios.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.GERENCIAR_USUARIOS));

        itemAgendaSecretaria.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.VISUALIZAR_AGENDA_SECRETARIA));
        itemAgendaMedico.setEnabled(
                ctx.possuiFuncionalidade(EnumFuncionalidade.VISUALIZAR_AGENDA_MEDICO));
    }

    // ==== Ações de menu ====

    private void abrirCadastroMedicos(ActionEvent e) {
        new MedicoListScreen().setVisible(true);
    }

    private void abrirCadastroPacientes(ActionEvent e) {
        new PacienteListScreen().setVisible(true);
    }

    private void abrirCadastroConvenios(ActionEvent e) {
        new ConvenioListScreen().setVisible(true);
    }

    private void abrirCadastroEspecialidades(ActionEvent e) {
        new EspecialidadeListScreen().setVisible(true);
    }

    private void abrirCadastroUsuarios(ActionEvent e) {
        new UsuarioListScreen().setVisible(true);
    }

    private void abrirAgendaSecretaria(ActionEvent e) {
        new AgendaSecretariaScreen().setVisible(true);
    }

    private void abrirAgendaMedico(ActionEvent e) {
        SessionContext ctx = SessionContext.getInstance();

        if (!ctx.possuiFuncionalidade(EnumFuncionalidade.VISUALIZAR_AGENDA_MEDICO)) {
            JOptionPane.showMessageDialog(this,
                    "Você não tem permissão para visualizar a agenda do médico.",
                    "Acesso negado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        var usuario = ctx.getUsuarioLogado();
        var medicoLogado = (usuario != null) ? usuario.getMedico() : null;

        if (medicoLogado == null || medicoLogado.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Este usuário não está associado a um médico.",
                    "Configuração incompleta",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        new AgendaMedicoScreen(medicoLogado).setVisible(true);
    }

    private void fazerLogout(ActionEvent e) {
        SessionContext.getInstance().limpar();
        JOptionPane.showMessageDialog(this,
                "Sessão encerrada.",
                "Logout",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginScreen().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
