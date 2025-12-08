package br.edu.imepac.clinica.screens.convenios;

import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.screens.BaseScreen;
import br.edu.imepac.clinica.services.ConvenioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ConvenioListScreen extends BaseScreen {

    private final ConvenioService convenioService = new ConvenioService();

    private JTable tabela;
    private DefaultTableModel tableModel;

    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    public ConvenioListScreen() {
        initComponents();
        carregarTabela();
        centralizar();
    }

    private void initComponents() {
        setTitle("Cadastro de Convênios");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Código", "Ativo"}, 0
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
        btnExcluir = new JButton("Excluir/Inativar");
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
        btnExcluir.addActionListener(e -> excluirOuInativar());
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);

        try {
            List<Convenio> convenios = convenioService.listarTodos();
            for (Convenio c : convenios) {
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getNome(),
                        c.getCodigo(),
                        c.isAtivo() ? "Sim" : "Não"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar convênios: " + ex.getMessage(),
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

    private void abrirFormulario(Convenio convenioEdicao) {
        ConvenioFormScreen form = new ConvenioFormScreen(this, convenioEdicao);
        form.setVisible(true);
        carregarTabela();
    }

    private void editarSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um convênio para editar.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Convenio c = convenioService.listarTodos()
                    .stream()
                    .filter(conv -> conv.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (c == null) {
                JOptionPane.showMessageDialog(this,
                        "Convênio não encontrado.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            abrirFormulario(c);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar convênio: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirOuInativar() {
        Long id = getIdSelecionado();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um convênio.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opc = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir/inativar este convênio?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opc != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            convenioService.excluirOuInativar(id);
            carregarTabela();
        } catch (ValidationException ve) {
            // mensagem de regra de negócio (ex.: marcado como inativo)
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            carregarTabela();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir/inativar convênio: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
