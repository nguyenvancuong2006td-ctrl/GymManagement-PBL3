package presentation;

import model.Account;
import model.Permission;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private JPanel sidebar, content, header;
    private Account account;

    private JButton btnDashboard, btnStaff, btnMember, btnPackage,
            btnSchedule, btnPT, btnProduct,btnProductShop,
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
        btnProductShop = createMenuButton("Product Shop");
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
        sidebar.add(btnProductShop);
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

        SwingUtilities.invokeLater(() ->
                openPanel(btnDashboard, new DashboardUI())
        );
    }

    /* ================= OPEN PANEL ================= */

    private void openPanel(JButton btn, JPanel panel) {
        setActiveButton(btn);
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    /* ================= EVENTS ================= */

    private void initEvents() {

        btnDashboard.addActionListener(e ->
                openPanel(btnDashboard, new DashboardUI())
        );

        btnStaff.addActionListener(e -> {
            if (!account.hasPermission(Permission.STAFF_VIEW)) {
                deny(); return;
            }
            openPanel(btnStaff, new StaffUI());
        });

        btnMember.addActionListener(e -> {
            if (!account.hasPermission(Permission.MEMBER_VIEW)) {
                deny(); return;
            }
            openPanel(btnMember, new MemberUI());
        });

        btnPackage.addActionListener(e -> {
            if (!account.hasPermission(Permission.PACKAGE_VIEW)) {
                deny(); return;
            }
            openPanel(btnPackage, new MembershipPackageUI());
        });

        btnSchedule.addActionListener(e -> {
            if (!account.hasPermission(Permission.SCHEDULE_VIEW)) {
                deny(); return;
            }
            openPanel(btnSchedule, new WorkoutScheduleUI());
            setActiveButton(btnSchedule);
        });

        btnPT.addActionListener(e -> {
            if (!account.hasPermission(Permission.TRAINER_VIEW)) {
                deny(); return;
            }
            openPanel(btnPT, new TrainerUI());
        });

        btnProduct.addActionListener(e -> {
            if (!account.hasPermission(Permission.PRODUCT_MANAGE)) {
                deny(); return;
            }
            openPanel(btnProduct, new ProductManagementUI());
            setActiveButton(btnProduct);
        });


        btnProductShop.addActionListener(e -> {
            if (!account.hasPermission(Permission.PRODUCT_SHOP_VIEW)) {
                deny();
                return;
            }
            openPanel(btnProductShop, new ProductShopUI());
        });



        btnPayment.addActionListener(e -> {
            if (!account.hasPermission(Permission.PAYMENT_VIEW)) {
                deny();
                return;
            }
            openPanel(btnPayment, new PaymentManagementUI());
        });


        btnReport.addActionListener(e -> {
            if (!account.hasPermission(Permission.REPORT_VIEW)) {
                deny(); return;
            }
            showText("Thống kê báo cáo");
            setActiveButton(btnReport);
        });

        btnSystem.addActionListener(e -> {
            if (!account.hasPermission(Permission.ACCOUNT_MANAGE)) {
                deny(); return;
            }
            showText("Quản lý hệ thống");
            setActiveButton(btnSystem);
        });
    }

    /* ================= PERMISSION UI ================= */

    private void applyPermission() {
        btnDashboard.setVisible(account.hasPermission(Permission.DASHBOARD_VIEW));
        btnStaff.setVisible(account.hasPermission(Permission.STAFF_VIEW));
        btnMember.setVisible(account.hasPermission(Permission.MEMBER_VIEW));
        btnPackage.setVisible(account.hasPermission(Permission.PACKAGE_VIEW));
        btnSchedule.setVisible(account.hasPermission(Permission.SCHEDULE_VIEW));
        btnPT.setVisible(account.hasPermission(Permission.TRAINER_VIEW));
        btnProduct.setVisible(account.hasPermission(Permission.PRODUCT_MANAGE));
        btnProductShop.setVisible(account.hasPermission(Permission.PRODUCT_SHOP_VIEW));
        btnPayment.setVisible(account.hasPermission(Permission.PAYMENT_VIEW));
        btnReport.setVisible(account.hasPermission(Permission.REPORT_VIEW));
        btnSystem.setVisible(account.hasPermission(Permission.ACCOUNT_MANAGE));
    }

    /* ================= HELPERS ================= */

    private void deny() {
        JOptionPane.showMessageDialog(
                this,
                "Bạn không có quyền truy cập chức năng này!",
                "Từ chối truy cập",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(245, 247, 250));
            activeButton.setForeground(new Color(60, 60, 60));
            activeButton.setBorder(new RoundedBorder(12, new Color(220, 220, 220)));
        }

        btn.setBackground(new Color(52, 120, 208));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new RoundedBorder(12, new Color(52, 120, 208)));
        activeButton = btn;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(245, 247, 250));
        btn.setForeground(new Color(60, 60, 60));
        btn.setBorder(new RoundedBorder(12, new Color(220, 220, 220)));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(new Color(230, 235, 240));
            }

            public void mouseExited(MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(new Color(245, 247, 250));
            }
        });

        return btn;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    private void showText(String text) {
        content.removeAll();
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        content.add(label, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }
}
