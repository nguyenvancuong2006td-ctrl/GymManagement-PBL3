package presentation;

import business.PTServiceBUS;
import business.TrainerBUS;
import model.PTService;
import model.Trainer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PTServiceManagementUI extends JPanel {

    // ===== FORM =====
    private JTextField txtTenDichVu, txtSoBuoi, txtGia;
    private JComboBox<Trainer> cboTrainer;

    // ===== BUTTON =====
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // ===== SEARCH =====
    private JTextField txtSearch;

    // ===== TABLE =====
    private JTable table;
    private DefaultTableModel model;

    // ===== DATA =====
    private final PTServiceBUS serviceBUS = new PTServiceBUS();
    private final TrainerBUS trainerBUS = new TrainerBUS();
    private List<PTService> services;

    public PTServiceManagementUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadTrainer();
        loadData();
        initSearchRealtime();
        resetButtonState();
    }

    /* =====================================================
                       FORM PANEL
       ===================================================== */

    private JPanel createFormPanel() {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Service Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        form.setBackground(Color.WHITE);

        txtTenDichVu = new JTextField();
        txtSoBuoi = new JTextField();
        txtGia = new JTextField();

        cboTrainer = new JComboBox<>();
        cboTrainer.setRenderer(new TrainerRenderer());
        cboTrainer.setPreferredSize(new Dimension(200, 28));

        form.add(new JLabel("Tên dịch vụ:"));
        form.add(txtTenDichVu);

        form.add(new JLabel("Huấn luyện viên PT:"));
        form.add(cboTrainer);

        form.add(new JLabel("Số buổi:"));
        form.add(txtSoBuoi);

        form.add(new JLabel("Giá tiền (VNĐ):"));
        form.add(txtGia);

        card.add(form, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        actions.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clearForm());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    /* =====================================================
                       TABLE PANEL
       ===================================================== */

    private JPanel createTablePanel() {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Service List",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        searchPanel.setBackground(Color.WHITE);

        searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        txtSearch = new JTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        card.add(searchPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Tên dịch vụ", "PT", "Số buổi", "Giá"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelect());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /* =====================================================
                           DATA
       ===================================================== */

    private void loadTrainer() {
        cboTrainer.removeAllItems();

        cboTrainer.addItem(null); // dòng mặc định
        for (Trainer t : trainerBUS.getAll()) {
            cboTrainer.addItem(t);
        }
    }

    private void loadData() {
        services = serviceBUS.getAll();
        showData(services);
    }

    private void showData(List<PTService> list) {
        model.setRowCount(0);
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (PTService s : list) {
            model.addRow(new Object[]{
                    s.getServiceID(),
                    s.getServiceName(),
                    s.getTrainerID(),
                    s.getTotalSessions(),
                    fmt.format(s.getPrice())
            });
        }
    }

    /* =====================================================
                           CRUD
       ===================================================== */

    private void add() {
        try {
            serviceBUS.add(getForm());
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        try {
            PTService s = getForm();
            s.setServiceID((int) model.getValueAt(row, 0));
            serviceBUS.update(s);
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        if (JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa dịch vụ này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) return;

        int id = (int) model.getValueAt(row, 0);
        serviceBUS.delete(id);
        loadData();
        clearForm();
    }

    /* =====================================================
                           UTIL
       ===================================================== */

    private PTService getForm() {
        Trainer t = (Trainer) cboTrainer.getSelectedItem();
        if (t == null)
            throw new IllegalArgumentException("Vui lòng chọn huấn luyện viên PT");

        PTService s = new PTService();
        s.setServiceName(txtTenDichVu.getText());
        s.setTrainerID(t.getTrainerID());
        s.setTotalSessions(Integer.parseInt(txtSoBuoi.getText()));
        s.setPrice(new BigDecimal(txtGia.getText()));
        return s;
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtTenDichVu.setText(model.getValueAt(row, 1).toString());
        txtSoBuoi.setText(model.getValueAt(row, 3).toString());
        txtGia.setText(model.getValueAt(row, 4).toString().replaceAll("[^0-9]", ""));

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void clearForm() {
        txtTenDichVu.setText("");
        txtSoBuoi.setText("");
        txtGia.setText("");
        cboTrainer.setSelectedIndex(0);
        table.clearSelection();
        resetButtonState();
    }

    private void resetButtonState() {
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void initSearchRealtime() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void filter() {
        String key = txtSearch.getText().toLowerCase();
        showData(
                services.stream()
                        .filter(s -> s.getServiceName().toLowerCase().contains(key))
                        .toList()
        );
    }

    /* =====================================================
                        COMBOBOX RENDERER
       ===================================================== */

    private static class TrainerRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value == null) {
                setText("-- Chọn huấn luyện viên PT --");
            } else if (value instanceof Trainer) {
                setText(((Trainer) value).getFullName());
            }

            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return this;
        }
    }
}
