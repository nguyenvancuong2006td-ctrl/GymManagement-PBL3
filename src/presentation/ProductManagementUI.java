package presentation;

import business.ProductBUS;
import model.Product;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductManagementUI extends JPanel {

    private JTextField txtName, txtCategory, txtPrice, txtStock, txtImage, txtSearch;
    private JLabel lblImage;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    private final ProductBUS productBUS = new ProductBUS();

    public ProductManagementUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        add(title(), BorderLayout.NORTH);
        add(body(), BorderLayout.CENTER);

        loadTable();
        setEditMode(false);
    }

    /* ================= TITLE ================= */

    private JLabel title() {
        JLabel lbl = new JLabel("Product Management");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return lbl;
    }

    /* ================= BODY ================= */

    private JPanel body() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(productInfo());
        body.add(Box.createVerticalStrut(15));
        body.add(productList());
        return body;
    }

    /* ================= FORM ================= */

    private JPanel productInfo() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createTitledBorder("Product Information"));

        lblImage = new JLabel("No Image", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(120,120));
        lblImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton choose = new JButton("Choose Image");
        choose.addActionListener(e -> chooseImage());

        JPanel img = new JPanel(new BorderLayout());
        img.add(lblImage, BorderLayout.CENTER);
        img.add(choose, BorderLayout.SOUTH);

        txtName = new JTextField();
        txtCategory = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        txtImage = new JTextField();

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.add(new JLabel("Name"));     form.add(txtName);
        form.add(new JLabel("Category")); form.add(txtCategory);
        form.add(new JLabel("Price"));    form.add(txtPrice);
        form.add(new JLabel("Quantity")); form.add(txtStock);

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        JPanel right = new JPanel(new BorderLayout());
        right.add(form, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        p.add(img, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);

        return p;
    }

    /* ================= TABLE ================= */

    private JPanel productList() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createTitledBorder("Product List"));

        txtSearch = new JTextField();
        JPanel search = new JPanel(new BorderLayout());
        search.add(new JLabel("Search: "), BorderLayout.WEST);
        search.add(txtSearch, BorderLayout.CENTER);

        model = new DefaultTableModel(
                new String[]{"ID","Name","Category","Price","Initial","In Stock","Sold"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        p.add(search, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    /* ================= DATA ================= */

    private void loadTable() {
        model.setRowCount(0);
        List<Product> list = productBUS.getAllForManagement();

        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getProductID(),
                    p.getProductName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getQuantityInitial(),
                    p.getQuantityInStock(),
                    p.getQuantitySold()
            });
        }
    }

    private void filter() {
        sorter.setRowFilter(
                RowFilter.regexFilter("(?i)" + txtSearch.getText())
        );
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        int m = table.convertRowIndexToModel(r);
        txtName.setText(model.getValueAt(m,1).toString());
        txtCategory.setText(model.getValueAt(m,2).toString());
        txtPrice.setText(model.getValueAt(m,3).toString());
        txtStock.setText(model.getValueAt(m,5).toString());

        setEditMode(true);
    }

    /* ================= CRUD ================= */

    private Product getFormData() {
        Product p = new Product();

        p.setProductName(txtName.getText().trim());
        p.setCategory(txtCategory.getText().trim());

        try {
            p.setPrice(new BigDecimal(txtPrice.getText().trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }

        try {
            p.setQuantityInStock(Integer.parseInt(txtStock.getText().trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số lượng tồn kho không hợp lệ!");
        }

        p.setImagePath(txtImage.getText().trim());
        return p;
    }

    private void addProduct() {
        try {
            productBUS.addProduct(getFormData());
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        try {
            int m = table.convertRowIndexToModel(r);
            Product p = getFormData();

            p.setProductID((int) model.getValueAt(m, 0));
            p.setQuantityInitial((int) model.getValueAt(m, 4)); // giữ quantityInitial

            productBUS.updateProduct(p);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        int m = table.convertRowIndexToModel(r);
        int id = (int) model.getValueAt(m,0);

        if (JOptionPane.showConfirmDialog(this,
                "Xoá sản phẩm này?",
                "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                productBUS.deleteProduct(id);
                loadTable();
                clearForm();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================= UI HELP ================= */

    private void clearForm() {
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtImage.setText("");

        lblImage.setIcon(null);
        lblImage.setText("No Image");

        table.clearSelection();
        setEditMode(false);
    }

    private void setEditMode(boolean editing) {
        btnUpdate.setEnabled(editing);
        btnDelete.setEnabled(editing);
    }

    private void chooseImage() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtImage.setText(fc.getSelectedFile().getAbsolutePath());
            ImageIcon icon = new ImageIcon(txtImage.getText());
            Image img = icon.getImage().getScaledInstance(
                    120,120,Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
            lblImage.setText("");
        }
    }
}
