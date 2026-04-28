package presentation;

import business.ReportBUS;
import model.CheckIn;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckInReportDialog extends JDialog {

    private static final Font FONT_TITLE =
            new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NORMAL =
            new Font("Segoe UI", Font.PLAIN, 13);

    public CheckInReportDialog(Frame owner, LocalDate date) {

        super(owner, "Chi tiết check-in ngày " + date, true);
        setSize(600, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(16, 16));

        /* ================= HEADER ================= */

        JLabel title = new JLabel("CHI TIẾT CHECK-IN");
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
                new String[]{"STT", "Số điện thoại", "Giờ check-in"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);

        DateTimeFormatter timeFmt =
                DateTimeFormatter.ofPattern("HH:mm");

        ReportBUS reportBUS = new ReportBUS();
        List<CheckIn> list =
                reportBUS.getCheckInDetail(date);

        int index = 1;
        for (CheckIn c : list) {
            model.addRow(new Object[]{
                    index++,
                    c.getPhoneNumber(),
                    c.getCheckInTime().format(timeFmt)
            });
        }

        /* ===== CENTER ALIGN ===== */

        DefaultTableCellRenderer center =
                new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));

        add(scroll, BorderLayout.CENTER);

        /* ================= FOOTER ================= */

        JLabel footer = new JLabel(
                "Tổng lượt check-in: " + list.size()
        );
        footer.setFont(FONT_NORMAL);
        footer.setBorder(
                BorderFactory.createEmptyBorder(0, 16, 16, 16)
        );

        add(footer, BorderLayout.SOUTH);
    }
}