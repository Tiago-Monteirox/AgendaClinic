package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.PerfilFuncionalidadeDao;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.enums.EnumFuncionalidade;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.services.AuthService;
import br.edu.imepac.clinica.session.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class LoginScreen extends BaseScreen {

    // Componentes de Interface
    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JTextField txtUsuario;
    private JLabel lblSenha;
    private JPasswordField pwdSenha;
    private JButton btnEntrar;
    private JLabel lblMensagemErro;

    private final AuthService authService = new AuthService();
    private final PerfilFuncionalidadeDao perfilFuncionalidadeDao = new PerfilFuncionalidadeDao();

    public LoginScreen() {
        initComponents();
        setupLayout();
        configurarListeners();
        centralizar(); // herdado de BaseScreen
    }

    private void initComponents() {
        lblTitulo = new JLabel("Clínica AgendaClinic");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        lblUsuario = new JLabel("Usuário:");
        txtUsuario = new JTextField(15);

        lblSenha = new JLabel("Senha:");
        pwdSenha = new JPasswordField(15);

        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setBackground(new Color(0, 102, 204));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));

        lblMensagemErro = new JLabel("");
        lblMensagemErro.setForeground(Color.RED);
        lblMensagemErro.setHorizontalAlignment(SwingConstants.CENTER);

        setTitle("Acesso ao Sistema");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Enter dispara o botão
        getRootPane().setDefaultButton(btnEntrar);
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Usuário
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblUsuario, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtUsuario, gbc);

        // Senha
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblSenha, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(pwdSenha, gbc);

        // Botão
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnEntrar, gbc);

        // Mensagem erro
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblMensagemErro, gbc);

        add(panel);
        pack();
    }

    private void configurarListeners() {
        btnEntrar.addActionListener(e -> realizarLogin());
    }

    public String getUsuario() {
        return txtUsuario.getText();
    }

    public char[] getSenha() {
        return pwdSenha.getPassword();
    }

    public void exibirMensagemErro(String mensagem) {
        lblMensagemErro.setText(mensagem);
    }

    private void realizarLogin() {
        exibirMensagemErro("");

        String usuario = getUsuario();
        char[] senhaChars = getSenha();
        String senha = new String(senhaChars);

        if (usuario == null || usuario.isBlank() || senha.isBlank()) {
            exibirMensagemErro("Informe usuário e senha.");
            return;
        }

        try {
            // 1) Autentica usuário
            Usuario usuarioAutenticado = authService.autenticar(usuario, senha);

            // DEBUG: ver perfilId
            Long perfilId = (usuarioAutenticado.getPerfil() != null)
                    ? usuarioAutenticado.getPerfil().getId()
                    : null;
            System.out.println(">> Usuario autenticado: " + usuarioAutenticado.getNomeLogin());
            System.out.println(">> PerfilId do usuário: " + perfilId);

            // 2) Carrega funcionalidades do perfil no banco
            Set<EnumFuncionalidade> permissoes = new HashSet<>();

            if (perfilId != null) {
                Set<EnumFuncionalidade> doBanco =
                        perfilFuncionalidadeDao.buscarFuncionalidadesPorPerfil(perfilId);
                System.out.println(">> Funcionalidades vindas do DAO: " + doBanco);
                permissoes.addAll(doBanco);
            }

            // 3) Preenche o contexto de sessão
            SessionContext ctx = SessionContext.getInstance();
            ctx.setUsuarioLogado(usuarioAutenticado);
            ctx.setPerfil(usuarioAutenticado.getPerfil());
            ctx.setFuncionalidades(permissoes);

            // Debug final
            System.out.println(">> SessionContext.isLogado(): " + ctx.isLogado());
            System.out.println(">> SessionContext.funcionalidades: " + ctx.getFuncionalidades());

            // 4) Abre menu principal
            exibirMensagemErro("");
            JOptionPane.showMessageDialog(
                    this,
                    "Bem-vindo(a), " + usuarioAutenticado.getNomeLogin() + "!",
                    "Login efetuado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            abrirMainMenu();
            dispose();

        } catch (ValidationException ve) {
            exibirMensagemErro(ve.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            exibirMensagemErro("Erro ao acessar o sistema. Tente novamente.");
        } finally {
            java.util.Arrays.fill(senhaChars, '\0');
        }
    }

    private void abrirMainMenu() {
        MainMenu menu = new MainMenu();
        menu.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
