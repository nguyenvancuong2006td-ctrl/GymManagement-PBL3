package presentation;

import business.MemberBUS;
import business.ServiceCheckoutBUS;
import data.MemberPackageDAO;
import util.InvoicePDFExporter;
import model.*;
import util.Session;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MemberUI extends JPanel {

    // ===== FORM =====
    private JTextField txtID, txtName, txtPhone, txtJoinDate;
    private JComboBox<String> cbGender;

    // ===== TABLE & SEARCH =====
    private JTable table;
    private JTextField txtSearch;

    // ===== BUTTON =====
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRegisterPackage;

    private final MemberBUS memberBUS = new MemberBUS();
    private List<Member> allMembers = new ArrayList<>();

    public MemberUI() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
        applyUiPermission();
        clearForm();
    }

    /* ================= FORM ================= */

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Member Information"));

        JPanel f = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtID = field(false);
        txtName = field(true);
        txtPhone = field(true);
        txtJoinDate = field(false);

        cbGender = new JComboBox<>(new String[]{"Male", "Female"});

        addRow(f, g, 0, "ID", txtID, "Gender", cbGender);
        addRow(f, g, 1, "Full Name", txtName, null, null);
        addRow(f, g, 2, "Phone", txtPhone, "Join Date", txtJoinDate);

        p.add(f, BorderLayout.CENTER);
        p.add(createButtonPanel(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

        btnAdd = btn("Add", new Color(76, 175, 80));
        btnUpdate = btn("Update", new Color(255, 152, 0));
        btnDelete = btn("Delete", new Color(220, 80, 80));
        btnClear = btn("Clear", new Color(180, 180, 180));
        btnRegisterPackage = btn("Đăng ký gói", new Color(33, 150, 243));

        btnRegisterPackage.setEnabled(false);

        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());
        btnRegisterPackage.addActionListener(e -> registerPackageFlow());

        p.add(btnAdd);
        p.add(btnUpdate);
        p.add(btnDelete);
        p.add(btnRegisterPackage);
        p.add(btnClear);
        return p;
    }

    /* ================= TABLE + SEARCH ================= */

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Member List"));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 28));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        JPanel top = new JPanel(new BorderLayout(5, 0));
        top.add(new JLabel("Search:"), BorderLayout.WEST);
        top.add(txtSearch, BorderLayout.CENTER);

        table = new JTable();
        table.setRowHeight(26);
        //khóa chỉnh sửa trong table
        table.setDefaultEditor(Object.class, null);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        // ✅ DOUBLE CLICK HERE
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {

                    int row = table.getSelectedRow();
                    int memberID = Integer.parseInt(
                            table.getValueAt(row, 0).toString()
                    );

                    new MemberDetailDialog(
                            (Frame) SwingUtilities.getWindowAncestor(MemberUI.this),
                            memberID
                    ).setVisible(true);
                }
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    /* ================= DATA ================= */

    private void loadData() {
        allMembers = memberBUS.search(null);
        fillTable(allMembers);
    }

    private void fillTable(List<Member> list) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Gender", "Phone", "Join Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // KHÓA EDIT
            }
        };

        for (Member m : list) {
            model.addRow(new Object[]{
                    m.getMemberID(),
                    m.getFullName(),
                    m.getGender(),
                    m.getPhoneNumber(),
                    m.getJoinDate()
            });
        }
        table.setModel(model);
    }

    private void filter() {
        String key = txtSearch.getText().toLowerCase().trim();
        List<Member> rs = new ArrayList<>();

        for (Member m : allMembers) {
            if (m.getFullName().toLowerCase().contains(key)) {
                rs.add(m);
            }
        }
        fillTable(rs);
    }

    /* ================= CRUD ================= */

    private Member getFormData() {
        Member m = new Member();
        if (!txtID.getText().isEmpty())
            m.setMemberID(Integer.parseInt(txtID.getText()));

        m.setFullName(txtName.getText());
        m.setGender(cbGender.getSelectedItem().toString());
        m.setPhoneNumber(txtPhone.getText());
        return m;
    }

    private void addMember() {
        try {
            Member created = memberBUS.add(getFormData());
            JOptionPane.showMessageDialog(this, "Thêm hội viên thành công!");

            loadData();
            clearForm();

            txtID.setText(String.valueOf(created.getMemberID()));
            registerPackageFlow();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateMember() {
        memberBUS.update(getFormData());
        JOptionPane.showMessageDialog(this, "Cập nhật thành công");
        loadData();
        clearForm();
    }

    private void deleteMember() {
        memberBUS.delete(Integer.parseInt(txtID.getText()));
        JOptionPane.showMessageDialog(this, "Xóa thành công");
        loadData();
        clearForm();
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        int memberID = Integer.parseInt(table.getValueAt(r, 0).toString());

        txtID.setText(String.valueOf(memberID));
        txtName.setText(table.getValueAt(r, 1).toString());
        cbGender.setSelectedItem(table.getValueAt(r, 2));
        txtPhone.setText(table.getValueAt(r, 3).toString());
        txtJoinDate.setText(String.valueOf(table.getValueAt(r, 4)));

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);

        // ===== CHECK GÓI ACTIVE =====
        MemberPackageDAO pkgDAO = new MemberPackageDAO();
        MemberPackage active = pkgDAO.getActiveByMember(memberID);
        btnRegisterPackage.setEnabled(active == null);
    }

    private void clearForm() {
        txtID.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtJoinDate.setText("");
        cbGender.setSelectedIndex(0);
        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnRegisterPackage.setEnabled(false);
    }

    /* ================= REGISTER FLOW ================= */

    private void registerPackageFlow() {
        try {
            if (txtID.getText().isEmpty()) return;

            int memberID = Integer.parseInt(txtID.getText());

            /* ================= 1. CHỌN GÓI TẬP ================= */

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn đăng ký gói tập không?",
                    "Đăng ký gói",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice != JOptionPane.YES_OPTION) return;

            RegisterPackageDialog pkgDialog =
                    new RegisterPackageDialog(
                            SwingUtilities.getWindowAncestor(this),
                            memberID
                    );
            pkgDialog.setVisible(true);

            if (!pkgDialog.isRegistered()) return;
            int packageID = pkgDialog.getSelectedPackageID();

            /* ================= 2. CHỌN PT (HUẤN LUYỆN VIÊN) ================= */

            Integer ptServiceID = null;

            int choicePT = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn đăng ký tập với PT không?",
                    "Đăng ký PT",
                    JOptionPane.YES_NO_OPTION
            );

            if (choicePT == JOptionPane.YES_OPTION) {

                // 2.1 Chọn huấn luyện viên
                SelectTrainerDialog trainerDialog =
                        new SelectTrainerDialog(SwingUtilities.getWindowAncestor(this));
                trainerDialog.setVisible(true);

                if (!trainerDialog.isSelected()) return;
                int trainerID = trainerDialog.getSelectedTrainerID();

                // 2.2 Chọn dịch vụ của PT đó
                SelectPTServiceDialog serviceDialog =
                        new SelectPTServiceDialog(
                                SwingUtilities.getWindowAncestor(this),
                                trainerID
                        );
                serviceDialog.setVisible(true);

                if (!serviceDialog.isSelected()) return;
                ptServiceID = serviceDialog.getSelectedServiceID();
            }

            /* ================= 3. XÁC NHẬN THANH TOÁN ================= */

            int confirmPay = JOptionPane.showConfirmDialog(
                    this,
                    "Nhân viên đã nhận tiền từ hội viên chưa?",
                    "Xác nhận thanh toán",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmPay != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        "Chưa xác nhận thanh toán. Giao dịch bị hủy."
                );
                return;
            }

            /* ================= 4. THANH TOÁN ================= */

            ServiceCheckoutBUS checkoutBUS = new ServiceCheckoutBUS();

            int invoiceID = checkoutBUS.checkoutService(
                    memberID,
                    packageID,
                    ptServiceID,
                    PaymentMethod.CASH
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán thành công!\nMã hóa đơn: #" + invoiceID
            );

            /* ================= 5. XUẤT HÓA ĐƠN PDF ================= */

            int exportPDF = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn xuất hóa đơn PDF không?",
                    "Xuất hóa đơn",
                    JOptionPane.YES_NO_OPTION
            );

            if (exportPDF == JOptionPane.YES_OPTION) {
                InvoicePDFExporter.export(invoiceID);

                JOptionPane.showMessageDialog(
                        this,
                        "Xuất hóa đơn PDF thành công!"
                );
            }

            /* ================= 6. KẾT THÚC ================= */

            loadData();
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /* ================= PERMISSION ================= */

    private void applyUiPermission() {
        boolean isAdmin = Session.getRole() == Role.Admin;
        btnDelete.setVisible(isAdmin);
    }

    /* ================= HELPERS ================= */

    private JTextField field(boolean enable) {
        JTextField f = new JTextField();
        f.setEnabled(enable);
        return f;
    }

    private JButton btn(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        return b;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y,
                        String l1, JComponent f1,
                        String l2, JComponent f2) {

        g.gridy = y;

        g.gridx = 0;
        p.add(new JLabel(l1), g);

        g.gridx = 1; g.weightx = 1;
        p.add(f1, g);

        if (l2 != null) {
            g.gridx = 2; g.weightx = 0;
            p.add(new JLabel(l2), g);

            g.gridx = 3; g.weightx = 1;
            p.add(f2, g);
        }
    }
}