package presentation;

import business.ReportBUS;
import model.Invoice;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RevenueReportDialog extends JDialog {

    private static final Font FONT_TITLE =
            new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NORMAL =
            new Font("Segoe UI", Font.PLAIN, 13);

    public RevenueReportDialog(Frame owner, LocalDate date) {

        super(owner, "Doanh thu ngày " + date, true);
        setSize(700, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(16, 16));

        /* ================= HEADER ================= */

        JLabel title = new JLabel("CHI TIẾT DOANH THU");
        title.setFont(FONT_TITLE);

        JLabel subtitle = new JLabel("Ngày: " + date);
        subtitle.setFont(FONT_NORMAL);
        subtitle.setForeground(Color.GRAY);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        /* ================= TABLE ================= */

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "STT",
                        "Mã hóa đơn",
                        "Thời gian",
                        "Nhân viên",
                        "Tổng tiền"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false; // ✅ KHÓA KHÔNG CHO NHẬP
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        DateTimeFormatter timeFmt =
                DateTimeFormatter.ofPattern("HH:mm");

        ReportBUS reportBUS = new ReportBUS();
        List<Invoice> invoices =
                reportBUS.getInvoiceDetail(date);

        int index = 1;
        double totalRevenue = 0;

        for (Invoice i : invoices) {
            model.addRow(new Object[]{
                    index++,
                    i.getInvoiceID(),
                    i.getInvoiceDate().format(timeFmt),
                    i.getStaffName(),
                    i.getTotalAmount()
            });
            totalRevenue += i.getTotalAmount().doubleValue();
        }

        /* ===== CANH GIỮA ===== */

        DefaultTableCellRenderer center =
                new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);

        /* ================= DOUBLE CLICK ================= */

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    int invoiceId =
                            Integer.parseInt(
                                    model.getValueAt(row, 1).toString()
                            );


                    new InvoiceDetailDialog(owner, invoiceId)
                            .setVisible(true);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(
                BorderFactory.createEmptyBorder(8, 16, 16, 16)
        );
        add(scroll, BorderLayout.CENTER);

        /* ================= FOOTER ================= */

        JLabel footer = new JLabel(
                "Tổng doanh thu: " + totalRevenue + " VND"
        );
        footer.setFont(FONT_NORMAL);
        footer.setBorder(
                BorderFactory.createEmptyBorder(0, 16, 16, 16)
        );

        add(footer, BorderLayout.SOUTH);
    }
}
