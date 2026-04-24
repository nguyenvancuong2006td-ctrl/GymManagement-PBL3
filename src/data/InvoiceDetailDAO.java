package data;

import model.InvoiceDetail;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailDAO {

    /* ================= INSERT ================= */

    public void insert(InvoiceDetail d, Connection conn) throws SQLException {

        String sql = """
            INSERT INTO InvoiceDetail
            (invoiceID, itemType, itemID, quantity, price)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getInvoiceID());
            ps.setString(2, d.getItemType());
            ps.setInt(3, d.getItemID());
            ps.setInt(4, d.getQuantity());
            ps.setBigDecimal(5, d.getPrice());
            ps.executeUpdate();
        }
    }

    /* ================= GET BY INVOICE (JOIN LẤY TÊN ITEM) ================= */

    public List<InvoiceDetail> getByInvoice(int invoiceID) {

        List<InvoiceDetail> list = new ArrayList<>();

        String sql = """
            SELECT
                d.invoiceDetailID,
                d.invoiceID,
                d.itemType,
                d.itemID,
                d.quantity,
                d.price,
                CASE
                    WHEN d.itemType = 'PT' THEN ps.serviceName
                    WHEN d.itemType = 'PRODUCT' THEN p.productName
                    WHEN d.itemType = 'PACKAGE' THEN m.packageName
                END AS itemName
            FROM InvoiceDetail d
            LEFT JOIN PTService ps
                ON d.itemType = 'PT'
               AND d.itemID = ps.serviceID
            LEFT JOIN Product p
                ON d.itemType = 'PRODUCT'
               AND d.itemID = p.productID
            LEFT JOIN MembershipPackage m
                ON d.itemType = 'PACKAGE'
               AND d.itemID = m.packageID
            WHERE d.invoiceID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail d = new InvoiceDetail();

                    d.setInvoiceDetailID(rs.getInt("invoiceDetailID"));
                    d.setInvoiceID(rs.getInt("invoiceID"));
                    d.setItemType(rs.getString("itemType"));
                    d.setItemID(rs.getInt("itemID"));
                    d.setItemName(rs.getString("itemName"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setPrice(rs.getBigDecimal("price"));

                    list.add(d);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể lấy chi tiết hóa đơn #" + invoiceID, e
            );
        }

        return list;
    }
}
