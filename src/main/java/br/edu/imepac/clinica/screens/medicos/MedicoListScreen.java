package br.edu.imepac.clinica.screens.medicos;

import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.services.MedicoService;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.screens.BaseScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicoListScreen extends BaseScreen {

    private final MedicoService medicoService = new MedicoService();

    private JTable tabela;
    private DefaultTableModel tableModel;

    private JTextField txtFiltroNome;
    private JButton btnFiltrar;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    public MedicoListScreen() {
        initComponents();
        carregarTabela();
        centralizar();
    }

    private void initComponents() {
        setTitle("Cadastro de Médicos");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtFiltroNome = new JTextField(20);
        btnFiltrar = new JButton("Filtrar");
        btnNovo = new JButton("Novo");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir/Inativar");
        btnAtualizar = new JButton("Atualizar");

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Nome:"));
        topo.add(txtFiltroNome);
        topo.add(btnFiltrar);
        topo.add(btnAtualizar);
        topo.add(btnNovo);
        topo.add(btnEditar);
        topo.add(btnExcluir);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "CRM", "Telefone", "Especialidade", "Ativo"}, 0
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

        // Listeners
        btnFiltrar.addActionListener(e -> carregarTabela());
        btnAtualizar.addActionListener(e -> carregarTabela());
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnExcluir.addActionListener(e -> excluirSelecionado());
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        try {
            String filtroNome = txtFiltroNome.getText();
            List<Medico> medicos = medicoService.listarTodos(); // pode usar filtro depois

            for (Medico m : medicos) {
                tableModel.addRow(new Object[]{
                        m.getId(),
                        m.getNome(),
                        m.getCrm(),
                        m.getTelefone(),
                        m.getEspecialidade() != null ? m.getEspecialidade().getNome() : "",
                        m.isAtivo() ? "Sim" : "Não"
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar médicos: " + ex.getMessage(),
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

    private void abrirFormulario(Medico medico) {
        MedicoFormScreen form = new MedicoFormScreen(this, medico);
        form.setVisible(true);
        carregarTabela();
    }

    private void editarSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecione um médico.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Medico medico = medicoService.listarTodos()
                    .stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (medico == null) {
                JOptionPane.showMessageDialog(this,
                        "Médico não encontrado.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            abrirFormulario(medico);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar médico.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecione um médico.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opc = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir/inativar este médico?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (opc != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            medicoService.excluirOuInativar(id);
            carregarTabela();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir/inativar médico: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
