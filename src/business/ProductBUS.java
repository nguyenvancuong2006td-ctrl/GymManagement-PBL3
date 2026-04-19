package business;

import data.ProductDAO;
import model.Product;
import model.Permission;

import java.math.BigDecimal;
import java.util.List;

public class ProductBUS {

    private final ProductDAO dao = new ProductDAO();

    /* ================= QUERY ================= */

    public List<Product> getAllForShop() {
        AuthorizationService.check(Permission.PRODUCT_SHOP_VIEW);
        return dao.getAll();
    }

    public List<Product> getAllForManagement() {
        AuthorizationService.check(Permission.PRODUCT_MANAGE);
        return dao.getAll();
    }

    /* ================= ADD ================= */

    public void addProduct(Product p) {
        AuthorizationService.check(Permission.PRODUCT_ADD);

        validateCommon(p);

        // khi thêm mới: số lượng ban đầu = số lượng nhập
        p.setQuantityInitial(p.getQuantityInStock());

        dao.insert(p);
    }

    /* ================= UPDATE ================= */

    public void updateProduct(Product p) {
        AuthorizationService.check(Permission.PRODUCT_UPDATE);

        if (p == null || p.getProductID() <= 0)
            throw new IllegalArgumentException("ProductID không hợp lệ!");

        validateCommon(p);

        if (p.getQuantityInitial() < 0 || p.getQuantityInStock() < 0)
            throw new IllegalArgumentException("Số lượng không hợp lệ!");

        // tồn kho không được vượt số lượng ban đầu
        if (p.getQuantityInStock() > p.getQuantityInitial())
            throw new IllegalArgumentException(
                    "Số lượng còn lại không được lớn hơn số lượng ban đầu!"
            );

        dao.update(p);
    }

    /* ================= DELETE ================= */

    public void deleteProduct(int productID) {
        AuthorizationService.check(Permission.PRODUCT_DELETE);

        if (productID <= 0)
            throw new IllegalArgumentException("ProductID không hợp lệ!");

        dao.delete(productID);
    }

    /* ================= VALIDATE CHUNG ================= */

    private void validateCommon(Product p) {

        if (p == null)
            throw new IllegalArgumentException("Product không được null!");

        if (p.getProductName() == null || p.getProductName().isBlank())
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng!");

        if (p.getPrice() == null ||
                p.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Giá phải lớn hơn 0!");

        if (p.getQuantityInStock() <= 0)
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
    }
}