package presentation;

import business.ReportBUS;
import util.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

public class ReportPanel extends JPanel {

    private final ReportBUS reportBUS = new ReportBUS();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> cbType;
    private final DatePicker dpFrom;
    private final DatePicker dpTo;

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

        dpFrom = createDatePicker();
        dpTo = createDatePicker();

        dpFrom.setDate(LocalDate.now().withDayOfMonth(1));
        dpTo.setDate(LocalDate.now());

        JButton btnView = new RoundedButton("Xem báo cáo", new Color(52, 152, 219));
        JButton btnPdf  = new RoundedButton("Xuất PDF", new Color(46, 204, 113));

        filter.add(btnPdf);
        filter.add(cbType);
        filter.add(new JLabel("Từ ngày:"));
        filter.add(dpFrom);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(dpTo);
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
        styleTable();

        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ================= EVENT ================= */

        btnView.addActionListener(e -> loadData());
        btnPdf.addActionListener(e -> exportPdf());
        addDoubleClick();
    }

    private void loadData() {

        model.setRowCount(0);

        LocalDate from = dpFrom.getDate();
        LocalDate to = dpTo.getDate();

        if (from == null) from = LocalDate.MIN;
        if (to == null) to = LocalDate.MAX;

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

        String dir = "reports";
        new java.io.File(dir).mkdirs();

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


    private DatePicker createDatePicker() {

        DatePicker picker = new DatePicker();

        picker.getSettings()
                .setFormatForDatesCommonEra("dd/MM/yyyy");

        picker.setPreferredSize(
                new Dimension(180, 32)
        );

        JButton btn =
                picker.getComponentToggleCalendarButton();

        btn.setText("Chọn");
        btn.setPreferredSize(
                new Dimension(45, 32)
        );

        return picker;
    }

    private void styleTable() {

        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();

        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        Component c =
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column);

                        if (!isSelected) {
                            c.setBackground(
                                    row % 2 == 0
                                            ? Color.WHITE
                                            : new Color(245, 245, 245)
                            );
                        }

                        setHorizontalAlignment(
                                SwingConstants.CENTER
                        );

                        return c;
                    }
                });
    }
}