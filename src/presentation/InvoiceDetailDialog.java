package presentation;

import business.InvoiceBUS;
import model.InvoiceDetail;
import model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceDetailDialog extends JDialog {

    private final InvoiceBUS bus = new InvoiceBUS();

    public InvoiceDetailDialog(JFrame parent, int invoiceID) {
        super(parent, "Chi tiết hóa đơn #" + invoiceID, true);
        setSize(850, 560);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(invoiceID), BorderLayout.NORTH);
        add(createCenter(invoiceID), BorderLayout.CENTER);
        add(createFooter(invoiceID), BorderLayout.SOUTH);
    }

    /* ================= HEADER ================= */
    private JPanel createHeader(int invoiceID) {
        JPanel header = new JPanel(new GridLayout(2, 4, 10, 5));
        header.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        header.add(new JLabel("Mã hóa đơn:"));
        header.add(new JLabel("#" + invoiceID));

        header.add(new JLabel("Ngày lập:"));
        header.add(new JLabel(bus.getInvoiceDate(invoiceID)));

        header.add(new JLabel("Nhân viên:"));
        header.add(new JLabel(bus.getStaffName(invoiceID)));

        header.add(new JLabel("Khách hàng:"));
        header.add(new JLabel(bus.getMemberName(invoiceID)));

        return header;
    }

    /* ================= CENTER ================= */
    private JPanel createCenter(int invoiceID) {
        JPanel center = new JPanel(new BorderLayout(10, 10));

        /* ===== TABLE SẢN PHẨM ===== */
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"STT", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0
        );

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        List<InvoiceDetail> details = bus.getInvoiceDetails(invoiceID);

        int stt = 1;
        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceDetail d : details) {
            BigDecimal lineTotal =
                    d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
            total = total.add(lineTotal);

            model.addRow(new Object[]{
                    stt++,
                    bus.getProductName(d.getProductID()),
                    d.getQuantity(),
                    formatMoney(d.getPrice()),
                    formatMoney(lineTotal)
            });
        }

        center.add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== PAYMENT ===== */
        center.add(createPaymentPanel(invoiceID), BorderLayout.SOUTH);

        return center;
    }

    /* ================= PAYMENT INFO ================= */
    private JPanel createPaymentPanel(int invoiceID) {

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));

        List<Payment> payments = bus.getPayments(invoiceID);

        if (payments.isEmpty()) {
            panel.add(new JLabel("Chưa có thanh toán"));
            panel.add(new JLabel(""));
            return panel;
        }

        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Payment p : payments) {
            panel.add(new JLabel("Phương thức:"));
            panel.add(new JLabel(p.getPaymentMethod()));

            panel.add(new JLabel("Số tiền:"));
            panel.add(new JLabel(formatMoney(p.getAmount())));

            panel.add(new JLabel("Trạng thái:"));
            panel.add(new JLabel(p.getStatus()));

            if (p.getPaymentDate() != null) {
                panel.add(new JLabel("Thời gian:"));
                panel.add(new JLabel(p.getPaymentDate().format(fmt)));
            }
        }

        return panel;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter(int invoiceID) {

        BigDecimal total = bus.getInvoiceTotal(invoiceID);

        JPanel footer = new JPanel(new BorderLayout());

        JLabel lblTotal = new JLabel("TỔNG CỘNG: " + formatMoney(total));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPDF = new JButton("Xuất hóa đơn PDF");
        JButton btnClose = new JButton("Đóng");

        btnPDF.addActionListener(e ->
                util.InvoicePDFExporter.export(invoiceID));
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnPDF);
        buttonPanel.add(btnClose);

        footer.add(lblTotal, BorderLayout.CENTER);
        footer.add(buttonPanel, BorderLayout.SOUTH);

        return footer;
    }

    /* ================= UTIL ================= */
    private String formatMoney(BigDecimal money) {
        return String.format("%,d đ", money.longValue());
    }
}