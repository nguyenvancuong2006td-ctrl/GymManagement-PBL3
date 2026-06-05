package presentation;

import business.TrainerBUS;
import model.Role;
import model.Trainer;
import util.RoundedButton;
import util.Session;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static util.InvoicePDFExporter.formatMoney;

public class TrainerUI extends JPanel {

    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField txtID, txtName, txtPhone, txtHireDate, txtSalary;
    private JComboBox<String> cbGender;

    private JTable table;
    private JTextField txtSearch;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

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

    private void applyGlobalStyle() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("TextField.font", FONT_NORMAL);
        UIManager.put("ComboBox.font", FONT_NORMAL);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("TableHeader.font", FONT_BOLD);
    }

    /* ================= FORM ================= */

    private JPanel createFormWrapper() {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel card = createCard("Trainer Information");
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = gbc();

        txtID = field(false);
        txtName = field(true);
        txtPhone = field(true);
        txtHireDate = field(false);
        txtSalary = field(true);

        cbGender = new JComboBox<>(new String[]{"Male", "Female"});
        cbGender.setPreferredSize(new Dimension(200, 36));

        addRow(card, g, 0, "ID", txtID, "Gender", cbGender);
        addRow(card, g, 1, "Full Name", txtName, "Salary", txtSalary);
        addRow(card, g, 2, "Phone", txtPhone, "Hire Date", txtHireDate);

        btnAdd = new RoundedButton("Add", new Color(46, 204, 113));
        btnUpdate = new RoundedButton("Update", new Color(52, 152, 219));
        btnDelete = new RoundedButton("Delete", new Color(231, 76, 60));
        btnClear = new RoundedButton("Clear", new Color(127, 140, 141));

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

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 4;
        g.fill = GridBagConstraints.HORIZONTAL;
        card.add(btnPanel, g);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    /* ================= TABLE ================= */

    private JPanel createTablePanel() {

        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);

        JPanel card = createCard("Trainer List");
        card.setLayout(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(FONT_BOLD);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 36));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190,190,190),1,true),
                BorderFactory.createEmptyBorder(8,12,8,12)
        ));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void update() { filterTable(txtSearch.getText()); }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        table = new JTable();
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scroll = new JScrollPane(table);

        card.add(searchPanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    /* ================= TABLE STYLE ================= */

    private void styleTable(JTable table) {

        // ===== BASIC =====
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // ===== GRID CLEAN =====
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(235, 235, 235));
        table.setIntercellSpacing(new Dimension(0, 1));

        // ===== SELECTION =====
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        // ===== HEADER STYLE =====
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // ===== ZEBRA ROW =====
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
                            : new Color(248, 250, 252));
                }

                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    /* ================= DATA ================= */

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
                        formatMoney(BigDecimal.valueOf(t.getSalary())),
                        t.getHireDate()
                });
            }
        }
        table.setModel(model);

        // ===== ID (CENTER) =====
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

// ===== NAME (LEFT) =====
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(left);

        table.getColumnModel().getColumn(2).setCellRenderer(center);

        table.getColumnModel().getColumn(3).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

// ===== HIRE DATE (CENTER) =====
        table.getColumnModel().getColumn(5).setCellRenderer(center);
    }

    /* ================= CRUD ================= */

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
                this, "Delete this trainer?", "Confirm",
                JOptionPane.YES_NO_OPTION);

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

    private void applyUiPermission() {

        boolean isAdmin = Session.getRole() == Role.Admin;

        btnUpdate.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
        btnAdd.setVisible(isAdmin);
        btnClear.setVisible(isAdmin);
    }

    /* ================= UI HELPERS ================= */

    private JPanel createCard(String title) {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        TitledBorder titleBorder = BorderFactory.createTitledBorder(title);
        titleBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 15));

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220),1,true),
                BorderFactory.createCompoundBorder(
                        titleBorder,
                        BorderFactory.createEmptyBorder(15,15,15,15)
                )
        ));

        return p;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(12, 14, 12, 14);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y,
                        String l1, JComponent f1,
                        String l2, JComponent f2) {

        g.gridy = y;

        g.gridx = 0;
        g.weightx = 0;
        p.add(new JLabel(l1), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(f1, g);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        p.add(new JLabel(l2), g);

        g.gridx = 3;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(f2, g);
    }

    private JTextField field(boolean enable) {

        JTextField f = new JTextField();

        f.setEnabled(enable);
        f.setBackground(enable ? Color.WHITE : new Color(245,245,245));

        f.setPreferredSize(new Dimension(250, 36));

        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190,190,190),1,true),
                BorderFactory.createEmptyBorder(8,12,8,12)
        ));

        return f;
    }


}
