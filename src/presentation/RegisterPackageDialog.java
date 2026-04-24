package presentation;

import business.MemberPackageBUS;
import data.MembershipPackageDAO;
import model.MembershipPackage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterPackageDialog extends JDialog {

    private JTable table;
    private JButton btnRegister, btnCancel;

    private boolean registered = false;
    private final int memberID;
    private int selectedPackageID = -1;

    public RegisterPackageDialog(Window owner, int memberID) {
        super(owner, "Đăng ký gói tập", ModalityType.APPLICATION_MODAL);
        this.memberID = memberID;

        setSize(520, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadPackages();
    }

    /* ================= TABLE ================= */

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Danh sách gói tập"));

        table = new JTable();
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadPackages() {
        MembershipPackageDAO dao = new MembershipPackageDAO();
        List<MembershipPackage> list = dao.getAll();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Tên gói", "Thời hạn (tháng)", "Giá"}, 0
        );

        for (MembershipPackage p : list) {
            model.addRow(new Object[]{
                    p.getPackageID(),
                    p.getPackageName(),
                    p.getDuration(),
                    p.getPrice()
            });
        }

        table.setModel(model);
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn gói tập!");
            return;
        }

        try {
            int packageID = Integer.parseInt(table.getValueAt(r, 0).toString());

            new MemberPackageBUS().registerPackage(memberID, packageID);

            // ✅ QUAN TRỌNG
            selectedPackageID = packageID;
            registered = true;

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public int getSelectedPackageID() {
        return selectedPackageID;
    }

    public boolean isRegistered() {
        return registered;
    }
}