package presentation;

import business.MembershipPackageBUS;
import model.MembershipPackage;
import model.Role;
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

public class MembershipPackageUI extends JPanel {

    /* ===== STYLE ===== */
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);

    /* ===== FORM FIELD ===== */
    private JTextField txtID, txtName, txtDuration, txtPrice;

    /* ===== BUTTONS ===== */
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    /* ===== TABLE & SEARCH ===== */
    private JTable table;
    private JTextField txtSearch;

    /* ===== DATA ===== */
    private final MembershipPackageBUS bus = new MembershipPackageBUS();
    private List<MembershipPackage> allPackages = new ArrayList<>();

    public MembershipPackageUI() {
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

    /* ================= GLOBAL STYLE ================= */

    private void applyGlobalStyle() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("TextField.font", FONT_NORMAL);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("TableHeader.font", FONT_BOLD);
    }

    /* ================= FORM ================= */

    private JPanel createFormWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = createCard("Membership Package");
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(720, 180));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 16, 10, 16);
        g.fill = GridBagConstraints.HORIZONTAL;

        // ID KHÔNG HIỂN THỊ – CHỈ DÙNG NỘI BỘ
        txtID = new JTextField();
        txtID.setEnabled(false);

        txtName = new JTextField();
        txtDuration = new JTextField();
        txtPrice = new JTextField();

        // ROW 1
        g.gridy = 0;
        addFormField(card, g, 0, "Package Name", txtName);
        addFormField(card, g, 2, "Duration (Months)", txtDuration);

        // ROW 2
        g.gridy = 1;
        addFormField(card, g, 0, "Price", txtPrice);

        // BUTTONS
        btnAdd = primaryButton("Add");
        btnUpdate = secondaryButton("Update");
        btnDelete = dangerButton("Delete");
        btnClear = secondaryButton("Clear");

        btnAdd.addActionListener(e -> addPackage());
        btnUpdate.addActionListener(e -> updatePackage());
        btnDelete.addActionListener(e -> deletePackage());
        btnClear.addActionListener(e -> clearForm());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 4;
        g.anchor = GridBagConstraints.EAST;
        card.add(btnPanel, g);

        wrapper.add(card);
        return wrapper;
    }

    private void addFormField(JPanel panel, GridBagConstraints g,
                              int x, String label, JTextField field) {
        g.gridx = x;
        g.weightx = 0;
        panel.add(new JLabel(label + ":"), g);

        g.gridx = x + 1;
        g.weightx = 1;
        panel.add(field, g);
    }

    /* ================= TABLE ================= */

    private JPanel createTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JPanel card = createCard("Package List");
        card.setLayout(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField();
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void update() { filterTable(txtSearch.getText()); }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        table = new JTable();
        table.setRowHeight(28);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        card.add(searchPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        wrapper.add(card);
        return wrapper;
    }

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

    /* ================= DATA ================= */

    private void loadData() {
        allPackages = bus.getAll();
        filterTable("");
    }

    private void filterTable(String keyword) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Duration", "Price"}, 0
        );

        String key = keyword.toLowerCase();

        for (MembershipPackage p : allPackages) {
            if (p.getPackageName().toLowerCase().contains(key)) {
                model.addRow(new Object[]{
                        p.getPackageID(),
                        p.getPackageName(),
                        p.getDuration(),
                        p.getPrice()
                });
            }
        }
        table.setModel(model);
    }

    /* ================= CRUD ================= */

    private MembershipPackage getFormData() {
        MembershipPackage p = new MembershipPackage();
        p.setPackageName(txtName.getText());
        p.setDuration(Integer.parseInt(txtDuration.getText()));
        p.setPrice(Double.parseDouble(txtPrice.getText()));
        return p;
    }

    private void addPackage() {
        bus.add(getFormData());
        loadData();
        clearForm();
    }

    private void updatePackage() {
        if (txtID.getText().isEmpty()) return;
        MembershipPackage p = getFormData();
        p.setPackageID(Integer.parseInt(txtID.getText()));
        bus.update(p);
        loadData();
        clearForm();
    }

    private void deletePackage() {
        if (txtID.getText().isEmpty()) return;
        bus.delete(Integer.parseInt(txtID.getText()));
        loadData();
        clearForm();
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtID.setText(table.getValueAt(r, 0).toString());
        txtName.setText(table.getValueAt(r, 1).toString());
        txtDuration.setText(table.getValueAt(r, 2).toString());
        txtPrice.setText(table.getValueAt(r, 3).toString());
    }

    private void clearForm() {
        txtID.setText("");
        txtName.setText("");
        txtDuration.setText("");
        txtPrice.setText("");
        table.clearSelection();
    }

    /* ================= PERMISSION ================= */

    private void applyUiPermission() {
        boolean isAdmin = Session.getRole() == Role.Admin;
        btnAdd.setVisible(isAdmin);
        btnUpdate.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
        btnClear.setVisible(isAdmin);
    }

    /* ================= HELPERS ================= */

    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        TitledBorder t = BorderFactory.createTitledBorder(title);
        t.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        p.setBorder(t);
        return p;
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