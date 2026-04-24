package data;

import model.Product;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ===================== SQL =====================
    private static final String SQL_GET_ALL =
            "SELECT * FROM Product";

    private static final String SQL_INSERT =
            """
            INSERT INTO Product
            (productName, category, price,
             quantityInitial, quantityInStock,
             description, imagePath)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE =
            """
            UPDATE Product SET
                productName = ?,
                category = ?,
                price = ?,
                quantityInitial = ?,
                quantityInStock = ?,
                description = ?,
                imagePath = ?
            WHERE productID = ?
            """;

    private static final String SQL_DELETE =
            "DELETE FROM Product WHERE productID = ?";

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("productID"),
                        rs.getString("productName"),
                        rs.getString("category"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantityInitial"),
                        rs.getInt("quantityInStock"),
                        rs.getString("description"),
                        rs.getString("imagePath")
                );
                list.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tải danh sách sản phẩm", e);
        }

        return list;
    }

    public void insert(Product p) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getProductName());
            ps.setString(2, p.getCategory());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getQuantityInitial());
            ps.setInt(5, p.getQuantityInStock());
            ps.setString(6, p.getDescription());
            ps.setString(7, p.getImagePath());

            ps.executeUpdate();

            // Lấy productID tự tăng
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setProductID(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Thêm sản phẩm thất bại", e);
        }
    }

    public void update(Product p) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, p.getProductName());
            ps.setString(2, p.getCategory());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getQuantityInitial());
            ps.setInt(5, p.getQuantityInStock());
            ps.setString(6, p.getDescription());
            ps.setString(7, p.getImagePath());
            ps.setInt(8, p.getProductID());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Cập nhật sản phẩm thất bại", e);
        }
    }

    public void delete(int productID) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, productID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Xoá sản phẩm thất bại", e);
        }
    }

    public String getNameByID(int productID) {
        String sql = "SELECT productName FROM Product WHERE productID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("productName");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tên sản phẩm theo ID", e);
        }

        return "Không xác định";
    }

    public void decreaseStock(
            int productID,
            int qty,
            Connection conn
    ) throws SQLException {

        String sql = """
        UPDATE Product
        SET quantityInStock = quantityInStock - ?
        WHERE productID = ?
          AND quantityInStock >= ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productID);
            ps.setInt(3, qty);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException(
                        "Không đủ tồn kho cho sản phẩm ID = " + productID
                );
            }
        }
    }
}