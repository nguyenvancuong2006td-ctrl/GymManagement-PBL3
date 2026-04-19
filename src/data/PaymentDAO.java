package data;

import model.Payment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /* ================= INSERT ================= */
    public void insert(Payment p, Connection conn) throws SQLException {

        String sql = """
            INSERT INTO Payment
            (amount, paymentMethod, status, memberID, staffID, packageID, invoiceID)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, p.getAmount());
            ps.setString(2, p.getPaymentMethod());
            ps.setString(3, p.getStatus());
            ps.setInt(4, p.getMemberID());
            ps.setInt(5, p.getStaffID());
            ps.setObject(6, p.getPackageID(), Types.INTEGER);
            ps.setInt(7, p.getInvoiceID()); // ✅ liên kết với Invoice

            ps.executeUpdate();
        }
    }

    /* ================= GET BY INVOICE ================= */
    public List<Payment> getByInvoiceID(int invoiceID) {

        List<Payment> list = new ArrayList<>();

        String sql = """
            SELECT paymentID,
                   amount,
                   paymentDate,
                   paymentMethod,
                   status,
                   memberID,
                   staffID,
                   packageID,
                   invoiceID
            FROM Payment
            WHERE invoiceID = ?
            ORDER BY paymentDate
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();

                    p.setPaymentID(rs.getInt("paymentID"));
                    p.setAmount(rs.getBigDecimal("amount"));

                    Timestamp ts = rs.getTimestamp("paymentDate");
                    if (ts != null) {
                        p.setPaymentDate(ts.toLocalDateTime());
                    }

                    p.setPaymentMethod(rs.getString("paymentMethod"));
                    p.setStatus(rs.getString("status"));
                    p.setMemberID(rs.getInt("memberID"));
                    p.setStaffID(rs.getInt("staffID"));

                    int pkgID = rs.getInt("packageID");
                    if (!rs.wasNull()) {
                        p.setPackageID(pkgID);
                    }

                    p.setInvoiceID(rs.getInt("invoiceID"));

                    list.add(p);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Cannot load payments by invoiceID", e);
        }

        return list;
    }
}