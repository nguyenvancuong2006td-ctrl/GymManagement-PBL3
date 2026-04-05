package presentation;

import business.AccountBUS;
import business.StaffBUS;
import model.Account;
import model.Staff;

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

    //  cache data cho realtime search
    private List<Staff> allStaff = new ArrayList<>();

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
        loadStaffTable(""); // load lần đầu
    }

    // ================= TOP FORM =================
    private JPanel createTopForm() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setOpaque(false);

        panel.add(createAccountCard());
        panel.add(createStaffCard());

        return panel;
    }

    // ================= ACCOUNT CARD =================
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

        g.gridx = 1; g.gridy = 4;
        p.add(btnCreate, g);

        return p;
    }

    // ================= STAFF CARD =================
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

        g.gridx = 1; g.gridy = 5;
        p.add(btnPanel, g);

        return p;
    }

    // ================= TABLE + SEARCH =================
    // ================= TABLE + SEARCH =================
    private JPanel createTablePanel() {

        // Panel ngoài
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        // Card có tiêu đề "Staff List"
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Staff List",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        // ===== SEARCH UI (CHỈ UI) =====
        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 30));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // GIỮ NGUYÊN EVENT SEARCH CŨ
        txtSearch.addActionListener(e -> filterTable(txtSearch.getText()));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterTable(txtSearch.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                filterTable(txtSearch.getText());
            }
            public void changedUpdate(DocumentEvent e) {
                filterTable(txtSearch.getText());
            }
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // ===== TABLE =====
        table = new JTable();
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(
                e -> fillFormFromTable()
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Add vào card
        card.add(searchPanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    // ================= LOGIC =================
    private void createAccount() {
        try {
            Account acc = new Account();
            acc.setUsername(txtUsername.getText());
            acc.setPassword(new String(txtPassword.getPassword()));
            acc.setRole(cbRole.getSelectedItem().toString());

            currentAccountID = accountBUS.createStaffAccount(acc);
            txtAccountID.setText(String.valueOf(currentAccountID));
            lockStaff(false);

            JOptionPane.showMessageDialog(this, "Account created");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addStaff() {
        try {
            Staff s = getStaffFromForm();
            staffBUS.addStaff(s);
            loadStaffTable("");
            clearStaffForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updateStaff() {
        try {
            if (txtStaffID.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select staff to update");
                return;
            }
            Staff s = getStaffFromForm();
            s.setStaffID(Integer.parseInt(txtStaffID.getText()));
            staffBUS.updateStaff(s);
            loadStaffTable("");
            clearStaffForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteStaff() {
        try {
            if (txtStaffID.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select staff to delete");
                return;
            }
            int id = Integer.parseInt(txtStaffID.getText());
            staffBUS.deleteStaff(id);
            loadStaffTable("");
            clearStaffForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    //  LOAD DB 1 LẦN
    private void loadStaffTable(String keyword) {
        try {
            allStaff = staffBUS.getAll();
            filterTable(keyword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //  FILTER REALTIME
    private void filterTable(String keyword) {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"ID", "Name", "Gender", "Phone", "Salary"}, 0
        );

        String key = keyword == null ? "" : keyword.toLowerCase();

        for (Staff s : allStaff) {
            if (s.getFullName().toLowerCase().contains(key)) {
                m.addRow(new Object[]{
                        s.getStaffID(),
                        s.getFullName(),
                        s.getGender(),
                        s.getPhoneNumber(),
                        s.getSalary()
                });
            }
        }
        table.setModel(m);
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r == -1) return;

        txtStaffID.setText(table.getValueAt(r, 0).toString());
        txtName.setText(table.getValueAt(r, 1).toString());
        cbGender.setSelectedItem(table.getValueAt(r, 2).toString());
        txtPhone.setText(table.getValueAt(r, 3).toString());
        txtSalary.setText(table.getValueAt(r, 4).toString());
        lockStaff(false);
    }

    private Staff getStaffFromForm() {
        Staff s = new Staff();
        s.setFullName(txtName.getText());
        s.setGender(cbGender.getSelectedItem().toString());
        s.setPhoneNumber(txtPhone.getText());
        s.setSalary(Double.parseDouble(txtSalary.getText()));
        s.setHireDate(LocalDate.now());
        s.setAccountID(currentAccountID);
        return s;
    }

    private void lockStaff(boolean b) {
        txtStaffID.setEnabled(!b);
        txtName.setEnabled(!b);
        cbGender.setEnabled(!b);
        txtPhone.setEnabled(!b);
        txtSalary.setEnabled(!b);
    }

    private void clearStaffForm() {
        txtStaffID.setText("");
        txtName.setText("");
        cbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtSalary.setText("");
        table.clearSelection();
        lockStaff(false);
    }

    // ================= UI HELPERS =================
    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
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
        g.gridx = 0; g.gridy = y; g.weightx = 0;
        p.add(new JLabel(l), g);
        g.gridx = 1; g.weightx = 1;
        p.add(f, g);
    }

    private JButton primaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(52,120,208));
        b.setForeground(Color.WHITE);
        return b;
    }

    private JButton secondaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(180,180,180));
        return b;
    }

    private JButton dangerButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(220,80,80));
        b.setForeground(Color.WHITE);
        return b;
    }
}