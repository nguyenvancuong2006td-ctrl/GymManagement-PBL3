package presentation;

import business.WorkoutScheduleBUS;
import model.WorkoutSchedule;
import model.Role;
import util.Session;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WorkoutScheduleUI extends JPanel {

    // ===== FORM =====
    private JTextField txtID, txtDate, txtStart, txtEnd, txtMemberID, txtTrainerID;

    // ===== TABLE & SEARCH =====
    private JTable table;
    private JTextField txtSearch;

    // ===== BUTTON =====
    private JButton btnAdd, btnUpdate, btnDelete, btnCancel, btnClear;

    private final WorkoutScheduleBUS bus = new WorkoutScheduleBUS();
    private List<WorkoutSchedule> allSchedules = new ArrayList<>();

    public WorkoutScheduleUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
        applyUiPermission();
        clearForm();
    }

    /* ================= FORM ================= */

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Workout Schedule"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtID = field(false);
        txtDate = field(true);
        txtStart = field(true);
        txtEnd = field(true);
        txtMemberID = field(true);
        txtTrainerID = field(true);

        g.gridy = 0;
        addField(form, g, 0, "ID", txtID);
        addField(form, g, 2, "Date (yyyy-MM-dd)", txtDate);

        g.gridy++;
        addField(form, g, 0, "Start (HH:mm)", txtStart);
        addField(form, g, 2, "End (HH:mm)", txtEnd);

        g.gridy++;
        addField(form, g, 0, "Member ID", txtMemberID);
        addField(form, g, 2, "Trainer ID", txtTrainerID);

        panel.add(form, BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private void addField(JPanel p, GridBagConstraints g, int x, String label, JTextField field) {
        g.gridx = x;
        p.add(new JLabel(label + ":"), g);
        g.gridx = x + 1;
        field.setPreferredSize(new Dimension(150, 28));
        p.add(field, g);
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

        btnAdd = btn("Add", new Color(76, 175, 80));
        btnUpdate = btn("Update", new Color(255, 152, 0));
        btnDelete = btn("Delete", new Color(220, 80, 80));
        btnCancel = btn("Cancel", new Color(120, 120, 120));
        btnClear = btn("Clear", new Color(160, 160, 160));

        btnAdd.addActionListener(e -> addSchedule());
        btnUpdate.addActionListener(e -> updateSchedule());
        btnDelete.addActionListener(e -> deleteSchedule());
        btnCancel.addActionListener(e -> cancelSchedule());
        btnClear.addActionListener(e -> clearForm());

        p.add(btnAdd);
        p.add(btnUpdate);
        p.add(btnDelete);
        p.add(btnCancel);
        p.add(btnClear);
        return p;
    }

    /* ================= TABLE ================= */

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Schedule List"));

        txtSearch = new JTextField();
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
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    /* ================= DATA ================= */

    private void loadData() {
        allSchedules = bus.getAll();
        fillTable(allSchedules);
    }

    private void fillTable(List<WorkoutSchedule> list) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Date", "Start", "End", "Member", "Trainer", "Status"}, 0
        );

        for (WorkoutSchedule w : list) {
            model.addRow(new Object[]{
                    w.getScheduleID(),
                    w.getDate(),
                    w.getStartTime(),
                    w.getEndTime(),
                    w.getMemberID(),
                    w.getTrainerID(),
                    w.getStatus()
            });
        }

        table.setModel(model);
        colorizeStatus();
    }

    private void colorizeStatus() {
        table.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {

                        Component c = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        if (!isSelected) {
                            String s = value == null ? "" : value.toString();
                            if ("BOOKED".equals(s)) c.setForeground(new Color(33, 150, 243));
                            else if ("COMPLETED".equals(s)) c.setForeground(new Color(76, 175, 80));
                            else if ("CANCELLED".equals(s)) c.setForeground(new Color(220, 80, 80));
                            else c.setForeground(Color.DARK_GRAY);
                        }
                        return c;
                    }
                });
    }

    /* ================= FILTER ================= */

    private void filter() {
        String key = txtSearch.getText().trim().toLowerCase();
        List<WorkoutSchedule> rs = new ArrayList<>();

        for (WorkoutSchedule w : allSchedules) {
            if (String.valueOf(w.getMemberID()).contains(key)
                    || String.valueOf(w.getTrainerID()).contains(key)
                    || (w.getStatus() != null && w.getStatus().toLowerCase().contains(key))) {
                rs.add(w);
            }
        }
        fillTable(rs);
    }

    /* ================= CRUD ================= */

    private WorkoutSchedule getFormData() {
        WorkoutSchedule w = new WorkoutSchedule();
        if (!txtID.getText().isEmpty())
            w.setScheduleID(Integer.parseInt(txtID.getText()));

        w.setDate(LocalDate.parse(txtDate.getText()));
        w.setStartTime(LocalTime.parse(txtStart.getText()));
        w.setEndTime(LocalTime.parse(txtEnd.getText()));
        w.setMemberID(Integer.parseInt(txtMemberID.getText()));
        w.setTrainerID(Integer.parseInt(txtTrainerID.getText()));
        return w;
    }

    private void addSchedule() {
        bus.add(getFormData());
        loadData();
        clearForm();
    }

    private void updateSchedule() {
        bus.update(getFormData());
        loadData();
        clearForm();
    }

    private void deleteSchedule() {
        bus.delete(Integer.parseInt(txtID.getText()));
        loadData();
        clearForm();
    }

    private void cancelSchedule() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        WorkoutSchedule w = new WorkoutSchedule();
        w.setScheduleID(Integer.parseInt(table.getValueAt(r, 0).toString()));
        w.setMemberID(Integer.parseInt(table.getValueAt(r, 4).toString()));
        w.setStatus(table.getValueAt(r, 6).toString());

        bus.cancel(w);
        loadData();
        clearForm();
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtID.setText(table.getValueAt(r, 0).toString());
        txtDate.setText(table.getValueAt(r, 1).toString());
        txtStart.setText(table.getValueAt(r, 2).toString());
        txtEnd.setText(table.getValueAt(r, 3).toString());
        txtMemberID.setText(table.getValueAt(r, 4).toString());
        txtTrainerID.setText(table.getValueAt(r, 5).toString());

        boolean editable = "BOOKED".equals(table.getValueAt(r, 6).toString());
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);
        btnCancel.setEnabled(editable);
    }

    private void clearForm() {
        txtID.setText("");
        txtDate.setText("");
        txtStart.setText("");
        txtEnd.setText("");
        txtMemberID.setText("");
        txtTrainerID.setText("");
        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCancel.setEnabled(false);
    }

    /* ================= PERMISSION ================= */

    private void applyUiPermission() {
        boolean isAdmin = Session.getRole() == Role.Admin;
        btnUpdate.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
        btnCancel.setVisible(isAdmin);
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
}