package presentation;

import business.MembershipPackageBUS;
import model.MembershipPackage;
import util.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AddPackageDialog extends JDialog {

    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtDuration;
    private JComboBox<String> cbType;

    private boolean saved = false;

    public AddPackageDialog(Window owner) {
        super(owner, "Membership Package", ModalityType.APPLICATION_MODAL);

        setSize(520, 250);
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
    }

    private void initUI() {

        // ===== ROOT =====
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(15, 20, 15, 20));

        // ===== CARD =====
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 230, 240), 1, true));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== ROW 1 =====
        // Label Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Package Name:"), gbc);

        // Text Name
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtName = createTextField();
        card.add(txtName, gbc);

        // Label Duration
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(new JLabel("Duration:"), gbc);

        // Text Duration
        gbc.gridx = 3;
        gbc.weightx = 1;
        txtDuration = createTextField();
        card.add(txtDuration, gbc);

        // ===== ROW 2 =====
        // Label Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        card.add(new JLabel("Type:"), gbc);

        // Combo Type
        gbc.gridx = 1;
        gbc.weightx = 1;
        cbType = new JComboBox<>(new String[]{"DAY", "MONTH"});
        cbType.setPreferredSize(new Dimension(120, 32));
        cbType.setSelectedIndex(1); // mặc định MONTH
        card.add(cbType, gbc);

        // Label Price
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(new JLabel("Price (đ):"), gbc);

        // Text Price
        gbc.gridx = 3;
        gbc.weightx = 1;
        txtPrice = createTextField();
        card.add(txtPrice, gbc);

        root.add(card, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(new Color(245, 247, 250));

        JButton btnCancel = new RoundedButton("Hủy", new Color(180, 180, 180));
        JButton btnSave = new RoundedButton("Lưu", new Color(52, 152, 219));

        btnCancel.setPreferredSize(new Dimension(90, 34));
        btnSave.setPreferredSize(new Dimension(90, 34));

        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePackage());

        bottom.add(btnCancel);
        bottom.add(btnSave);

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ===== TEXTFIELD =====
    private JTextField createTextField() {
        JTextField txt = new JTextField(10);

        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(140, 32));

        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 220, 230), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        return txt;
    }

    // ===== SAVE =====
    private void savePackage() {
        try {
            MembershipPackage pkg = new MembershipPackage();

            String name = txtName.getText().trim();
            String durationText = txtDuration.getText().trim();
            String priceText = txtPrice.getText().trim();

            // ✅ validate cơ bản
            if (name.isEmpty() || durationText.isEmpty() || priceText.isEmpty()) {
                throw new Exception("Vui lòng nhập đầy đủ thông tin!");
            }

            int duration = Integer.parseInt(durationText);
            double price = Double.parseDouble(priceText.replace(",", "").replace(".", ""));

            String type = cbType.getSelectedItem().toString();

            pkg.setPackageName(name);
            pkg.setDurationType(type);

            if ("DAY".equals(type)) {
                pkg.setDuration(Math.max(1, duration / 30));
            } else {
                pkg.setDuration(duration);
            }

            pkg.setPrice(price);

            new MembershipPackageBUS().add(pkg);

            saved = true;

            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sai định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
