package presentation;

import business.ReportBUS;
import util.ReportPDF;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MemberExpireReportDialog extends JDialog {

    public MemberExpireReportDialog(Frame owner, int days) {

        super(owner, "Hội viên sắp hết hạn (" + days + " ngày)", true);
        setSize(750, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(16, 16));

        /* ================= HEADER ================= */

        JLabel title = new JLabel("HỘI VIÊN SẮP HẾT HẠN GÓI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel sub = new JLabel(
                "Danh sách hội viên có gói tập hết hạn trong " + days + " ngày tới"
        );
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        /* ================= TABLE ================= */

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "Mã HV", "Họ tên", "SĐT", "Ngày hết hạn", "Còn lại (ngày)"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        ReportBUS bus = new ReportBUS();
        for (Object[] r : bus.getMembersExpiring(days)) {
            model.addRow(r);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        add(scroll, BorderLayout.CENTER);

        /* ================= FOOTER ================= */

        JLabel lblTotal =
                new JLabel("Tổng số: " + model.getRowCount() + " hội viên");

        JButton btnPdf = new JButton("Xuất PDF");

        btnPdf.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn vị trí lưu báo cáo");
            chooser.setSelectedFile(
                    new java.io.File("HoiVienSapHetHan.pdf")
            );

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            ReportPDF.exportTable(
                    path,
                    "HỘI VIÊN SẮP HẾT HẠN GÓI",
                    "Trong " + days + " ngày tới",
                    model
            );
        });

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        footer.add(lblTotal, BorderLayout.WEST);
        footer.add(btnPdf, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);
    }
}
