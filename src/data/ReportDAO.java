package data;

import util.DBConnection;
import model.CheckIn;
import model.Invoice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

            LocalDateTime start =
                    from.atStartOfDay();

            LocalDateTime end =
                    to.atTime(23,59,59);

            ps.setTimestamp(
                    1,
                    Timestamp.valueOf(start)
            );

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(end)
            );

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
                "SELECT m.fullName, c.phoneNumber, c.checkInTime, c.checkOutTime " +
                        "FROM CheckIn c " +
                        "JOIN Member m ON c.phoneNumber = m.phoneNumber " +
                        "WHERE CAST(c.checkInTime AS DATE) = ? " +
                        "ORDER BY c.checkInTime";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CheckIn c = new CheckIn();
                c.setPhoneNumber(rs.getString("phoneNumber"));
                c.setFullName(rs.getString("fullName"));
                c.setCheckInTime(rs.getTimestamp("checkInTime").toLocalDateTime());

                Timestamp out = rs.getTimestamp("checkOutTime");
                if (out != null) {
                    c.setCheckOutTime(out.toLocalDateTime());
                }

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

            LocalDateTime start =
                    from.atStartOfDay();

            LocalDateTime end =
                    to.atTime(23,59,59);

            ps.setTimestamp(
                    1,
                    Timestamp.valueOf(start)
            );

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(end)
            );

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
                i.setStaffName(rs.getString("fullName"));

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
