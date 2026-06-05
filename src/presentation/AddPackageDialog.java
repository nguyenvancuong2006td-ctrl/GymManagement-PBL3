package presentation;

import business.MembershipPackageBUS;
import model.MembershipPackage;
import util.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class AddPackageDialog extends JDialog {

    private JTextField txtName;
    private JTextField txtPrice;
    private JLabel lblPreviewPrice;
    private JSpinner spDuration;

    private boolean saved = false;

    public AddPackageDialog(Window owner) {
        super(owner, "Thêm gói tập", ModalityType.APPLICATION_MODAL);

        setSize(460, 350);
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
    }

    private void initUI() {

        // ===== ROOT =====
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(240, 244, 249));

        // ===== CARD =====
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(380, 280));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 230, 240), 1, true),
                new EmptyBorder(10, 0, 0, 0)
        ));

        root.add(card);

        /* ===== TITLE ===== */
        JLabel title = new JLabel("THÊM GÓI TẬP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(10, 15, 5, 0));
        card.add(title, BorderLayout.NORTH);

        /* ===== FORM ===== */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(5, 15, 5, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        // NAME
        gbc.gridy = 0;
        form.add(createLabel("Tên gói tập", labelFont), gbc);

        gbc.gridy = 1;
        txtName = createTextField();
        form.add(txtName, gbc);

        // DURATION
        gbc.gridy = 2;
        form.add(createLabel("Thời hạn (tháng)", labelFont), gbc);

        gbc.gridy = 3;
        spDuration = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));
        spDuration.setPreferredSize(new Dimension(0, 34));

        JComponent editor = spDuration.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBorder(new EmptyBorder(6, 10, 6, 10));
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        form.add(spDuration, gbc);

        // PRICE
        gbc.gridy = 4;
        form.add(createLabel("Giá (VNĐ)", labelFont), gbc);

        gbc.gridy = 5;
        txtPrice = createTextField();
        form.add(txtPrice, gbc);

        // PREVIEW
        gbc.gridy = 6;
        JPanel preview = new JPanel(new BorderLayout());
        preview.setBackground(new Color(248, 250, 253));
        preview.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lblText = new JLabel("Tổng tiền:");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblPreviewPrice = new JLabel("0 VNĐ");
        lblPreviewPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPreviewPrice.setForeground(new Color(39, 174, 96));

        preview.add(lblText, BorderLayout.WEST);
        preview.add(lblPreviewPrice, BorderLayout.EAST);

        form.add(preview, gbc);

        // update realtime
        txtPrice.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePrice(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePrice(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePrice(); }
        });

        card.add(form, BorderLayout.CENTER);

        /* ===== BUTTON ===== */
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(Color.WHITE);

        JButton btnCancel = new RoundedButton("Hủy", new Color(220, 225, 230));
        JButton btnSave = new RoundedButton("Lưu", new Color(40, 120, 200));

        btnCancel.setPreferredSize(new Dimension(90, 34));
        btnSave.setPreferredSize(new Dimension(90, 34));

        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePackage());

        bottom.add(btnCancel);
        bottom.add(btnSave);

        card.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /* ===== UI COMPONENT ===== */
    private JTextField createTextField() {
        JTextField txt = new JTextField();

        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(0, 34));

        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 220, 230), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        return txt;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    /* ===== LOGIC ===== */
    private void updatePrice() {
        try {
            String raw = txtPrice.getText().replace(".", "").replace(",", "");

            if (raw.isBlank()) {
                lblPreviewPrice.setText("0 VNĐ");
                return;
            }

            double price = Double.parseDouble(raw);
            DecimalFormat df = new DecimalFormat("#,###");

            lblPreviewPrice.setText(df.format(price) + " VNĐ");

        } catch (Exception ignored) {}
    }

    private void savePackage() {
        try {
            MembershipPackage pkg = new MembershipPackage();

            pkg.setPackageName(txtName.getText().trim());
            pkg.setDuration((Integer) spDuration.getValue());
            pkg.setPrice(Double.parseDouble(
                    txtPrice.getText().replace(".", "").replace(",", "")
            ));

            new MembershipPackageBUS().add(pkg);

            saved = true;

            JOptionPane.showMessageDialog(this, " Thêm thành công!");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}