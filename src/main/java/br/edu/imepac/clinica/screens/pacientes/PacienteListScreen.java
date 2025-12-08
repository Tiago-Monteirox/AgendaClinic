package br.edu.imepac.clinica.screens.pacientes;

import br.edu.imepac.clinica.entidades.Paciente;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.services.PacienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PacienteListScreen extends BaseScreen {

    private final PacienteService pacienteService = new PacienteService();

    private JTable tabela;
    private DefaultTableModel tableModel;

    private JTextField txtFiltroNome;
    private JTextField txtFiltroCpf;
    private JButton btnFiltrar;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    public PacienteListScreen() {
        initComponents();
        carregarTabela();
        centralizar();
    }

private void initComponents() {
    setTitle("Cadastro de Pacientes");
    setSize(900, 400);
    setMinimumSize(new Dimension(900, 400));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    txtFiltroNome = new JTextField(15);
    txtFiltroCpf = new JTextField(11);
    btnFiltrar = new JButton("Filtrar");
    btnAtualizar = new JButton("Atualizar");
    btnNovo = new JButton("Novo");
    btnEditar = new JButton("Editar");
    btnExcluir = new JButton("Excluir/Inativar");

    // 🔥 Painel ÚNICO, tudo alinhado sem espaço no meio
    JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

    topo.add(new JLabel("Nome:"));
    topo.add(txtFiltroNome);
    topo.add(new JLabel("CPF:"));
    topo.add(txtFiltroCpf);

    topo.add(btnFiltrar);
    topo.add(btnAtualizar);

    topo.add(btnNovo);
    topo.add(btnEditar);
    topo.add(btnExcluir);

    // ==== TABELA ====
    tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nome", "CPF", "Telefone", "Ativo"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tabela = new JTable(tableModel);
    JScrollPane scroll = new JScrollPane(tabela);

    add(topo, BorderLayout.NORTH);
    add(scroll, BorderLayout.CENTER);

    // LISTENERS
    btnFiltrar.addActionListener(e -> carregarTabela());
    btnAtualizar.addActionListener(e -> carregarTabela());
    btnNovo.addActionListener(e -> abrirFormulario(null));
    btnEditar.addActionListener(e -> editarSelecionado());
    btnExcluir.addActionListener(e -> excluirSelecionado());
}


    private void carregarTabela() {
        tableModel.setRowCount(0);
        try {
            // filtros estão prontos se você quiser usar depois
            // String nome = txtFiltroNome.getText();
            // String cpf = txtFiltroCpf.getText();

            List<Paciente> pacientes = pacienteService.listarTodos();

            for (Paciente p : pacientes) {
                tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getNome(),
                        p.getCpf(),
                        p.getTelefone(),
                        p.isAtivo() ? "Sim" : "Não"
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pacientes: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long getIdSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Long) tabela.getValueAt(row, 0);
    }

    private void abrirFormulario(Paciente paciente) {
        PacienteFormScreen form = new PacienteFormScreen(this, paciente);
        form.setVisible(true);
        carregarTabela();
    }

    private void editarSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um paciente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // poderia ter um pacienteService.buscarPorId(id), mas vamos seguir tua pegada
            List<Paciente> pacientes = pacienteService.listarTodos();
            Paciente paciente = pacientes.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (paciente == null) {
                JOptionPane.showMessageDialog(this,
                        "Paciente não encontrado.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            abrirFormulario(paciente);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar paciente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um paciente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opc = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir/inativar este paciente?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (opc != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            pacienteService.excluirOuInativar(id);
            carregarTabela();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir/inativar paciente: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
