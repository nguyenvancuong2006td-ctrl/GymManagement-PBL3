package presentation;

import business.MemberBUS;
import model.Member;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MemberUI extends JPanel {

    // ===== ROLE =====
    private final String role; // "ADMIN" | "STAFF"

    // ===== FORM =====
    private JTextField txtID, txtName, txtPhone, txtJoinDate;
    private JComboBox<String> cbGender;

    // ===== TABLE & SEARCH =====
    private JTable table;
    private JTextField txtSearch;

    // ===== BUTTON =====
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    private final MemberBUS memberBUS = new MemberBUS();
    private List<Member> allMembers = new ArrayList<>();

    // ===== CONSTRUCTOR =====
    public MemberUI(String role) {
        this.role = role;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
        applyRolePermission();
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
        txtJoinDate = field(false); // read-only

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

        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());

        p.add(btnAdd);
        p.add(btnUpdate);
        p.add(btnDelete);
        p.add(btnClear);
        return p;
    }

    /* ================= TABLE + SEARCH ================= */
    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Member List"));

        // ===== SEARCH BAR (CÂN ĐỐI) =====
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 28));
        txtSearch.setToolTipText("Search member by name");

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 0;
        top.add(new JLabel("Search:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        top.add(txtSearch, gbc);

        // ===== TABLE =====
        table = new JTable();
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    /* ================= DATA ================= */
    private void loadData() {
        if ("ADMIN".equalsIgnoreCase(role)) {
            allMembers = memberBUS.search(null);
        } else {
            allMembers = new ArrayList<>();
        }
        fillTable(allMembers);
    }

    private void fillTable(List<Member> list) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Gender", "Phone", "Join Date"}, 0);

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

        if (!txtID.getText().isEmpty()) {
            m.setMemberID(Integer.parseInt(txtID.getText()));
        }
        m.setFullName(txtName.getText());
        m.setGender(cbGender.getSelectedItem().toString());
        m.setPhoneNumber(txtPhone.getText());

        return m;
    }

    private void addMember() {
        JOptionPane.showMessageDialog(this,
                memberBUS.add(getFormData(), role));
        loadData();
        clearForm();
    }

    private void updateMember() {
        JOptionPane.showMessageDialog(this,
                memberBUS.update(getFormData(), role));
        loadData();
        clearForm();
    }

    private void deleteMember() {
        JOptionPane.showMessageDialog(this,
                memberBUS.delete(
                        Integer.parseInt(txtID.getText()), role));
        loadData();
        clearForm();
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtID.setText(table.getValueAt(r, 0).toString());
        txtName.setText(table.getValueAt(r, 1).toString());
        cbGender.setSelectedItem(table.getValueAt(r, 2));
        txtPhone.setText(table.getValueAt(r, 3).toString());
        txtJoinDate.setText(String.valueOf(table.getValueAt(r, 4)));

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
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
    }

    private void applyRolePermission() {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        btnUpdate.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
    }

    /* ================= UI HELPERS ================= */
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