package presentation;

import business.MembershipPackageBUS;
import model.MembershipPackage;
import model.Role;
import util.Session;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipPackageUI extends JPanel {

    // ===== FORM =====
    private JTextField txtID, txtName, txtDuration, txtPrice;

    // ===== TABLE & SEARCH =====
    private JTable table;
    private JTextField txtSearch;

    // ===== DATA =====
    private final MembershipPackageBUS bus = new MembershipPackageBUS();
    private List<MembershipPackage> allPackages = new ArrayList<>();

    public MembershipPackageUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createFormWrapper(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
        applyUiPermission();
        clearForm();
    }

    /* =========================================================
                            FORM (REDESIGNED)
       ========================================================= */

    private JPanel createFormWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = createCard("Membership Package");
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(650, 200));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 16, 10, 16);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtID = field(false);
        txtName = field(true);
        txtDuration = field(true);
        txtPrice = field(true);

        // ===== ROW 0 =====
        g.gridy = 0;
        addFormField(card, g, 0, "ID", txtID);
        addFormField(card, g, 2, "Package Name", txtName);

        // ===== ROW 1 =====
        g.gridy = 1;
        addFormField(card, g, 0, "Duration (Months)", txtDuration);
        addFormField(card, g, 2, "Price", txtPrice);

        // ===== BUTTON =====
        JButton btnAdd = primaryButton("Add");
        JButton btnUpdate = secondaryButton("Update");
        JButton btnDelete = dangerButton("Delete");
        JButton btnClear = secondaryButton("Clear");

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
        JLabel lb = new JLabel(label + ":");
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lb, g);

        g.gridx = x + 1;
        g.weightx = 1;
        field.setPreferredSize(new Dimension(220, 28));
        panel.add(field, g);
    }

    /* =========================================================
                          TABLE + SEARCH
       ========================================================= */

    private JPanel createTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JPanel card = createCard("Package List");
        card.setLayout(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 28));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void update() { filterTable(txtSearch.getText()); }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        table = new JTable();
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        card.add(searchPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        wrapper.add(card);
        return wrapper;
    }

    /* =========================================================
                               DATA
       ========================================================= */

    private void loadData() {
        allPackages = bus.getAll();
        filterTable("");
    }

    private void filterTable(String keyword) {
        String key = keyword == null ? "" : keyword.toLowerCase();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Duration", "Price"}, 0
        );

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

    /* =========================================================
                               CRUD
       ========================================================= */

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

    /* =========================================================
                         UI PERMISSION
       ========================================================= */

    private void applyUiPermission() {
        boolean isAdmin = Session.getRole() == Role.Admin;
        txtName.setEnabled(isAdmin);
        txtDuration.setEnabled(isAdmin);
        txtPrice.setEnabled(isAdmin);
    }

    /* =========================================================
                             HELPERS
       ========================================================= */

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

    private JTextField field(boolean enable) {
        JTextField f = new JTextField();
        f.setEnabled(enable);
        return f;
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
