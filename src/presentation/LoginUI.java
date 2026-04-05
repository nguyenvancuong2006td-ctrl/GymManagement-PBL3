package presentation;

import business.AccountBUS;
import model.Account;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnClear;

    private char defaultEchoChar;
    private final AccountBUS accountBUS = new AccountBUS();

    public LoginUI() {
        initUI();
        initEvent();
    }

    // ================= UI =================
    private void initUI() {

        setTitle("Gym Management System - Login");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        /* ---------- LEFT PANEL ---------- */
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 123, 255),
                        0, getHeight(), new Color(0, 200, 160)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        left.setBounds(0, 0, 400, 500);
        left.setLayout(null);

        JLabel title = new JLabel("GYM SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBounds(90, 180, 300, 40);

        JLabel sub = new JLabel("Train Hard - Stay Strong");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(240, 240, 240));
        sub.setBounds(95, 220, 300, 30);

        left.add(title);
        left.add(sub);
        add(left);

        /* ---------- RIGHT PANEL ---------- */
        JPanel right = new JPanel(null);
        right.setBounds(400, 0, 500, 500);
        right.setBackground(Color.WHITE);
        add(right);

        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogin.setForeground(new Color(20, 90, 160));
        lblLogin.setBounds(190, 40, 200, 40);
        right.add(lblLogin);

        // Username
        right.add(createLabel("Username", 80, 120));
        txtUsername = createTextField(80, 145);
        right.add(txtUsername);

        // Password
        right.add(createLabel("Password", 80, 200));
        txtPassword = createPasswordField(80, 225);
        right.add(txtPassword);

        defaultEchoChar = txtPassword.getEchoChar();

        // Show password
        chkShowPassword = new JCheckBox(" Hiện mật khẩu");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.setBounds(80, 270, 200, 25);
        right.add(chkShowPassword);

        // Buttons
        btnLogin = createButton("LOGIN", 80, 320);
        btnClear = createButton("CLEAR", 260, 320);
        right.add(btnLogin);
        right.add(btnClear);

        getRootPane().setDefaultButton(btnLogin);
        setVisible(true);
    }

    // ================= EVENT =================
    private void initEvent() {

        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
                chkShowPassword.setText(" Ẩn mật khẩu");
            } else {
                txtPassword.setEchoChar(defaultEchoChar);
                chkShowPassword.setText(" Hiện mật khẩu");
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnLogin.addActionListener(e -> handleLogin());

        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    // ================= LOGIN LOGIC =================
    private void handleLogin() {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ!");
            return;
        }

        try {
            Account acc = accountBUS.login(username, password);

            if (acc == null) {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
                return;
            }

            // check trạng thái account
            if (!acc.getStatus().equalsIgnoreCase("Active")) {
                JOptionPane.showMessageDialog(this, "Tài khoản đã bị khóa!");
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome " + acc.getUsername() + " (" + acc.getRole() + ")",
                    "Login Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            //  MỞ DASHBOARD + TRUYỀN ACCOUNT
           MainFrame mainframe = new MainFrame(acc);
            mainframe.setVisible(true);
            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    // ================= UI HELPERS =================
    private JLabel createLabel(String text, int x, int y) {
        JLabel lb = new JLabel(text);
        lb.setBounds(x, y, 200, 20);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lb.setForeground(new Color(60, 60, 60));
        return lb;
    }

    private JTextField createTextField(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 340, 40);
        tf.setBackground(new Color(245, 247, 250));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    private JPasswordField createPasswordField(int x, int y) {
        JPasswordField pf = new JPasswordField();
        pf.setBounds(x, y, 340, 40);
        pf.setBackground(new Color(245, 247, 250));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return pf;
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 160, 45);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }
}