package presentation;

import business.ReportBUS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ReportPanel extends JPanel {

    private final ReportBUS reportBUS = new ReportBUS();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> cbType;
    private final JSpinner spFrom, spTo;

    public ReportPanel() {

        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        /* ================= FILTER ================= */

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbType = new JComboBox<>(new String[]{
                "Báo cáo check-in",
                "Báo cáo doanh thu",
                "Hội viên sắp hết hạn gói"
        });

        spFrom = new JSpinner(new SpinnerDateModel());
        spTo   = new JSpinner(new SpinnerDateModel());

        spFrom.setEditor(new JSpinner.DateEditor(spFrom, "dd/MM/yyyy"));
        spTo.setEditor(new JSpinner.DateEditor(spTo, "dd/MM/yyyy"));

        JButton btnView = new JButton("Xem báo cáo");
        JButton btnPdf = new JButton("Xuất PDF");

        filter.add(btnPdf);
        filter.add(cbType);
        filter.add(new JLabel("Từ ngày:"));
        filter.add(spFrom);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(spTo);
        filter.add(btnView);

        add(filter, BorderLayout.NORTH);

        /* ================= TABLE ================= */

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFocusable(false);

        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ================= EVENT ================= */

        btnView.addActionListener(e -> loadData());
        btnPdf.addActionListener(e -> exportPdf());
        addDoubleClick();
    }

    private void loadData() {

        model.setRowCount(0);

        LocalDate from = toLocal((Date) spFrom.getValue());
        LocalDate to   = toLocal((Date) spTo.getValue());

        if (cbType.getSelectedIndex() == 0) {

            model.setColumnIdentifiers(
                    new String[]{"Ngày", "Số lượt check-in"}
            );

            for (Object[] r : reportBUS.getCheckInReport(from, to)) {
                model.addRow(r);
            }

        } else if (cbType.getSelectedIndex() == 1) {

            model.setColumnIdentifiers(
                    new String[]{"Ngày", "Số hóa đơn", "Doanh thu"}
            );

            for (Object[] r : reportBUS.getRevenueReport(from, to)) {
                model.addRow(r);
            }

        } else {
            Frame owner =
                    (Frame) SwingUtilities.getWindowAncestor(this);

            new MemberExpireReportDialog(owner, 7)
                    .setVisible(true);
        }
    }

    private void exportPdf() {

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không có dữ liệu để xuất PDF",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Tạo thư mục reports nếu chưa có
        String dir = "reports";
        new java.io.File(dir).mkdirs();

        //  Đặt tên file tự động (giống hóa đơn)
        String title;
        String fileName;

        if (cbType.getSelectedIndex() == 0) {
            title = "BÁO CÁO CHECK-IN";
            fileName = "Report_CheckIn_"
                    + System.currentTimeMillis() + ".pdf";
        } else {
            title = "BÁO CÁO DOANH THU";
            fileName = "Report_Revenue_"
                    + System.currentTimeMillis() + ".pdf";
        }

        String path = dir + "/" + fileName;

        // Xuất PDF NGAY – KHÔNG HỎI
        util.ReportPDF.exportTable(
                path,
                title,
                "Dữ liệu báo cáo đang hiển thị",
                model
        );

        JOptionPane.showMessageDialog(
                this,
                "Xuất PDF thành công:\n" + path,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );
    }



    private void addDoubleClick() {

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {

                    LocalDate d =
                            (LocalDate) model.getValueAt(
                                    table.getSelectedRow(), 0
                            );

                    Frame owner =
                            (Frame) SwingUtilities.getWindowAncestor(table);

                    if (cbType.getSelectedIndex() == 0)
                        new CheckInReportDialog(owner, d).setVisible(true);
                    else
                        new RevenueReportDialog(owner, d).setVisible(true);
                }
            }
        });
    }

    private LocalDate toLocal(Date d) {
        return d.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}