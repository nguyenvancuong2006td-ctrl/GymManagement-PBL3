package presentation;

import data.PTServiceDAO;
import model.PTService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterPTDialog extends JDialog {

    private JTable table;
    private JButton btnSelect, btnCancel;

    private boolean selected = false;
    private int selectedServiceID = -1;

    public RegisterPTDialog(Window owner) {
        super(owner, "Chọn gói PT", ModalityType.APPLICATION_MODAL);

        setSize(550, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadServices();
    }

    /* ================= TABLE ================= */

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Danh sách gói PT"));

        table = new JTable();
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadServices() {
        PTServiceDAO dao = new PTServiceDAO();
        List<PTService> list = dao.getAll();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Tên dịch vụ", "PT ID", "Số buổi", "Giá"}, 0
        );

        for (PTService s : list) {
            model.addRow(new Object[]{
                    s.getServiceID(),
                    s.getServiceName(),
                    s.getTrainerID(),
                    s.getTotalSessions(),
                    s.getPrice()
            });
        }

        table.setModel(model);

        // Ẩn cột ID nội bộ
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    /* ================= BUTTON ================= */

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        btnSelect = new JButton("Chọn");
        btnCancel = new JButton("Hủy");

        btnSelect.setBackground(new Color(76, 175, 80));
        btnSelect.setForeground(Color.WHITE);

        btnSelect.addActionListener(e -> select());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnSelect);
        p.add(btnCancel);
        return p;
    }

    /* ================= ACTION ================= */

    private void select() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn gói PT!");
            return;
        }

        selectedServiceID = Integer.parseInt(
                table.getValueAt(r, 0).toString()
        );

        selected = true;
        dispose();
    }

    /* ================= GETTER ================= */

    public boolean isSelected() {
        return selected;
    }

    public int getSelectedServiceID() {
        return selectedServiceID;
    }
}
