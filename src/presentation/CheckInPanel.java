package presentation;

import business.CheckInBUS;
import model.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CheckInPanel extends JPanel {

    private JTextField txtPhone;
    private JLabel lblName, lblPhone, lblGender, lblJoinDate;

    private final CheckInBUS checkInBUS = new CheckInBUS();

    public CheckInPanel() {
        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        /* ================= TITLE ================= */
        JLabel title = new JLabel("CHECK-IN HỘI VIÊN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        /* ================= CONTENT ================= */
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        add(content, BorderLayout.CENTER);

        /* ================= CHECK-IN AREA ================= */
        JPanel checkInBox = cardPanel();
        checkInBox.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 14));

        JLabel lblInput = new JLabel("Số điện thoại:");
        lblInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtPhone = new JTextField(18);
        txtPhone.setPreferredSize(new Dimension(220, 36));

        JButton btnCheckIn = new JButton("CHECK-IN");
        btnCheckIn.setBackground(new Color(33, 150, 243));
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckIn.setFocusPainted(false);
        btnCheckIn.setPreferredSize(new Dimension(130, 36));

        checkInBox.add(lblInput);
        checkInBox.add(txtPhone);
        checkInBox.add(btnCheckIn);

        content.add(checkInBox);
        content.add(Box.createVerticalStrut(24));

        /* ================= RESULT TITLE ================= */
        JLabel resultTitle = new JLabel("KẾT QUẢ CHECK-IN");
        resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(resultTitle);
        content.add(Box.createVerticalStrut(10));

        /* ================= RESULT CARD ================= */
        JPanel resultCard = new JPanel(new BorderLayout());
        resultCard.setBackground(Color.WHITE);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(22, 28, 22, 28)
        ));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(resultCard);

        /* --------- LEFT COLOR STRIP --------- */
        JPanel leftStrip = new JPanel();
        leftStrip.setPreferredSize(new Dimension(5, 1));
        leftStrip.setBackground(new Color(33, 150, 243));
        resultCard.add(leftStrip, BorderLayout.WEST);

        /* --------- INFO CONTENT --------- */
        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);
        resultCard.add(info, BorderLayout.CENTER);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.anchor = GridBagConstraints.WEST;

        /* STATUS */
        JLabel lblStatus = new JLabel("CHECK-IN THÀNH CÔNG");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblStatus.setForeground(new Color(34, 150, 80));
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        info.add(lblStatus, g);

        g.gridwidth = 1;

        /* HỌ TÊN */
        g.gridy++;
        g.gridx = 0;
        info.add(label("Họ tên:"), g);
        g.gridx = 1;
        lblName = valueLabel(true);
        info.add(lblName, g);

        /* SỐ ĐIỆN THOẠI */
        g.gridy++;
        g.gridx = 0;
        info.add(label("Số điện thoại:"), g);
        g.gridx = 1;
        lblPhone = valueLabel(false);
        info.add(lblPhone, g);

        /* GIỚI TÍNH */
        g.gridy++;
        g.gridx = 0;
        info.add(label("Giới tính:"), g);
        g.gridx = 1;
        lblGender = valueLabel(false);
        info.add(lblGender, g);

        /* NGÀY THAM GIA */
        g.gridy++;
        g.gridx = 0;
        info.add(label("Ngày tham gia:"), g);
        g.gridx = 1;
        lblJoinDate = valueLabel(false);
        info.add(lblJoinDate, g);

        /* ================= EVENT ================= */
        btnCheckIn.addActionListener(e -> handleCheckIn());
        txtPhone.addActionListener(e -> handleCheckIn());
    }

    /* ================= UI HELPERS ================= */

    private JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(18, 20, 18, 20)
        ));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JLabel valueLabel(boolean highlight) {
        JLabel v = new JLabel("-");
        v.setFont(new Font(
                "Segoe UI",
                highlight ? Font.BOLD : Font.PLAIN,
                highlight ? 16 : 14
        ));
        return v;
    }

    /* ================= CHECK-IN LOGIC ================= */

    private void handleCheckIn() {
        try {
            String phone = txtPhone.getText().trim();

            if (!phone.matches("\\d{9,11}")) {
                throw new Exception("Số điện thoại không hợp lệ");
            }

            Member m = checkInBUS.checkIn(phone);

            lblName.setText(m.getFullName());
            lblPhone.setText(m.getPhoneNumber());
            lblGender.setText(m.getGender());
            lblJoinDate.setText(
                    m.getJoinDate() != null
                            ? m.getJoinDate().toLocalDate().toString()
                            : "-"
            );

            txtPhone.setText("");
            txtPhone.requestFocus();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Check-in thất bại",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
