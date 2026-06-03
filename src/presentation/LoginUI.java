package presentation;

import business.AccountBUS;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnClear;
    private JLabel lblForgot;

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

            private final Image gymImage =
                    new ImageIcon("images/gym.jpg")
                            .getImage();

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // Vẽ ảnh nền
                g2.drawImage(
                        gymImage, 0, 0, getWidth(), getHeight(), this);
                // Overlay tối
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Hiệu ứng glow
                g2.setColor(new Color(0, 255, 180, 40));
                g2.fillOval(-100, 100, 350, 350);
            }
        };
        left.setBounds(0, 0, 400, 500);
        left.setLayout(null);

        JLabel title = new JLabel("GYM SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(20, 180, 360, 50);

        JLabel sub = new JLabel("Train Hard - Stay Strong");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        sub.setForeground(new Color(0, 255, 180));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setBounds(20, 230, 360, 30);

        left.add(title);
        left.add(sub);
        add(left);

        /* ---------- RIGHT PANEL ---------- */
        JPanel right = new JPanel(null);
        right.setBounds(400, 0, 500, 500);
        right.setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblLogin.setForeground(new Color(20, 90, 160));
        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogin.setBounds(100, 40, 300, 40);
        right.add(lblLogin);

        JLabel lblWelcome = new JLabel("Welcome Back!");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(new Color(120, 120, 120));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(100, 80, 300, 25);
        right.add(lblWelcome);

        // ===== SEPARATOR =====
        JSeparator separator = new JSeparator();
        separator.setBounds(80, 115, 340, 1);
        right.add(separator);

        // ===== USERNAME =====
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsername.setBounds(80, 130, 100, 25);
        right.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(80, 160, 340, 40);
        txtUsername.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );
        right.add(txtUsername);

        // ===== PASSWORD =====
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setBounds(80, 220, 100, 25);
        right.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(80, 250, 340, 40);
        txtPassword.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );
        right.add(txtPassword);

        defaultEchoChar = txtPassword.getEchoChar();

        // ===== SHOW PASSWORD =====
        chkShowPassword = new JCheckBox("Hiện mật khẩu");
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkShowPassword.setBounds(80, 300, 130, 25);
        right.add(chkShowPassword);

        // ===== FORGOT PASSWORD =====
        lblForgot = new JLabel("<html><u>Quên mật khẩu?</u></html>");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblForgot.setForeground(new Color(20, 90, 160));
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.setBounds(300, 302, 120, 25);

        right.add(lblForgot);


        // Buttons
        btnLogin = createButton("LOGIN", 80, 360);
        btnClear = createButton("CLEAR", 260, 360);
        right.add(btnLogin);
        right.add(btnClear);

        add(right);
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

        lblForgot.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                lblForgot.setForeground(new Color(0, 120, 215));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblForgot.setForeground(new Color(20, 90, 160));
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                JOptionPane.showMessageDialog(
                        LoginUI.this,
                        """
                        Bạn quên mật khẩu?
        
                        Vui lòng liên hệ quản trị viên hệ thống để được cấp lại mật khẩu.
        
                        Email: admin@gymsystem.com
                        Hotline: 0383 680 402
                        """,
                        "Khôi phục mật khẩu",
                        JOptionPane.INFORMATION_MESSAGE
                );
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


    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 160, 45);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        if(text.equals("LOGIN")) {
            btn.setBackground(new Color(20, 90, 160));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(240,240,240));
            btn.setForeground(Color.DARK_GRAY);
        }

        btn.setFocusPainted(false);

        return btn;
    }
}