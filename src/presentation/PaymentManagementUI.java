package presentation;

import business.InvoiceBUS;
import model.Invoice;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class PaymentManagementUI extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JSpinner spFromDate, spToDate;

    private final InvoiceBUS invoiceBUS = new InvoiceBUS();
    private List<Invoice> allInvoices = new ArrayList<>();

    public PaymentManagementUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        initUI();
        loadData();
        initSearchRealtime();
    }

    /* ================= UI ================= */

    private void initUI() {

        JLabel title = new JLabel("QUẢN LÝ THANH TOÁN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(card, BorderLayout.CENTER);

        /* ===== SEARCH PANEL ===== */
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        searchPanel.setBackground(Color.WHITE);

        // ---- Lọc theo ngày ----
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePanel.setBackground(Color.WHITE);

        spFromDate = createDateSpinner();
        spToDate = createDateSpinner();

        datePanel.add(new JLabel("Từ ngày:"));
        datePanel.add(spFromDate);
        datePanel.add(new JLabel("Đến ngày:"));
        datePanel.add(spToDate);

        // ---- Tìm nhanh ----
        JPanel quickPanel = new JPanel(new BorderLayout(6, 0));
        quickPanel.setBackground(Color.WHITE);

        JLabel lblSearch = new JLabel("Tìm nhanh:");
        txtSearch = new JTextField();

        JLabel lblHint = new JLabel("Mã hóa đơn hoặc Tên nhân viên");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);

        quickPanel.add(lblSearch, BorderLayout.WEST);
        quickPanel.add(txtSearch, BorderLayout.CENTER);
        quickPanel.add(lblHint, BorderLayout.SOUTH);

        searchPanel.add(datePanel);
        searchPanel.add(quickPanel);

        card.add(searchPanel, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(
                new String[]{"Mã hóa đơn", "Ngày lập", "Nhân viên", "Tổng tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        card.add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== ACTIONS ===== */
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnDetail = new JButton("Xem chi tiết");
        JButton btnExport = new JButton("Xuất hóa đơn PDF");

        btnDetail.addActionListener(e -> viewDetail());
        btnExport.addActionListener(e -> exportPDF());

        actions.add(btnDetail);
        actions.add(btnExport);
        card.add(actions, BorderLayout.SOUTH);
    }

    /* ================= DATA ================= */

    private void loadData() {
        allInvoices = invoiceBUS.getAllInvoices();
        initDefaultDateRange();   // ✅ SET NGÀY MẶC ĐỊNH THÔNG MINH
        filterAndShow();
    }

    /**
     * Từ ngày = ngày tạo hóa đơn đầu tiên
     * Đến ngày = hôm nay
     */
    private void initDefaultDateRange() {

        if (allInvoices.isEmpty()) {
            Date now = new Date();
            spFromDate.setValue(now);
            spToDate.setValue(now);
            return;
        }

        Date minDate = allInvoices.stream()
                .map(i -> Date.from(
                        i.getInvoiceDate()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                ))
                .min(Date::compareTo)
                .orElse(new Date());

        spFromDate.setValue(minDate);
        spToDate.setValue(new Date());
    }

    private void filterAndShow() {

        model.setRowCount(0);

        // ===== CHUẨN HÓA NGÀY =====
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime((Date) spFromDate.getValue());
        calFrom.set(Calendar.HOUR_OF_DAY, 0);
        calFrom.set(Calendar.MINUTE, 0);
        calFrom.set(Calendar.SECOND, 0);
        calFrom.set(Calendar.MILLISECOND, 0);

        Calendar calTo = Calendar.getInstance();
        calTo.setTime((Date) spToDate.getValue());
        calTo.set(Calendar.HOUR_OF_DAY, 23);
        calTo.set(Calendar.MINUTE, 59);
        calTo.set(Calendar.SECOND, 59);
        calTo.set(Calendar.MILLISECOND, 999);

        Date fromDate = calFrom.getTime();
        Date toDate = calTo.getTime();

        String keyword = txtSearch.getText().trim().toLowerCase();

        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Invoice i : allInvoices) {

            Date invoiceDate = Date.from(
                    i.getInvoiceDate()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            );

            boolean matchDate =
                    !invoiceDate.before(fromDate)
                            && !invoiceDate.after(toDate);

            boolean matchKeyword =
                    keyword.isEmpty()
                            || String.valueOf(i.getInvoiceID()).contains(keyword)
                            || i.getStaffName().toLowerCase().contains(keyword);

            if (matchDate && matchKeyword) {
                model.addRow(new Object[]{
                        i.getInvoiceID(),
                        dateFmt.format(invoiceDate),
                        i.getStaffName(),
                        moneyFmt.format(i.getTotalAmount())
                });
            }
        }
    }

    /* ================= REALTIME ================= */

    private void initSearchRealtime() {

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterAndShow(); }
            public void removeUpdate(DocumentEvent e) { filterAndShow(); }
            public void changedUpdate(DocumentEvent e) { filterAndShow(); }
        });

        spFromDate.addChangeListener(e -> filterAndShow());
        spToDate.addChangeListener(e -> filterAndShow());
    }

    private JSpinner createDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel());
        sp.setEditor(new JSpinner.DateEditor(sp, "dd/MM/yyyy"));
        return sp;
    }

    /* ================= ACTIONS ================= */

    private void viewDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn");
            return;
        }

        int invoiceID = (int) table.getValueAt(row, 0);
        new InvoiceDetailDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                invoiceID
        ).setVisible(true);
    }

    private void exportPDF() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn");
            return;
        }

        int invoiceID = (int) table.getValueAt(row, 0);
        util.InvoicePDFExporter.export(invoiceID);
    }
}
