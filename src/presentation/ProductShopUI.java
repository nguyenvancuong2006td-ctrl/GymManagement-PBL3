package presentation;

import business.CartBUS;
import business.ProductBUS;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductShopUI extends JPanel {

    private final ProductBUS productBUS;
    private final CartBUS cartBUS;
    private JPanel grid;

    public ProductShopUI() {
        productBUS = new ProductBUS();
        cartBUS = new CartBUS();

        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        /* ===== HEADER ===== */
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Product Shop");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        JButton cartBtn = new JButton("Giỏ hàng");
        cartBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            new CartUI(frame, cartBUS).setVisible(true);
        });
        header.add(cartBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        /* ===== GRID ===== */
        grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private void loadProducts() {
        new SwingWorker<List<Product>, Void>() {

            @Override
            protected List<Product> doInBackground() {
                return productBUS.getAllForShop();
            }

            @Override
            protected void done() {
                try {
                    List<Product> list = get();
                    grid.removeAll();

                    if (list.isEmpty()) {
                        JLabel empty = new JLabel("Không có sản phẩm", SwingConstants.CENTER);
                        empty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                        grid.add(empty);
                    } else {
                        for (Product p : list) {
                            grid.add(new ProductCard(p, cartBUS));
                        }
                    }

                    grid.revalidate();
                    grid.repaint();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ProductShopUI.this,
                            e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }
}
