package presentation;

import business.AccountBUS;
import business.StaffBUS;
import model.Account;
import model.Staff;
import model.StaffAccount;
import util.RoundedButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffUI extends JPanel {

    /* ================= FONT ================= */
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);

    /* ================= ACCOUNT ================= */
    private JTextField txtUsername, txtAccountID;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;

    /* ================= STAFF ================= */
    private JTextField txtStaffID, txtName, txtPhone, txtSalary;
    private JComboBox<String> cbGender;

    /* ================= TABLE ================= */
    private JTable table;
    private JTextField txtSearch;

    private List<StaffAccount> allStaff = new ArrayList<>();

    private final AccountBUS accountBUS = new AccountBUS();
    private final StaffBUS staffBUS = new StaffBUS();

    private int currentAccountID = -1;

    /* ================= CONSTRUCTOR ================= */

    public StaffUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        applyGlobalStyle();

        add(createTopForm(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        resetToCreateMode();
        loadStaffTable("");
    }

    /* ================= GLOBAL STYLE ================= */

    private void applyGlobalStyle() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("TextField.font", FONT_NORMAL);
        UIManager.put("PasswordField.font", FONT_NORMAL);
        UIManager.put("ComboBox.font", FONT_NORMAL);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("TableHeader.font", FONT_BOLD);
    }

    /* ================= TOP FORM ================= */

    private JPanel createTopForm() {
        JPanel p = new JPanel(new GridLayout(1, 2, 15, 0));
        p.setOpaque(false);
        p.add(createAccountCard());
        p.add(createStaffCard());
        return p;
    }

    /* ================= ACCOUNT CARD ================= */

    private JPanel createAccountCard() {
        JPanel p = createCard("Account");

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cbRole = new JComboBox<>(new String[]{"Staff", "Admin"});
        txtAccountID = new JTextField();
        txtAccountID.setEditable(false);

        JButton btnCreate = new RoundedButton(
                "Create Account",
                new Color(52, 120, 208)
        );
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
        txtPhone = new JTextField();
        txtSalary = new JTextField();
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        JButton btnAdd = new RoundedButton("Thêm", new Color(46, 204, 113));
        JButton btnUpdate = new RoundedButton("Cập nhật", new Color(52, 152, 219));
        JButton btnDelete = new RoundedButton("Xóa", new Color(231, 76, 60));
        JButton btnClear = new RoundedButton("Làm mới", new Color(127, 140, 141));

        Dimension size = new Dimension(100, 35);

        btnAdd.setPreferredSize(size);
        btnUpdate.setPreferredSize(size);
        btnDelete.setPreferredSize(size);
        btnClear.setPreferredSize(size);


        btnAdd.addActionListener(e -> addStaff());
        btnUpdate.addActionListener(e -> updateAll());
        btnDelete.addActionListener(e -> deleteStaff());
        btnClear.addActionListener(e -> resetToCreateMode());

        p.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        addRow(p, g, 0, "Staff ID", txtStaffID);
        addRow(p, g, 1, "Full Name", txtName);
        addRow(p, g, 2, "Gender", cbGender);
        addRow(p, g, 3, "Phone", txtPhone);
        addRow(p, g, 4, "Salary", txtSalary);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 1;
        g.gridy = 5;


        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.weightx = 0;
        p.add(btnPanel, g);
        return p;
    }

    /* ================= TABLE ================= */

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

        table = new JTable();
        table.setRowHeight(28);
        table.setDefaultEditor(Object.class, null);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
        styleTable(table);

        card.add(txtSearch, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    /* ================= TABLE STYLE ================= */

    private void styleTable(JTable table) {

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected,
                    boolean focus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        t, value, selected, focus, row, col);

                if (selected) {
                    c.setBackground(new Color(52, 152, 219));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    c.setForeground(Color.BLACK);
                }

                setBorder(noFocusBorder);
                return c;
            }
        };

        table.setDefaultRenderer(Object.class, renderer);
    }

    /* ================= DATA ================= */

    private void loadStaffTable(String keyword) {
        allStaff = staffBUS.getAllWithAccount();
        filterTable(keyword);
    }

    private void filterTable(String keyword) {

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Staff ID","Name","Gender","Phone","Salary","Username","Password"}, 0
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
        styleTable(table);

        // ===== FORMAT SALARY – LEFT ALIGN =====
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        DefaultTableCellRenderer salaryRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (value instanceof Number) {
                    setText(nf.format(((Number) value).longValue()));
                } else {
                    setText("");
                }

                setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        };

        table.getColumnModel().getColumn(4).setCellRenderer(salaryRenderer);
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        StaffAccount sa = allStaff.get(r);

        txtStaffID.setText(String.valueOf(sa.getStaffID()));
        txtName.setText(sa.getFullName());
        cbGender.setSelectedItem(sa.getGender());
        txtPhone.setText(sa.getPhone());
        txtSalary.setText(String.valueOf(sa.getSalary()));
        txtUsername.setText(sa.getUsername());
        txtPassword.setText(sa.getPassword());
        txtAccountID.setText(String.valueOf(sa.getAccountID()));

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
            if (currentAccountID < 0)
                throw new Exception("Vui lòng tạo Account trước");

            Staff s = new Staff();
            s.setFullName(txtName.getText());
            s.setGender(cbGender.getSelectedItem().toString());
            s.setPhoneNumber(txtPhone.getText());
            s.setSalary(Double.parseDouble(txtSalary.getText()));
            s.setAccountID(currentAccountID);

            staffBUS.addStaff(s);
            loadStaffTable("");
            resetToCreateMode();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateAll() {
        try {
            Account acc = new Account();
            acc.setAccountID(Integer.parseInt(txtAccountID.getText()));
            acc.setUsername(txtUsername.getText());
            acc.setPassword(new String(txtPassword.getPassword()));
            acc.setRole(cbRole.getSelectedItem().toString());
            accountBUS.updateAccount(acc);

            Staff s = new Staff();
            s.setStaffID(Integer.parseInt(txtStaffID.getText()));
            s.setFullName(txtName.getText());
            s.setGender(cbGender.getSelectedItem().toString());
            s.setPhoneNumber(txtPhone.getText());
            s.setSalary(Double.parseDouble(txtSalary.getText()));
            staffBUS.updateStaff(s);

            loadStaffTable("");
            resetToCreateMode();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteStaff() {
        try {
            staffBUS.deleteStaff(Integer.parseInt(txtStaffID.getText()));
            loadStaffTable("");
            resetToCreateMode();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /* ================= STATE ================= */

    private void lockStaff(boolean lock) {
        txtName.setEnabled(!lock);
        cbGender.setEnabled(!lock);
        txtPhone.setEnabled(!lock);
        txtSalary.setEnabled(!lock);
    }

    private void resetToCreateMode() {
        txtStaffID.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtSalary.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtAccountID.setText("");
        cbRole.setSelectedIndex(0);
        currentAccountID = -1;
        table.clearSelection();
        lockStaff(true);
    }

    /* ================= UI HELPERS ================= */

    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        TitledBorder t = BorderFactory.createTitledBorder(title);
        t.setTitleFont(FONT_BOLD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        p.setBorder(t);
        return p;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,10,8,10);
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


}
