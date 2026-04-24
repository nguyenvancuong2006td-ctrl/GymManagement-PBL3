package presentation;

import business.PTServiceBUS;
import model.PTService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SelectPTServiceDialog extends JDialog {

    private JTable table;
    private int selectedServiceID = -1;

    public SelectPTServiceDialog(Window owner, int trainerID) {
        super(owner, "Chọn dịch vụ PT", ModalityType.APPLICATION_MODAL);
        setSize(600, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        PTServiceBUS bus = new PTServiceBUS();
        List<PTService> list = bus.getByTrainer(trainerID);

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(
                    owner,
                    "Huấn luyện viên này chưa có dịch vụ PT nào",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            return;
        }

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Tên dịch vụ", "Số buổi", "Giá"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (PTService s : list) {
            model.addRow(new Object[]{
                    s.getServiceID(),
                    s.getServiceName(),
                    s.getTotalSessions(),
                    fmt.format(s.getPrice())
            });
        }

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnSelect = new JButton("Chọn");
        btnSelect.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ PT");
                return;
            }
            selectedServiceID = (int) table.getValueAt(r, 0);
            dispose();
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnSelect, BorderLayout.SOUTH);
    }

    public boolean isSelected() {
        return selectedServiceID != -1;
    }

    public int getSelectedServiceID() {
        return selectedServiceID;
    }
}