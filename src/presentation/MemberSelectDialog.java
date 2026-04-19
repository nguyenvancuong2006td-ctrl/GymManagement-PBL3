package presentation;

import data.MemberDAO;
import model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberSelectDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private Member selectedMember;

    public MemberSelectDialog(Window parent) {
        super(parent, "Chọn hội viên", ModalityType.APPLICATION_MODAL);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("CHỌN HỘI VIÊN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Mã", "Tên hội viên", "SĐT"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnSelect = new JButton("Chọn");
        JButton btnCancel = new JButton("Hủy");

        btnSelect.addActionListener(e -> onSelect());
        btnCancel.addActionListener(e -> {
            selectedMember = null;
            dispose();
        });

        bottom.add(btnCancel);
        bottom.add(btnSelect);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        MemberDAO dao = new MemberDAO();
        List<Member> list = dao.getAll();

        model.setRowCount(0);
        for (Member m : list) {
            model.addRow(new Object[]{
                    m.getMemberID(),
                    m.getFullName(),
                    m.getPhoneNumber() // ✅ KHỚP DAO CỦA BẠN
            });
        }
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn hội viên!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int memberID = (int) model.getValueAt(row, 0);
        selectedMember = new MemberDAO().findById(memberID);
        dispose();
    }

    // ===== METHOD GỌI TỪ CARTUI =====
    public static Member showDialog(Component parent) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        MemberSelectDialog dialog = new MemberSelectDialog(window);
        dialog.setVisible(true);
        return dialog.selectedMember;
    }
}