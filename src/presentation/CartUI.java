package presentation;

import business.CartBUS;
import business.CheckoutBUS;
import data.StaffDAO;
import model.Member;
import model.PaymentMethod;
import model.Product;
import model.Role;
import model.Staff;
import util.InvoicePDFExporter;
import util.Session;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class CartUI extends JDialog {

    // ===== BUSINESS =====
    private final CartBUS cartBUS;
    private final CheckoutBUS checkoutBUS = new CheckoutBUS();

    // ===== UI =====
    private JPanel listPanel;
    private JLabel lblTotal;
    private JButton btnCheckout;

    public CartUI(JFrame parent, CartBUS cartBUS) {
        super(parent, "Giỏ hàng", true);
        this.cartBUS = cartBUS;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadCart();
    }

    /* ================= UI ================= */

    private void initUI() {

        JLabel title = new JLabel("🛒 GIỎ HÀNG", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel footer = new JPanel(new BorderLayout(10, 10));
        footer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTotal = new JLabel("Tổng tiền: 0 ₫");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(33, 150, 243));

        btnCheckout = new JButton("THANH TOÁN");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setBackground(new Color(33, 150, 243));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setPreferredSize(new Dimension(0, 45));
        btnCheckout.addActionListener(e -> handleCheckout());

        footer.add(lblTotal, BorderLayout.WEST);
        footer.add(btnCheckout, BorderLayout.SOUTH);

        add(footer, BorderLayout.SOUTH);
    }

    /* ================= LOAD CART ================= */

    private void loadCart() {
        listPanel.removeAll();

        if (cartBUS.isEmpty()) {
            JLabel empty = new JLabel("Giỏ hàng trống", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            listPanel.add(empty);
            btnCheckout.setEnabled(false);
        } else {
            for (Map.Entry<Product, Integer> e : cartBUS.getItems().entrySet()) {
                listPanel.add(createItemCard(e.getKey(), e.getValue()));
                listPanel.add(Box.createVerticalStrut(10));
            }
            btnCheckout.setEnabled(true);
        }

        lblTotal.setText("Tổng tiền: " + formatMoney(cartBUS.getTotalAmount()));
        revalidate();
        repaint();
    }

    /* ================= ITEM CARD ================= */

    private JPanel createItemCard(Product p, int qty) {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel name = new JLabel(p.getProductName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel quantity = new JLabel("Số lượng: " + qty);
        quantity.setForeground(Color.GRAY);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.add(name);
        info.add(quantity);

        BigDecimal total = p.getPrice().multiply(BigDecimal.valueOf(qty));
        JLabel price = new JLabel(formatMoney(total));
        price.setFont(new Font("Segoe UI", Font.BOLD, 14));

        card.add(new JLabel("📦"), BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(price, BorderLayout.EAST);

        return card;
    }

    /* ================= CHECKOUT ================= */

    private void handleCheckout() {

        if (cartBUS.isEmpty()) return;

        // ===== LẤY STAFF THEO ACCOUNT (ADMIN & STAFF ĐỀU GIỐNG NHAU) =====
        StaffDAO staffDAO = new StaffDAO();
        Staff staff;

        try {
            staff = staffDAO.getByAccountID(Session.getAccountID());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không lấy được thông tin người xử lý!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (staff == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tài khoản này chưa được gán nhân viên.\n"
                            + "Không thể thực hiện thanh toán.",
                    "Không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int staffID = staff.getStaffID();
        String staffName = staff.getFullName();

        // ===== CHỌN HỘI VIÊN =====
        Member member = MemberSelectDialog.showDialog(this);
        if (member == null) return;

        // ===== CHỌN PHƯƠNG THỨC =====
        PaymentMethod method = (PaymentMethod) JOptionPane.showInputDialog(
                this,
                "Chọn phương thức thanh toán:",
                "Thanh toán",
                JOptionPane.PLAIN_MESSAGE,
                null,
                PaymentMethod.values(),
                PaymentMethod.CASH
        );
        if (method == null) return;

        // ===== XÁC NHẬN =====
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Người xử lý: " + staffName
                        + "\nHội viên: " + member.getFullName()
                        + "\nPhương thức: " + method
                        + "\nTổng tiền: " + formatMoney(cartBUS.getTotalAmount()),
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int invoiceID = checkoutBUS.checkout(
                    cartBUS,
                    staffID,
                    member,
                    method
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn xuất hóa đơn PDF không?",
                    "Xuất hóa đơn",
                    JOptionPane.YES_NO_OPTION
            );

            if (opt == JOptionPane.YES_OPTION) {
                InvoicePDFExporter.export(invoiceID);
            }

            dispose();


            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi thanh toán:\n" + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /* ================= UTIL ================= */

    private String formatMoney(BigDecimal value) {
        return NumberFormat
                .getCurrencyInstance(new Locale("vi", "VN"))
                .format(value);
    }
}