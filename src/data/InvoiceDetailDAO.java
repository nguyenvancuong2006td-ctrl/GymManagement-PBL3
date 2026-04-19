package data;

import model.InvoiceDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailDAO {

    public void insert(InvoiceDetail d, Connection conn) throws SQLException {
        String sql = """
            INSERT INTO InvoiceDetail (invoiceID, productID, quantity, price)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getInvoiceID());
            ps.setInt(2, d.getProductID());
            ps.setInt(3, d.getQuantity());
            ps.setBigDecimal(4, d.getPrice());
            ps.executeUpdate();
        }
    }

    public List<InvoiceDetail> getByInvoiceID(int invoiceID) throws SQLException {
        List<InvoiceDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM InvoiceDetail WHERE invoiceID=?";

        try (Connection c = util.DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, invoiceID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InvoiceDetail d = new InvoiceDetail();
                d.setInvoiceDetailID(rs.getInt("invoiceDetailID"));
                d.setInvoiceID(invoiceID);
                d.setProductID(rs.getInt("productID"));
                d.setQuantity(rs.getInt("quantity"));
                d.setPrice(rs.getBigDecimal("price"));

                list.add(d);
            }
        }
        return list;
    }
}