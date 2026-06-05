package presentation;

import business.MemberPackageBUS;
import data.MembershipPackageDAO;
import model.MembershipPackage;
import util.RoundedButton;
import business.MembershipPackageBUS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.github.lgooddatepicker.components.DatePicker;

public class RegisterPackageDialog extends JDialog {

    private JTable table;
    private JButton btnRegister;
    private JButton btnCancel;
    private JButton btnAddPackage;

    private boolean registered = false;
    private final int memberID;
    private int selectedPackageID = -1;
    private DatePicker dpStartDate;

    public RegisterPackageDialog(Window owner, int memberID) {
        super(owner, "Đăng ký gói tập", ModalityType.APPLICATION_MODAL);
        this.memberID = memberID;

        setSize(520, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        add(createStartDatePanel(), BorderLayout.NORTH);
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

    private JPanel createStartDatePanel() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lbl = new JLabel("Ngày bắt đầu tập:");

        dpStartDate = new DatePicker();

        // Mặc định là hôm nay
        dpStartDate.setDate(java.time.LocalDate.now());

        p.add(lbl);
        p.add(dpStartDate);

        return p;
    }

    private void loadPackages() {
        MembershipPackageDAO dao = new MembershipPackageDAO();
        List<MembershipPackage> list = dao.getAll();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Tên gói", "Thời hạn (tháng)", "Giá"}, 0
        );

        java.text.NumberFormat currency =
                java.text.NumberFormat.getCurrencyInstance(
                        new java.util.Locale("vi", "VN")
                );

        for (MembershipPackage p : list) {
            model.addRow(new Object[]{
                    p.getPackageID(),
                    p.getPackageName(),
                    p.getDuration(),
                    currency.format(p.getPrice())
            });
        }

        table.setModel(model);
    }

    /* ================= BUTTON ================= */

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        btnRegister = new RoundedButton("Đăng ký", new Color(41, 128, 185));
        btnCancel = new RoundedButton("Hủy", new Color(192, 57, 43));
        btnAddPackage = new RoundedButton("Thêm gói", new Color(46, 204, 113));

        btnRegister.addActionListener(e -> register());
        btnCancel.addActionListener(e -> dispose());
        btnAddPackage.addActionListener(e -> addPackage());

        p.add(btnRegister);
        p.add(btnCancel);
        p.add(btnAddPackage);
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
            java.time.LocalDate startDate = dpStartDate.getDate();

            if (startDate == null) {
                startDate = java.time.LocalDate.now();
            }
            new MemberPackageBUS()
                    .registerPackage(
                            memberID,
                            packageID,
                            startDate
                    );
            selectedPackageID = packageID;
            registered = true;

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addPackage() {

        AddPackageDialog dialog =
                new AddPackageDialog(this);

        dialog.setVisible(true);

        if(dialog.isSaved()) {

            loadPackages();

            JOptionPane.showMessageDialog(
                    this,
                    "Đã cập nhật danh sách gói tập"
            );
        }
    }

    public int getSelectedPackageID() {
        return selectedPackageID;
    }

    public boolean isRegistered() {
        return registered;
    }
}