package data;

import util.DBConnection;
import model.CheckIn;
import model.Invoice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    /* ================= CHECK-IN REPORT ================= */

    // Tổng check-in theo ngày
    public List<Object[]> getCheckInByDate(LocalDate from, LocalDate to) {

        List<Object[]> list = new ArrayList<>();

        String sql =
                "SELECT CAST(checkInTime AS DATE) AS d, COUNT(*) AS total " +
                        "FROM CheckIn " +
                        "WHERE checkInTime BETWEEN ? AND ? " +
                        "GROUP BY CAST(checkInTime AS DATE) " +
                        "ORDER BY d";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getDate("d").toLocalDate(),
                        rs.getInt("total")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // Chi tiết check-in theo ngày (drill-down)
    public List<CheckIn> getCheckInDetail(LocalDate date) {

        List<CheckIn> list = new ArrayList<>();

        String sql =
                "SELECT phoneNumber, checkInTime " +
                        "FROM CheckIn " +
                        "WHERE CAST(checkInTime AS DATE) = ? " +
                        "ORDER BY checkInTime";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CheckIn c = new CheckIn();
                c.setPhoneNumber(rs.getString("phoneNumber"));
                c.setCheckInTime(rs.getTimestamp("checkInTime").toLocalDateTime());
                list.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    /* ================= REVENUE REPORT ================= */

    // Doanh thu theo ngày
    public List<Object[]> getRevenueByDate(LocalDate from, LocalDate to) {

        List<Object[]> list = new ArrayList<>();

        String sql =
                "SELECT CAST(invoiceDate AS DATE) AS d, " +
                        "COUNT(*) AS invoiceCount, " +
                        "SUM(totalAmount) AS revenue " +
                        "FROM Invoice " +
                        "WHERE invoiceDate BETWEEN ? AND ? " +
                        "GROUP BY CAST(invoiceDate AS DATE) " +
                        "ORDER BY d";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getDate("d").toLocalDate(),
                        rs.getInt("invoiceCount"),
                        rs.getBigDecimal("revenue")
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // Hóa đơn theo ngày (drill-down)
    public List<Invoice> getInvoiceByDate(LocalDate date) {

        List<Invoice> list = new ArrayList<>();

        String sql =
                "SELECT i.invoiceID, i.invoiceDate, i.totalAmount, s.fullName " +
                        "FROM Invoice i " +
                        "JOIN Staff s ON i.staffID = s.staffID " +
                        "WHERE CAST(i.invoiceDate AS DATE) = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Invoice i = new Invoice();
                i.setInvoiceID(rs.getInt("invoiceID"));
                i.setInvoiceDate(
                        rs.getTimestamp("invoiceDate").toLocalDateTime()
                );
                i.setTotalAmount(rs.getBigDecimal("totalAmount"));
                i.setStaffName(rs.getString("fullName")); // ✅ TỪ Staff

                list.add(i);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // ReportDAO.java
    public List<Object[]> getMembersExpiring(int days) {

        List<Object[]> list = new ArrayList<>();

        String sql =
                "SELECT m.memberID, m.fullName, m.phoneNumber, " +
                        "       mp.endDate, DATEDIFF(day, GETDATE(), mp.endDate) AS remaining " +
                        "FROM MemberPackage mp " +
                        "JOIN Member m ON mp.memberID = m.memberID " +
                        "WHERE mp.endDate >= CAST(GETDATE() AS DATE) " +
                        "  AND mp.endDate <= DATEADD(day, ?, CAST(GETDATE() AS DATE)) " +
                        "ORDER BY mp.endDate";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("memberID"),
                        rs.getString("fullName"),
                        rs.getString("phoneNumber"),
                        rs.getDate("endDate").toLocalDate(),
                        rs.getInt("remaining")
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

}
