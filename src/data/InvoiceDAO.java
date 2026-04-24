package data;

import model.Invoice;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    /* ================= INSERT ================= */

    public int insert(Invoice invoice, Connection conn) throws SQLException {

        String sql = """
            INSERT INTO Invoice (totalAmount, staffID, memberID)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setBigDecimal(1, invoice.getTotalAmount());
            ps.setInt(2, invoice.getStaffID());   // staffID -> Staff.staffID
            ps.setInt(3, invoice.getMemberID());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /* ================= GET ALL (HIỂN THỊ TÊN NHÂN VIÊN) ================= */

    public List<Invoice> getAll() throws SQLException {

        List<Invoice> list = new ArrayList<>();

        String sql = """
            SELECT
                i.invoiceID,
                i.invoiceDate,
                i.totalAmount,
                i.staffID,
                i.memberID,
                s.fullName AS staffName
            FROM Invoice i
            LEFT JOIN Staff s
                ON i.staffID = s.staffID
            ORDER BY i.invoiceDate DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice();

                invoice.setInvoiceID(rs.getInt("invoiceID"));

                Timestamp ts = rs.getTimestamp("invoiceDate");
                invoice.setInvoiceDate(ts != null ? ts.toLocalDateTime() : null);

                invoice.setTotalAmount(rs.getBigDecimal("totalAmount"));
                invoice.setStaffID(rs.getInt("staffID"));
                invoice.setMemberID(rs.getInt("memberID"));
                invoice.setStaffName(rs.getString("staffName"));

                list.add(invoice);
            }
        }

        return list;
    }

    /* ================= GET BY ID (CÓ TÊN NHÂN VIÊN) ================= */

    public Invoice getByID(int invoiceID) throws SQLException {

        String sql = """
            SELECT
                i.invoiceID,
                i.invoiceDate,
                i.totalAmount,
                i.staffID,
                i.memberID,
                s.fullName AS staffName
            FROM Invoice i
            LEFT JOIN Staff s
                ON i.staffID = s.staffID
            WHERE i.invoiceID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice();

                    invoice.setInvoiceID(rs.getInt("invoiceID"));

                    Timestamp ts = rs.getTimestamp("invoiceDate");
                    invoice.setInvoiceDate(ts != null ? ts.toLocalDateTime() : null);

                    invoice.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    invoice.setStaffID(rs.getInt("staffID"));
                    invoice.setMemberID(rs.getInt("memberID"));
                    invoice.setStaffName(rs.getString("staffName"));

                    return invoice;
                }
            }
        }

        return null;
    }

    public BigDecimal getTotalAmount(int invoiceID) {

        String sql = "SELECT totalAmount FROM Invoice WHERE invoiceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("totalAmount");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể lấy tổng tiền của hóa đơn #" + invoiceID,
                    e
            );
        }

        return BigDecimal.ZERO;
    }

    /* ================= GET BY MEMBER ================= */

    public List<Invoice> getByMember(int memberID) {

        List<Invoice> list = new ArrayList<>();

        String sql = """
            SELECT
                i.invoiceID,
                i.invoiceDate,
                i.totalAmount,
                i.staffID,
                i.memberID,
                s.fullName AS staffName
            FROM Invoice i
            LEFT JOIN Staff s
                ON i.staffID = s.staffID
            WHERE i.memberID = ?
            ORDER BY i.invoiceDate DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Invoice invoice = new Invoice();

                    invoice.setInvoiceID(rs.getInt("invoiceID"));

                    Timestamp ts = rs.getTimestamp("invoiceDate");
                    invoice.setInvoiceDate(
                            ts != null ? ts.toLocalDateTime() : null
                    );

                    invoice.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    invoice.setStaffID(rs.getInt("staffID"));
                    invoice.setMemberID(rs.getInt("memberID"));
                    invoice.setStaffName(rs.getString("staffName"));

                    list.add(invoice);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể lấy hóa đơn của hội viên ID = " + memberID,
                    e
            );
        }

        return list;
    }
}
