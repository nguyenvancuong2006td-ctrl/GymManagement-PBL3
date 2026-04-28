package business;

import model.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CartBUS {

    // Lưu sản phẩm và số lượng
    private final Map<Product, Integer> cart = new HashMap<>();

    /* ================== GIỎ HÀNG ================== */

    public void addToCart(Product p, int qty) {

        if (p == null) {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ!");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
        }

        int currentQty = cart.getOrDefault(p, 0);
        int newQty = currentQty + qty;

        if (newQty > p.getQuantityInStock()) {
            throw new RuntimeException(
                    "Số lượng vượt tồn kho! Còn lại: " + p.getQuantityInStock()
            );
        }

        cart.put(p, newQty);
    }

    public void clear() {
        cart.clear();
    }

    /* ================== TÍNH TỔNG TIỀN ================== */
    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Product, Integer> e : cart.entrySet()) {
            BigDecimal price = e.getKey().getPrice();   // BigDecimal
            BigDecimal qty = BigDecimal.valueOf(e.getValue());
            total = total.add(price.multiply(qty));
        }

        return total;
    }

    /* ================== GETTERS ================== */
    public Map<Product, Integer> getItems() {
        return Collections.unmodifiableMap(cart);
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public void decrease(Product p) {

        if (p == null) return;

        Integer currentQty = cart.get(p);

        if (currentQty == null) {
            return; // sản phẩm không có trong giỏ
        }

        if (currentQty <= 1) {
            cart.remove(p); // về 0 thì xóa khỏi giỏ
        } else {
            cart.put(p, currentQty - 1);
        }
    }

    public void add(Product p) {

        if (p == null) {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ!");
        }

        int currentQty = cart.getOrDefault(p, 0);
        int newQty = currentQty + 1;

        // Kiểm tra tồn kho
        if (newQty > p.getQuantityInStock()) {
            throw new RuntimeException(
                    "Số lượng vượt tồn kho! Còn lại: " + p.getQuantityInStock()
            );
        }

        cart.put(p, newQty);
    }
}