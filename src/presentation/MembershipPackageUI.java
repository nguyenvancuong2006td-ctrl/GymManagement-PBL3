package presentation;

import business.MembershipPackageBUS;
import model.MembershipPackage;
import model.Role;
import util.RoundedButton;
import util.Session;

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

    private JComboBox<String> cbType;

    /* ===== PRICE FORMATTER ===== */
    private final NumberFormat priceFormat =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

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

        txtID = new JTextField();
        txtID.setEnabled(false);

        txtName = new JTextField();
        txtDuration = new JTextField();
        txtPrice = new JTextField();
        cbType = new JComboBox<>(new String[]{"DAY", "MONTH"});

        // ===== ROW 1 =====
        g.gridy = 0;
        addFormField(card, g, 0, "Package Name", txtName);
        addFormField(card, g, 2, "Duration", txtDuration);

        // ===== ROW 2 =====
        g.gridy = 1;
        addFormField(card, g, 0, "Type", cbType);
        addFormField(card, g, 2, "Price (₫)", txtPrice);


        btnAdd = new RoundedButton("Add", new Color(46, 204, 113));
        btnUpdate = new RoundedButton("Update", new Color(52, 152, 219));
        btnDelete = new RoundedButton("Delete", new Color(231, 76, 60));
        btnClear = new RoundedButton("Clear", new Color(149, 165, 166));

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
                              int x, String label, JComponent field) {
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
        table.setDefaultEditor(Object.class, null);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        card.add(searchPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        wrapper.add(card);
        return wrapper;
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

        for (MembershipPackage p : allPackages) {
            if (p.getPackageName().toLowerCase().contains(keyword.toLowerCase())) {

                String durationText =
                        "DAY".equalsIgnoreCase(p.getDurationType())
                                ? p.getDuration() + " ngày"
                                : p.getDuration() + " tháng";
                model.addRow(new Object[]{
                        p.getPackageID(),
                        p.getPackageName(),
                        durationText,
                        p.getPrice()
                });

            }
        }

        table.setModel(model);


        // ID – CENTER
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // NAME – CENTER
        DefaultTableCellRenderer centerNameRenderer = new DefaultTableCellRenderer();
        centerNameRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerNameRenderer);

        // DURATION – CENTER
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // PRICE – FORMAT + CENTER
        table.getColumnModel().getColumn(3).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    protected void setValue(Object value) {
                        if (value instanceof Number) {
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setText(priceFormat.format(value));
                        } else {
                            setText("");
                        }
                    }
                }
        );
    }

    /* ================= CRUD ================= */

    private MembershipPackage getFormData() {
        MembershipPackage p = new MembershipPackage();
        p.setPackageName(txtName.getText());

        String raw = txtDuration.getText().replaceAll("[^0-9]", "");
        p.setDuration(Integer.parseInt(raw));

        p.setDurationType(cbType.getSelectedItem().toString());

        double price = Double.parseDouble(
                txtPrice.getText().replaceAll("[^0-9]", "")
        );
        p.setPrice(price);

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

        MembershipPackage p = allPackages.get(r);

        txtID.setText(String.valueOf(p.getPackageID()));
        txtName.setText(p.getPackageName());

        txtDuration.setText(String.valueOf(p.getDuration()));

        cbType.setSelectedItem(p.getDurationType());

        txtPrice.setText(String.valueOf((int)p.getPrice()));

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

    private void styleTable(JTable table) {

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
    }
}