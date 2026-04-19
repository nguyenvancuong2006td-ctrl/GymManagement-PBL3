package presentation;

import business.AccountBUS;
import business.StaffBUS;
import model.Account;
import model.Staff;
import model.StaffAccount;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffUI extends JPanel {

    // ===== ACCOUNT =====
    private JTextField txtUsername, txtAccountID;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;

    // ===== STAFF =====
    private JTextField txtStaffID, txtName, txtPhone, txtSalary;
    private JComboBox<String> cbGender;

    // ===== TABLE & SEARCH =====
    private JTable table;
    private JTextField txtSearch;


    private List<StaffAccount> allStaff = new ArrayList<>();

    private final AccountBUS accountBUS = new AccountBUS();
    private final StaffBUS staffBUS = new StaffBUS();

    private int currentAccountID = -1;

    public StaffUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createTopForm(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        lockStaff(true);
        loadStaffTable("");
    }

    /* ================= TOP FORM ================= */

    private JPanel createTopForm() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setOpaque(false);
        panel.add(createAccountCard());
        panel.add(createStaffCard());
        return panel;
    }

    /* ================= ACCOUNT CARD ================= */

    private JPanel createAccountCard() {
        JPanel p = createCard("Account");

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cbRole = new JComboBox<>(new String[]{"Staff"});
        txtAccountID = new JTextField();
        txtAccountID.setEditable(false);

        JButton btnCreate = primaryButton("Create Account");
        btnCreate.addActionListener(e -> createAccount());

        p.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        addRow(p, g, 0, "Username", txtUsername);
        addRow(p, g, 1, "Password", txtPassword);
        addRow(p, g, 2, "Role", cbRole);
        addRow(p, g, 3, "Account ID", txtAccountID);

        g.gridx = 1;
        g.gridy = 4;
        p.add(btnCreate, g);

        return p;
    }

    /* ================= STAFF CARD ================= */

    private JPanel createStaffCard() {
        JPanel p = createCard("Staff");

        txtStaffID = new JTextField();
        txtStaffID.setEditable(false);
        txtName = new JTextField();
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        txtPhone = new JTextField();
        txtSalary = new JTextField();

        JButton btnAdd = primaryButton("Add");
        JButton btnUpdate = secondaryButton("Update");
        JButton btnDelete = dangerButton("Delete");
        JButton btnClear = secondaryButton("Clear");

        btnAdd.addActionListener(e -> addStaff());
        btnUpdate.addActionListener(e -> updateStaff());
        btnDelete.addActionListener(e -> deleteStaff());
        btnClear.addActionListener(e -> clearStaffForm());

        p.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        addRow(p, g, 0, "Staff ID", txtStaffID);
        addRow(p, g, 1, "Full name", txtName);
        addRow(p, g, 2, "Gender", cbGender);
        addRow(p, g, 3, "Phone", txtPhone);
        addRow(p, g, 4, "Salary", txtSalary);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 1;
        g.gridy = 5;
        p.add(btnPanel, g);

        return p;
    }

    /* ================= TABLE + SEARCH ================= */

    private JPanel createTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JPanel card = createCard("Staff List");
        card.setLayout(new BorderLayout(8, 8));

        txtSearch = new JTextField();
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { filterTable(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) {}
        });

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        table = new JTable();
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        card.add(searchPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    /* ================= DATA ================= */

    private void loadStaffTable(String keyword) {
        allStaff = staffBUS.getAllWithAccount();
        filterTable(keyword);
    }

    private void filterTable(String keyword) {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{
                        "Staff ID",
                        "Name",
                        "Gender",
                        "Phone",
                        "Salary",
                        "Username",
                        "Password"
                }, 0
        );

        String key = keyword == null ? "" : keyword.toLowerCase();

        for (StaffAccount s : allStaff) {
            if (s.getFullName().toLowerCase().contains(key)
                    || s.getUsername().toLowerCase().contains(key)) {
                m.addRow(new Object[]{
                        s.getStaffID(),
                        s.getFullName(),
                        s.getGender(),
                        s.getPhone(),
                        s.getSalary(),
                        s.getUsername(),
                        s.getPassword()
                });
            }
        }
        table.setModel(m);
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtStaffID.setText(table.getValueAt(r, 0).toString());
        txtName.setText(table.getValueAt(r, 1).toString());
        cbGender.setSelectedItem(table.getValueAt(r, 2));
        txtPhone.setText(table.getValueAt(r, 3).toString());
        txtSalary.setText(table.getValueAt(r, 4).toString());

        txtUsername.setText(table.getValueAt(r, 5).toString());
        txtPassword.setText(table.getValueAt(r, 6).toString());

        lockStaff(false);
    }

    /* ================= CRUD ================= */

    private void createAccount() {
        try {
            Account acc = new Account();
            acc.setUsername(txtUsername.getText());
            acc.setPassword(new String(txtPassword.getPassword()));
            acc.setRole(cbRole.getSelectedItem().toString());

            currentAccountID = accountBUS.createStaffAccount(acc);
            txtAccountID.setText(String.valueOf(currentAccountID));
            lockStaff(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addStaff() {
        try {
            Staff s = new Staff();
            s.setFullName(txtName.getText());
            s.setGender(cbGender.getSelectedItem().toString());
            s.setPhoneNumber(txtPhone.getText());
            s.setSalary(Double.parseDouble(txtSalary.getText()));
            s.setHireDate(LocalDate.now());
            s.setAccountID(currentAccountID);

            staffBUS.addStaff(s);
            loadStaffTable("");
            clearStaffForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateStaff() {
        try {
            Staff s = new Staff();
            s.setStaffID(Integer.parseInt(txtStaffID.getText()));
            s.setFullName(txtName.getText());
            s.setGender(cbGender.getSelectedItem().toString());
            s.setPhoneNumber(txtPhone.getText());
            s.setSalary(Double.parseDouble(txtSalary.getText()));
            s.setHireDate(LocalDate.now());

            staffBUS.updateStaff(s);
            loadStaffTable("");
            clearStaffForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteStaff() {
        try {
            staffBUS.deleteStaff(Integer.parseInt(txtStaffID.getText()));
            loadStaffTable("");
            clearStaffForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void lockStaff(boolean b) {
        txtName.setEnabled(!b);
        cbGender.setEnabled(!b);
        txtPhone.setEnabled(!b);
        txtSalary.setEnabled(!b);
    }

    private void clearStaffForm() {
        txtStaffID.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtSalary.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        table.clearSelection();
        lockStaff(true);
    }

    /* ================= UI HELPERS ================= */

    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));
        return p;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String l, JComponent f) {
        g.gridx = 0;
        g.gridy = y;
        g.weightx = 0;
        p.add(new JLabel(l), g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(f, g);
    }

    private JButton primaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(52, 120, 208));
        b.setForeground(Color.WHITE);
        return b;
    }

    private JButton secondaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(180, 180, 180));
        return b;
    }

    private JButton dangerButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(220, 80, 80));
        b.setForeground(Color.WHITE);
        return b;
    }
}
