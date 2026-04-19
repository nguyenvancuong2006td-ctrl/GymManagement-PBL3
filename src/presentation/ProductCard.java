package presentation;

import business.CartBUS;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class ProductCard extends JPanel {

    private int quantity = 1;

    public ProductCard(Product product, CartBUS cartBUS) {

        /* ===== CARD CONFIG ===== */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(180, 225));
        setMaximumSize(new Dimension(180, 225));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true),
                new EmptyBorder(6, 6, 8, 6)
        ));

        /* ===== IMAGE ===== */
        JLabel img = new JLabel("No Image", SwingConstants.CENTER);
        img.setPreferredSize(new Dimension(100, 100));
        img.setMaximumSize(new Dimension(100, 100));
        img.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (product.getImagePath() != null && !product.getImagePath().isBlank()) {
            File f = new File(product.getImagePath());
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(product.getImagePath());
                Image scaled = icon.getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                img.setIcon(new ImageIcon(scaled));
                img.setText("");
            }
        }

        /* ===== NAME ===== */
        JLabel name = new JLabel(product.getProductName(), SwingConstants.CENTER);
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* ===== PRICE ===== */
        JLabel price = new JLabel("$" + product.getPrice());
        price.setFont(new Font("Segoe UI", Font.BOLD, 14));
        price.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* ===== QUANTITY ===== */
        JButton minus = new JButton("-");
        JButton plus = new JButton("+");
        JLabel qtyLabel = new JLabel("1", SwingConstants.CENTER);

        minus.addActionListener(e -> {
            if (quantity > 1) {
                quantity--;
                qtyLabel.setText(String.valueOf(quantity));
            }
        });

        plus.addActionListener(e -> {
            if (quantity < product.getQuantityInStock()) {
                quantity++;
                qtyLabel.setText(String.valueOf(quantity));
            }
        });

        JPanel qtyPanel = new JPanel(new GridLayout(1, 3, 4, 0));
        qtyPanel.setMaximumSize(new Dimension(140, 28));
        qtyPanel.setOpaque(false);
        qtyPanel.add(minus);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plus);

        /* ===== ADD TO CART ===== */
        JButton addToCart = new JButton("Add to Cart");
        addToCart.setBackground(new Color(33, 123, 244));
        addToCart.setForeground(Color.WHITE);
        addToCart.setFocusPainted(false);
        addToCart.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCart.setMaximumSize(new Dimension(160, 32));

        addToCart.addActionListener(e -> {
            try {
                cartBUS.addToCart(product, quantity);
                JOptionPane.showMessageDialog(
                        this,
                        "Đã thêm vào giỏ hàng",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        /* ===== ADD COMPONENTS ===== */
        add(img);
        add(Box.createVerticalStrut(5));
        add(name);
        add(Box.createVerticalStrut(3));
        add(price);
        add(Box.createVerticalStrut(5));
        add(qtyPanel);
        add(Box.createVerticalStrut(6));
        add(addToCart);
    }
}
