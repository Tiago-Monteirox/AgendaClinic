package br.edu.imepac.clinica.screens.especialidades;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.services.EspecialidadeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EspecialidadeListScreen extends BaseScreen {

    private final EspecialidadeService especialidadeService = new EspecialidadeService();

    private JTable tabela;
    private DefaultTableModel tableModel;

    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    public EspecialidadeListScreen() {
        initComponents();
        carregarTabela();
        centralizar();
    }

    private void initComponents() {
        setTitle("Cadastro de Especialidades");
        setSize(600, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tabela);

        btnNovo = new JButton("Novo");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        botoes.add(btnAtualizar);

        getContentPane().setLayout(new BorderLayout());
        add(botoes, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        configurarListeners();
    }

    private void configurarListeners() {
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnExcluir.addActionListener(e -> excluir());
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);

        try {
            List<Especialidade> lista = especialidadeService.listarTodos();
            for (Especialidade esp : lista) {
                tableModel.addRow(new Object[]{
                        esp.getId(),
                        esp.getNome()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar especialidades: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long getIdSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        return (Long) tabela.getValueAt(linha, 0);
    }

    private void abrirFormulario(Especialidade espEdicao) {
        EspecialidadeFormScreen form = new EspecialidadeFormScreen(this, espEdicao);
        form.setVisible(true);
        carregarTabela();
    }

    private void editarSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma especialidade para editar.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Especialidade esp = especialidadeService.listarTodos()
                    .stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (esp == null) {
                JOptionPane.showMessageDialog(this,
                        "Especialidade não encontrada.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            abrirFormulario(esp);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar especialidade: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma especialidade.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opc = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir esta especialidade?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opc != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            especialidadeService.excluirOuBloquear(id);
            carregarTabela();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Regra de negócio",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir especialidade: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
