package presentation;

import business.SystemBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SystemManagementPanel extends JPanel {

    private final SystemBUS bus = new SystemBUS();

    public SystemManagementPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(246, 248, 251));
        setBorder(new EmptyBorder(32, 48, 32, 48));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /* ================= HEADER ================= */

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ HỆ THỐNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel sub = new JLabel(
                "Quản trị dữ liệu và đảm bảo an toàn vận hành phần mềm"
        );
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(120, 120, 120));

        p.add(title);
        p.add(Box.createVerticalStrut(6));
        p.add(sub);
        return p;
    }

    /* ================= BODY ================= */

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(32, 0, 0, 0));

        body.add(statusCard());
        body.add(Box.createVerticalStrut(24));
        body.add(backupCard());
        body.add(Box.createVerticalStrut(16));
        body.add(restoreCard());

        return body;
    }

    /* ================= CARD: STATUS ================= */

    private JPanel statusCard() {
        JPanel card = card();

        JLabel title = sectionTitle("Trạng thái hệ thống");

        JLabel content = new JLabel(
                "Dữ liệu hội viên • Hóa đơn • Thanh toán • Báo cáo"
        );
        content.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        content.setForeground(new Color(70, 70, 70));

        card.add(title, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /* ================= CARD: BACKUP ================= */

    private JPanel backupCard() {
        JPanel card = card();

        JLabel title = sectionTitle("Sao lưu dữ liệu");

        JLabel desc = new JLabel(
                "<html>Sao lưu toàn bộ dữ liệu của hệ thống.<br>" +
                        "Dữ liệu sao lưu được lưu tại thư mục backup của SQL Server.</html>"
        );
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btn = primaryButton("Sao lưu ngay", new Color(52, 152, 219));
        btn.addActionListener(e -> {
            if (bus.backup()) {
                JOptionPane.showMessageDialog(this,
                        "Sao lưu dữ liệu thành công",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Sao lưu dữ liệu thất bại",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createVerticalStrut(8));
        left.add(desc);

        card.add(left, BorderLayout.CENTER);
        card.add(btn, BorderLayout.EAST);
        return card;
    }

    /* ================= CARD: RESTORE ================= */

    private JPanel restoreCard() {
        JPanel card = card();
        card.setBackground(new Color(255, 245, 245));

        JLabel title = sectionTitle("Phục hồi dữ liệu");
        title.setForeground(new Color(176, 58, 46));

        JLabel desc = new JLabel(
                "<html><b>CẢNH BÁO:</b> Phục hồi dữ liệu sẽ ghi đè<br>" +
                        "toàn bộ dữ liệu hiện tại của hệ thống.</html>"
        );
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btn = primaryButton("Phục hồi dữ liệu", new Color(231, 76, 60));
        btn.addActionListener(e -> confirmRestore());

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createVerticalStrut(8));
        left.add(desc);

        card.add(left, BorderLayout.CENTER);
        card.add(btn, BorderLayout.EAST);
        return card;
    }

    /* ================= LOGIC ================= */

    private void confirmRestore() {
        int c = JOptionPane.showConfirmDialog(
                this,
                "Phục hồi sẽ ghi đè toàn bộ dữ liệu hiện tại.\n" +
                        "Bạn có chắc chắn muốn tiếp tục?",
                "Xác nhận phục hồi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (c == JOptionPane.YES_OPTION) {
            if (bus.restore()) {
                JOptionPane.showMessageDialog(this,
                        "Phục hồi dữ liệu thành công.\nVui lòng khởi động lại phần mềm.",
                        "Hoàn tất",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Phục hồi dữ liệu thất bại",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================= FOOTER ================= */

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel f = new JLabel("Gym Management System • Phiên bản 1.0");
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(Color.GRAY);

        p.add(f, BorderLayout.EAST);
        return p;
    }

    /* ================= UI HELPERS ================= */

    private JPanel card() {
        JPanel p = new JPanel(new BorderLayout(24, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 17));
        return l;
    }

    private JButton primaryButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(180, 44));
        return b;
    }
}
