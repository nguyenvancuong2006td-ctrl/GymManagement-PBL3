package data;

import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    public int getTotalMembers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Member";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int getActiveMembers() throws SQLException {
        String sql = """
            SELECT COUNT(DISTINCT p.memberID)
            FROM Payment p
            JOIN MembershipPackage mp ON p.packageID = mp.packageID
            WHERE p.status = 'Completed'
              AND DATEADD(DAY, mp.duration, p.paymentDate) >= GETDATE()
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int getTotalTrainers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Trainer";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int getTodayInvoices() throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM Payment
            WHERE status = 'Completed'
              AND CAST(paymentDate AS DATE) = CAST(GETDATE() AS DATE)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public double getTodayRevenue() throws SQLException {
        String sql = """
            SELECT ISNULL(SUM(amount),0)
            FROM Payment
            WHERE status = 'Completed'
              AND CAST(paymentDate AS DATE) = CAST(GETDATE() AS DATE)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        }
    }

    public int getNewMembersToday() throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM Member
            WHERE CAST(joinDate AS DATE) = CAST(GETDATE() AS DATE)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public Map<LocalDate, Double> getRevenueLast7Days() throws SQLException {
        String sql = """
            SELECT CAST(paymentDate AS DATE), SUM(amount)
            FROM Payment
            WHERE status = 'Completed'
              AND paymentDate >= DATEADD(DAY,-6,GETDATE())
            GROUP BY CAST(paymentDate AS DATE)
            ORDER BY CAST(paymentDate AS DATE)
        """;

        Map<LocalDate, Double> map = new LinkedHashMap<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getDate(1).toLocalDate(), rs.getDouble(2));
            }
        }
        return map;
    }
}
