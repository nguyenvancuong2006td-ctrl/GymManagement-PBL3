package presentation;

import business.TrainerBUS;
import model.Role;
import model.Trainer;
import util.Session;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerUI extends JPanel {

    /* ================= STYLE ================= */
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);

    /* ================= FORM ================= */
    private JTextField txtID, txtName, txtPhone, txtHireDate, txtSalary;
    private JComboBox<String> cbGender;

    /* ================= TABLE ================= */
    private JTable table;
    private JTextField txtSearch;

    /* ================= BUTTON ================= */
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    /* ================= DATA ================= */
    private final TrainerBUS trainerBUS = new TrainerBUS();
    private List<Trainer> allTrainers = new ArrayList<>();

    public TrainerUI() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        applyGlobalStyle();

        add(createFormWrapper(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
        applyUiPermission();
        clearForm();
    }

    /* =========================================================
                        GLOBAL STYLE (UI ONLY)
       ========================================================= */

    private void applyGlobalStyle() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("TextField.font", FONT_NORMAL);
        UIManager.put("ComboBox.font", FONT_NORMAL);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("TableHeader.font", FONT_BOLD);
    }

    /* =========================================================
                          FORM – CENTERED CARD
       ========================================================= */

    private JPanel createFormWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = createCard("Trainer Information");
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(760, 260));

        GridBagConstraints g = gbc();

        txtID = field(false);      // system-generated → not editable
        txtName = field(true);
        txtPhone = field(true);
        txtHireDate = field(false);
        txtSalary = field(true);

        cbGender = new JComboBox<>(new String[]{"Male", "Female"});

        addRow(card, g, 0, "ID", txtID, "Gender", cbGender);
        addRow(card, g, 1, "Full Name", txtName, "Salary", txtSalary);
        addRow(card, g, 2, "Phone", txtPhone, "Hire Date", txtHireDate);

        btnAdd = primaryButton("Add");
        btnUpdate = secondaryButton("Update");
        btnDelete = dangerButton("Delete");
        btnClear = secondaryButton("Clear");

        btnAdd.addActionListener(e -> addTrainer());
        btnUpdate.addActionListener(e -> updateTrainer());
        btnDelete.addActionListener(e -> deleteTrainer());
        btnClear.addActionListener(e -> clearForm());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 1;
        g.gridy = 3;
        g.gridwidth = 3;
        g.anchor = GridBagConstraints.EAST;
        card.add(btnPanel, g);

        wrapper.add(card);
        return wrapper;
    }

    /* =========================================================
                        TABLE + SEARCH
       ========================================================= */

    private JPanel createTablePanel() {

        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JPanel card = createCard("Trainer List");
        card.setLayout(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(FONT_BOLD);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(240, 28));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                filterTable(txtSearch.getText());
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        table = new JTable();
        table.setRowHeight(28);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scroll = new JScrollPane(table);

        card.add(searchPanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    /* =========================================================
                             TABLE STYLE (UI ONLY)
       ========================================================= */

    private void styleTable(JTable table) {
        table.getTableHeader().setBackground(new Color(245, 246, 248));
        table.getTableHeader().setForeground(new Color(60, 60, 60));
        table.setGridColor(new Color(230, 230, 230));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(248, 248, 248));
                }
                return c;
            }
        });
    }

    /* =========================================================
                             DATA (KHÔNG ĐỔI)
       ========================================================= */

    private void loadData() {
        allTrainers = trainerBUS.getAll();
        filterTable("");
    }

    private void filterTable(String keyword) {
        String key = keyword == null ? "" : keyword.toLowerCase();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Gender", "Phone", "Salary", "Hire Date"}, 0
        );

        for (Trainer t : allTrainers) {
            if (t.getFullName() != null &&
                    t.getFullName().toLowerCase().contains(key)) {

                model.addRow(new Object[]{
                        t.getTrainerID(),
                        t.getFullName(),
                        t.getGender(),
                        t.getPhoneNumber(),
                        t.getSalary(),
                        t.getHireDate()
                });
            }
        }
        table.setModel(model);
    }

    /* =========================================================
                              CRUD (KHÔNG ĐỔI)
       ========================================================= */

    private Trainer getFormData() {
        Trainer t = new Trainer();

        if (!txtID.getText().isEmpty())
            t.setTrainerID(Integer.parseInt(txtID.getText()));

        t.setFullName(txtName.getText());
        t.setGender(cbGender.getSelectedItem().toString());
        t.setPhoneNumber(txtPhone.getText());
        t.setSalary(Double.parseDouble(txtSalary.getText()));

        return t;
    }

    private void addTrainer() {
        trainerBUS.add(getFormData());
        loadData();
        clearForm();
    }

    private void updateTrainer() {
        trainerBUS.update(getFormData());
        loadData();
        clearForm();
    }

    private void deleteTrainer() {
        if (txtID.getText().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this trainer?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        trainerBUS.delete(Integer.parseInt(txtID.getText()));
        loadData();
        clearForm();
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtID.setText(table.getValueAt(r, 0).toString());
        txtName.setText(table.getValueAt(r, 1).toString());
        cbGender.setSelectedItem(table.getValueAt(r, 2));
        txtPhone.setText(table.getValueAt(r, 3).toString());
        txtSalary.setText(table.getValueAt(r, 4).toString());
        txtHireDate.setText(String.valueOf(table.getValueAt(r, 5)));

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void clearForm() {
        txtID.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtSalary.setText("");
        txtHireDate.setText("");
        cbGender.setSelectedIndex(0);
        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    /* =========================================================
                        UI PERMISSION (KHÔNG ĐỔI)
       ========================================================= */

    private void applyUiPermission() {
        boolean isAdmin = Session.getRole() == Role.Admin;
        btnUpdate.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
        btnAdd.setVisible(isAdmin);
        btnClear.setVisible(isAdmin);
    }

    /* =========================================================
                           UI HELPERS
       ========================================================= */

    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);

        TitledBorder t = BorderFactory.createTitledBorder(title);
        t.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setTitleColor(new Color(60, 60, 60));

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        p.setBorder(t);
        return p;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 12, 10, 12);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y,
                        String l1, JComponent f1,
                        String l2, JComponent f2) {

        g.gridy = y;

        g.gridx = 0;
        p.add(new JLabel(l1), g);

        g.gridx = 1;
        f1.setPreferredSize(new Dimension(220, 28));
        p.add(f1, g);

        g.gridx = 2;
        p.add(new JLabel(l2), g);

        g.gridx = 3;
        f2.setPreferredSize(new Dimension(170, 28));
        p.add(f2, g);
    }

    private JTextField field(boolean enable) {
        JTextField f = new JTextField();
        f.setEnabled(enable);
        f.setPreferredSize(new Dimension(220, 28));
        return f;
    }

    private JButton primaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(52, 120, 208));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private JButton secondaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(200, 200, 200));
        b.setFocusPainted(false);
        return b;
    }

    private JButton dangerButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(210, 70, 70));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}