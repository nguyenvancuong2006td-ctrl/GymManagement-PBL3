package presentation;

import business.MemberPTBUS;
import data.PTServiceDAO;
import model.PTService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterPTDialog extends JDialog {

    private JTable table;
    private JButton btnRegister, btnCancel;

    private boolean registered = false;
    private final int memberID;

    public RegisterPTDialog(Window owner, int memberID) {
        super(owner, "Đăng ký PT", ModalityType.APPLICATION_MODAL);
        this.memberID = memberID;

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

        // Ẩn cột ID nội bộ nếu muốn
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    /* ================= BUTTON ================= */

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy");

        btnRegister.setBackground(new Color(76, 175, 80));
        btnRegister.setForeground(Color.WHITE);

        btnRegister.addActionListener(e -> register());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnRegister);
        p.add(btnCancel);
        return p;
    }

    /* ================= ACTION ================= */

    private void register() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn gói PT!");
            return;
        }

        try {
            int serviceID = Integer.parseInt(table.getValueAt(r, 0).toString());

            new MemberPTBUS().buyPT(memberID, serviceID);

            registered = true;
            JOptionPane.showMessageDialog(this, "Đăng ký PT thành công");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public boolean isRegistered() {
        return registered;
    }
}