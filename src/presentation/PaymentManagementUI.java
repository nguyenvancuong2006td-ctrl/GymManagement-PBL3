package presentation;

import business.InvoiceBUS;
import model.Invoice;
import util.RoundedButton;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import com.github.lgooddatepicker.components.DatePicker;

public class PaymentManagementUI extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;

    private DatePicker spFromDate;
    private DatePicker spToDate;

    private final InvoiceBUS invoiceBUS = new InvoiceBUS();
    private List<Invoice> allInvoices;

    public PaymentManagementUI() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));

        initUI();
        loadData();
        initRealtime();
    }

    /* ================= UI ================= */

    private void initUI() {

        JLabel title = new JLabel("QUẢN LÝ THANH TOÁN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(Color.WHITE);
        add(card, BorderLayout.CENTER);

        /* ===== SEARCH ===== */
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        searchPanel.setBackground(Color.WHITE);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePanel.setBackground(Color.WHITE);

        spFromDate = createDatePicker();
        spToDate = createDatePicker();

        datePanel.add(new JLabel("Từ:"));
        datePanel.add(spFromDate);
        datePanel.add(new JLabel("Đến:"));
        datePanel.add(spToDate);

        JPanel quickPanel = new JPanel(new BorderLayout(6, 0));
        quickPanel.setBackground(Color.WHITE);

        txtSearch = new JTextField();

        quickPanel.add(new JLabel("Tìm nhanh:"), BorderLayout.WEST);
        quickPanel.add(txtSearch, BorderLayout.CENTER);

        searchPanel.add(datePanel);
        searchPanel.add(quickPanel);

        card.add(searchPanel, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(
                new String[]{"Mã HD", "Ngày", "Nhân viên", "Tổng tiền"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        styleTable();
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== ACTIONS ===== */
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnDetail = new RoundedButton("Xem chi tiết", new Color(52, 152, 219));
        JButton btnExport = new RoundedButton("Xuất PDF", new Color(46, 204, 113));

        btnDetail.addActionListener(e -> viewDetail());
        btnExport.addActionListener(e -> exportPDF());

        actions.add(btnDetail);
        actions.add(btnExport);

        card.add(actions, BorderLayout.SOUTH);
    }

    /* ================= DATA ================= */

    private void loadData() {
        allInvoices = invoiceBUS.getAllInvoices();

        // default: 1 tháng gần nhất
        spFromDate.setDate(LocalDate.now().minusMonths(1));
        spToDate.setDate(LocalDate.now());

        filterAndShow();
    }

    /* ================= FILTER ================= */

    private void filterAndShow() {

        model.setRowCount(0);

        if (allInvoices == null) return;

        LocalDate fromDate = spFromDate.getDate();
        LocalDate toDate = spToDate.getDate();

        if (fromDate == null) fromDate = LocalDate.MIN;
        if (toDate == null) toDate = LocalDate.MAX;

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(23, 59, 59);

        String keyword = txtSearch.getText().trim().toLowerCase();

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Invoice i : allInvoices) {

            LocalDateTime time = i.getInvoiceDate()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            boolean okDate =
                    !time.isBefore(from) &&
                            !time.isAfter(to);

            boolean okKey =
                    keyword.isEmpty()
                            || String.valueOf(i.getInvoiceID()).contains(keyword)
                            || i.getStaffName().toLowerCase().contains(keyword);

            if (okDate && okKey) {

                model.addRow(new Object[]{
                        i.getInvoiceID(),
                        fmt.format(java.util.Date.from(
                                time.atZone(ZoneId.systemDefault()).toInstant()
                        )),
                        i.getStaffName(),
                        money.format(i.getTotalAmount())
                });
            }
        }
    }

    /* ================= REALTIME ================= */

    private void initRealtime() {

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterAndShow(); }
            public void removeUpdate(DocumentEvent e) { filterAndShow(); }
            public void changedUpdate(DocumentEvent e) { filterAndShow(); }
        });

        spFromDate.addDateChangeListener(e -> filterAndShow());
        spToDate.addDateChangeListener(e -> filterAndShow());
    }

    /* ================= PICKER ================= */

    private DatePicker createDatePicker() {

        DatePicker picker = new DatePicker();
        picker.getSettings().setFormatForDatesCommonEra("dd/MM/yyyy");
        picker.getComponentToggleCalendarButton().setText("Chọn");
        picker.setPreferredSize(new Dimension(140, 30));

        return picker;
    }

    /* ================= TABLE STYLE ================= */

    private void styleTable() {

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        table.setGridColor(new Color(235, 235, 235));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
    }

    /* ================= ACTION ================= */

    private void viewDetail() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = (int) table.getValueAt(row, 0);

        new InvoiceDetailDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                id
        ).setVisible(true);
    }

    private void exportPDF() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = (int) table.getValueAt(row, 0);
        util.InvoicePDFExporter.export(id);
    }
}