package presentation;

import business.MemberBUS;
import data.*;
import model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import business.CheckInBUS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.Locale;



public class MemberDetailDialog extends JDialog {

    private final int memberID;

    private final MemberBUS memberBUS = new MemberBUS();
    private final MemberPackageDAO packageDAO = new MemberPackageDAO();
    private final MemberPTDAO memberPTDAO = new MemberPTDAO();
    private final PTServiceDAO ptServiceDAO = new PTServiceDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    /* ================= STYLE ================= */

    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Color BG_APP     = new Color(245, 247, 250);

    public MemberDetailDialog(Frame owner, int memberID) {
        super(owner, "Chi tiết hội viên #" + memberID, true);
        this.memberID = memberID;

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        applyGlobalStyle();

        add(createHeader(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
    }

    /* ================= GLOBAL STYLE ================= */

    private void applyGlobalStyle() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("TabbedPane.font", FONT_NORMAL);
    }

    /* ================= HEADER ================= */

    private JPanel createHeader() {
        Member m = memberBUS.getById(memberID);

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        p.setBackground(new Color(33, 150, 243));

        JLabel title = new JLabel("CHI TIẾT HỘI VIÊN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Họ tên: " + m.getFullName());
        sub.setFont(FONT_NORMAL);
        sub.setForeground(Color.WHITE);

        p.add(title, BorderLayout.NORTH);
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }

    /* ================= TABS ================= */

    private JTabbedPane createTabs() {
        JTabbedPane tab = new JTabbedPane();

        tab.addTab("Thông tin", wrap(createInfoTab()));
        tab.addTab("Gói tập", wrap(createPackageTab()));
        tab.addTab("PT", createPTTab());
        tab.addTab("Hóa đơn", createInvoiceTab());
        tab.addTab("Check-in", createCheckInHistoryTab());

        return tab;
    }

    /* ================= COMMON UI ================= */

    private JPanel wrap(JPanel content) {
        JPanel w = new JPanel(new GridBagLayout());
        w.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        w.setBackground(BG_APP);
        w.add(content);
        return w;
    }

    private JPanel cardBorder(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        if (title != null) {
            p.setBorder(BorderFactory.createTitledBorder(
                    p.getBorder(),
                    title,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    FONT_BOLD
            ));
        }
        return p;
    }

    /* ================= TAB: INFO ================= */

    private JPanel createInfoTab() {

        Member m = memberBUS.getById(memberID);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        /* ================= SUMMARY ================= */

        JPanel summary = new JPanel(new GridLayout(1, 3, 16, 0));
        summary.setOpaque(false);

        summary.add(infoCard("Mã hội viên", String.valueOf(m.getMemberID())));
        summary.add(infoCard("Giới tính", m.getGender()));
        summary.add(infoCard("Trạng thái", "ĐANG HOẠT ĐỘNG")); // UI only

        root.add(summary, BorderLayout.NORTH);

        /* ================= DETAIL ================= */

        JPanel detailCard = new JPanel(new GridBagLayout());
        detailCard.setBackground(Color.WHITE);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        // Row 1
        g.gridx = 0; g.gridy = 0;
        detailCard.add(detailLabel("Họ tên:"), g);
        g.gridx = 1;
        detailCard.add(detailValue(m.getFullName()), g);

        // Row 2
        g.gridx = 0; g.gridy = 1;
        detailCard.add(detailLabel("Điện thoại:"), g);
        g.gridx = 1;
        detailCard.add(detailValue(m.getPhoneNumber()), g);

        // Row 3
        g.gridx = 0; g.gridy = 2;
        detailCard.add(detailLabel("Ngày tham gia:"), g);
        g.gridx = 1;
        detailCard.add(detailValue(m.getJoinDate().toString()), g);

        root.add(detailCard, BorderLayout.CENTER);

        return root;
    }

    private JPanel infoCard(String label, String value) {
        JPanel c = new JPanel(new BorderLayout(4, 4));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));

        c.add(l, BorderLayout.NORTH);
        c.add(v, BorderLayout.CENTER);
        return c;
    }

    /* ================= TAB: PACKAGE ================= */

    private JPanel createPackageTab() {

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        MemberPackage pkg = packageDAO.getActiveByMember(memberID);

        if (pkg == null) {
            JLabel lbl = new JLabel("Hội viên chưa đăng ký gói tập", SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            root.add(lbl, BorderLayout.CENTER);
            return root;
        }

        /* ================= HEADER ================= */

        JLabel header = new JLabel("THÔNG TIN GÓI TẬP");
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        /* ================= SUMMARY CARD ================= */

        JPanel summaryGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        summaryGrid.setOpaque(false);

        summaryGrid.add(summaryCard("Mã gói", String.valueOf(pkg.getPackageID())));
        summaryGrid.add(summaryCard("Trạng thái", "ĐANG HOẠT ĐỘNG"));
        summaryGrid.add(summaryCard("Ngày bắt đầu", pkg.getStartDate().toString()));
        summaryGrid.add(summaryCard("Ngày hết hạn", pkg.getEndDate().toString()));

        JPanel summaryCardWrapper = new JPanel(new BorderLayout());
        summaryCardWrapper.setBackground(Color.WHITE);
        summaryCardWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        summaryCardWrapper.add(summaryGrid, BorderLayout.NORTH);

        /* ================= TOP AREA ================= */

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(summaryCardWrapper, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        /* ================= FILLER (để UI không cụt) ================= */

        root.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        return root;
    }


    /* ================= TAB: PT (FULL – ĐÃ SỬA UI) ================= */

    private JPanel createPTTab() {

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(BG_APP);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        MemberPT pt = memberPTDAO.getByMember(memberID);
        if (pt == null) {
            JLabel lbl = new JLabel("Hội viên chưa đăng ký PT", SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            root.add(lbl, BorderLayout.CENTER);
            return root;
        }

        PTService s = ptServiceDAO.findById(pt.getServiceID());

        /* ===== SUMMARY ===== */

        JPanel summary = new JPanel(new GridLayout(1, 4, 16, 0));
        summary.setOpaque(false);

        summary.add(summaryCard("Dịch vụ PT", s.getServiceName()));
        summary.add(summaryCard("PT", "ID: " + s.getTrainerID()));
        summary.add(summaryCard("Tổng buổi", String.valueOf(s.getTotalSessions())));
        summary.add(summaryCard("Đã dùng", String.valueOf(pt.getUsedSessions())));

        root.add(summary, BorderLayout.NORTH);

        /* ===== TABLE ===== */

        JPanel tableCard = cardBorder("Lịch tập PT");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ngày", "Bắt đầu", "Kết thúc", "PT", "Trạng thái"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        WorkoutScheduleDAO scheduleDAO = new WorkoutScheduleDAO();
        for (Object[] row : scheduleDAO.getByMemberPT(pt.getMemberPTID())) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(245, 246, 248));
        table.setGridColor(new Color(230, 230, 230));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(248, 248, 248));
                }
                return c;
            }
        });

        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(tableCard, BorderLayout.CENTER);

        return root;
    }

    private JPanel summaryCard(String title, String value) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    /* ================= TAB: INVOICE ================= */


    private JPanel createInvoiceTab() {

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Invoice> invoices = invoiceDAO.getByMember(memberID);

        BigDecimal total = BigDecimal.ZERO;
        for (Invoice i : invoices) total = total.add(i.getTotalAmount());

        JLabel summary = new JLabel(
                "Tổng hóa đơn: " + invoices.size() +
                        " | Tổng tiền: " +
                        NumberFormat
                                .getCurrencyInstance(new Locale("vi", "VN"))
                                .format(total)
        );
        summary.setFont(FONT_BOLD);
        root.add(summary, BorderLayout.NORTH);

        DefaultTableModel invoiceModel = new DefaultTableModel(
                new String[]{"ID", "Ngày lập", "Tổng tiền", "Nhân viên"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Invoice i : invoices) {
            invoiceModel.addRow(new Object[]{
                    i.getInvoiceID(),
                    i.getInvoiceDate(),
                    i.getTotalAmount(),
                    i.getStaffName()
            });
        }

        JTable invoiceTable = new JTable(invoiceModel);
        invoiceTable.setRowHeight(28);

        // ===== FORMATTER =====
        DateTimeFormatter dateFmt =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        NumberFormat moneyFmt =
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // ===== ID – CENTER =====
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        invoiceTable.getColumnModel().getColumn(0).setCellRenderer(center);

        // ===== NGÀY LẬP – DD/MM/YYYY =====
        invoiceTable.getColumnModel().getColumn(1)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    protected void setValue(Object value) {
                        if (value instanceof LocalDateTime) {
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setText(((LocalDateTime) value).format(dateFmt));
                        } else {
                            setText("");
                        }
                    }
                });

        // ===== TỔNG TIỀN – GIÁ TIỀN ĐẸP =====
        invoiceTable.getColumnModel().getColumn(2)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    protected void setValue(Object value) {
                        if (value instanceof BigDecimal) {
                            setHorizontalAlignment(SwingConstants.LEFT);
                            setText(moneyFmt.format(value));
                        } else {
                            setText("");
                        }
                    }
                });

        // ===== NHÂN VIÊN – LEFT =====
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);
        invoiceTable.getColumnModel().getColumn(3).setCellRenderer(left);

        JScrollPane leftPane = new JScrollPane(invoiceTable);

        DefaultTableModel detailModel = new DefaultTableModel(
                new String[]{"Dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable detailTable = new JTable(detailModel);
        JScrollPane rightPane = new JScrollPane(detailTable);

        InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();

        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row < 0) return;

            int invoiceID =
                    Integer.parseInt(invoiceTable.getValueAt(row, 0).toString());
            detailModel.setRowCount(0);

            for (InvoiceDetail d : detailDAO.getByInvoice(invoiceID)) {
                BigDecimal lineTotal =
                        d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()));

                detailModel.addRow(new Object[]{
                        d.getItemName(),
                        d.getQuantity(),
                        moneyFmt.format(d.getPrice()),
                        moneyFmt.format(lineTotal)
                });
            }
        });

        JSplitPane split =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
        split.setDividerLocation(420);

        root.add(split, BorderLayout.CENTER);
        return root;
    }


    private JPanel createCheckInHistoryTab() {

        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(BG_APP);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        /* ================= HEADER ================= */

        JLabel title = new JLabel("CHECK-IN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Lịch sử ra vào của hội viên");
        subtitle.setFont(FONT_NORMAL);
        subtitle.setForeground(Color.GRAY);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);

        /* ================= DATA (UI → BUS) ================= */

        Member member = memberBUS.getById(memberID);
        CheckInBUS checkInBUS = new CheckInBUS();

        List<CheckIn> history =
                checkInBUS.getCheckInHistory(member.getPhoneNumber());

        int total =
                checkInBUS.getTotalCheckIn(member.getPhoneNumber());

        LocalDateTime latest =
                checkInBUS.getLatestCheckIn(member.getPhoneNumber());

        DateTimeFormatter dateFmt =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt =
                DateTimeFormatter.ofPattern("HH:mm");

        /* ================= CONTENT ================= */

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setOpaque(false);
        root.add(content, BorderLayout.CENTER);

        /* ================= SUMMARY ================= */

        JPanel summary = new JPanel(new GridLayout(1, 2, 20, 0));
        summary.setOpaque(false);

        summary.add(summaryCard(
                "Tổng lượt check-in",
                String.valueOf(total)
        ));

        summary.add(summaryCard(
                "Lần gần nhất",
                latest == null
                        ? "Chưa có"
                        : latest.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ));

        content.add(summary, BorderLayout.NORTH);

        /* ================= TABLE ================= */

        JPanel tableCard = cardBorder("LỊCH SỬ CHECK-IN");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ngày", "Giờ check-in"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (CheckIn ci : history) {
            model.addRow(new Object[]{
                    ci.getCheckInTime().format(dateFmt),
                    ci.getCheckInTime().format(timeFmt)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.getTableHeader().setBackground(new Color(245, 246, 248));
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(230, 230, 230));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        tableCard.add(scroll, BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);

        return root;
    }


    /* ================= UI HELPERS ================= */

    private JLabel detailLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.GRAY);
        return l;
    }

    private JLabel detailValue(String text) {
        JLabel v = new JLabel(text);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return v;
    }

}
