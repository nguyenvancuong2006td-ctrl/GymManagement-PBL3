package presentation;

import model.Account;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel sidebar, content, header;
    private Account account;

    private JButton btnDashboard,btnStaff, btnMember, btnPackage,
            btnSchedule, btnPT, btnProduct,
            btnPayment, btnReport, btnSystem;

    private JButton activeButton = null;

    public MainFrame(Account acc) {
        this.account = acc;
        init();
        applyPermission();
    }

    private void init() {
        setTitle("Gym Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initHeader();
        initSidebar();
        initContent();
        initEvents();
    }

    /* ================= HEADER ================= */
    private void initHeader() {
        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 55));
        header.setBackground(new Color(52, 120, 208));

        JLabel title = new JLabel("  GYM MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    /* ================= SIDEBAR ================= */
    private void initSidebar() {
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(245, 247, 250));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(15));

        btnDashboard = createMenuButton("Dashboard");
        btnStaff = createMenuButton("Quản lý nhân viên");
        btnMember = createMenuButton("Quản lý hội viên");
        btnPackage = createMenuButton("Quản lý gói tập");
        btnSchedule = createMenuButton("Quản lý lịch tập");
        btnPT = createMenuButton("Quản lý PT");
        btnProduct = createMenuButton("Quản lý sản phẩm");
        btnPayment = createMenuButton("Quản lý thanh toán");
        btnReport = createMenuButton("Thống kê báo cáo");
        btnSystem = createMenuButton("Quản lý hệ thống");

        sidebar.add(btnDashboard);
        sidebar.add(btnStaff);
        sidebar.add(btnMember);
        sidebar.add(btnPackage);
        sidebar.add(btnSchedule);
        sidebar.add(btnPT);
        sidebar.add(btnProduct);
        sidebar.add(btnPayment);
        sidebar.add(btnReport);
        sidebar.add(btnSystem);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createMenuButton("Đăng xuất");
        logoutBtn.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });

        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(10));

        add(sidebar, BorderLayout.WEST);
    }

    /* ================= CONTENT ================= */
    private void initContent() {
        content = new JPanel(new BorderLayout());
        content.setBackground(new Color(240, 242, 245));

        JLabel welcome = new JLabel(
                "Welcome " + account.getUsername() + " (" + account.getRole() + ")",
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel card = createCardPanel();
        card.add(welcome, BorderLayout.CENTER);

        content.add(card, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            setActiveButton(btnDashboard);
            content.removeAll();
            content.add(new DashboardUI(), BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });
    }

    /* ================= EVENTS ================= */
    private void initEvents() {

        btnDashboard.addActionListener(e -> {
            setActiveButton(btnDashboard);
            content.removeAll();
            content.add(new DashboardUI(), BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });

        btnStaff.addActionListener(e -> {
            setActiveButton(btnStaff);
            content.removeAll();
            content.add(new StaffUI(), BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });

        btnMember.addActionListener(e -> {
            setActiveButton(btnMember);
            content.removeAll();
            content.add(new MemberUI(account.getRole()), BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });

        btnPackage.addActionListener(e -> {
            setActiveButton(btnPackage);
            showText("Quản lý gói tập");
        });

        btnSchedule.addActionListener(e -> {
            setActiveButton(btnSchedule);
            showText("Quản lý lịch tập");
        });

        btnPT.addActionListener(e -> {
            setActiveButton(btnPT);
            showText("Quản lý PT");
        });

        btnProduct.addActionListener(e -> {
            setActiveButton(btnProduct);
            showText("Quản lý sản phẩm");
        });

        btnPayment.addActionListener(e -> {
            setActiveButton(btnPayment);
            showText("Quản lý thanh toán");
        });

        btnReport.addActionListener(e -> {
            setActiveButton(btnReport);
            showText("Thống kê báo cáo");
        });

        btnSystem.addActionListener(e -> {
            setActiveButton(btnSystem);
            showText("Quản lý hệ thống");
        });
    }

    /* ================= ACTIVE BUTTON ================= */
    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(245, 247, 250));
            activeButton.setForeground(new Color(60, 60, 60));
            activeButton.setBorder(new RoundedBorder(10, new Color(210, 215, 220)));
        }

        btn.setBackground(new Color(52, 120, 208));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(30, 90, 180)),
                new RoundedBorder(10, new Color(52, 120, 208))
        ));

        activeButton = btn;
    }

    /* ================= SHOW TEXT ================= */
    private void showText(String text) {
        content.removeAll();

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel card = createCardPanel();
        card.add(label, BorderLayout.CENTER);

        content.add(card, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    /* ================= PERMISSION ================= */
    private void applyPermission() {
        if (account.getRole().equalsIgnoreCase("Staff")) {
            btnReport.setVisible(false);
            btnSystem.setVisible(false);
            btnStaff.setVisible(false);
        }
    }

    /* ================= UI ================= */
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(245, 247, 250));
        btn.setForeground(new Color(60, 60, 60));
        btn.setBorder(new RoundedBorder(10, new Color(210, 215, 220)));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) {
                    btn.setBackground(new Color(220, 230, 245));
                    btn.setBorder(new RoundedBorder(10, new Color(120, 160, 220)));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) {
                    btn.setBackground(new Color(245, 247, 250));
                    btn.setBorder(new RoundedBorder(10, new Color(210, 215, 220)));
                }
            }
        });

        return btn;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }
}

/* ================= ROUNDED BORDER ================= */
class RoundedBorder implements Border {

    private final int radius;
    private final Color color;

    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(6, 12, 6, 12);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }
}