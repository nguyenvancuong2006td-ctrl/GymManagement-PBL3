package data;

import util.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    /* ================= TOTAL MEMBERS ================= */
    public int getTotalMembers() throws SQLException {

        String sql = "SELECT COUNT(*) FROM Member";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /* ================= ACTIVE MEMBERS ================= */
    public int getActiveMembers() throws SQLException {

        String sql = """
            SELECT COUNT(DISTINCT p.memberID)
            FROM Payment p
            JOIN MembershipPackage mp ON p.packageID = mp.packageID
            WHERE p.status = 'Completed'
              AND p.paymentDate IS NOT NULL
              AND DATEADD(DAY, mp.duration, p.paymentDate) >= GETDATE()
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /* ================= TOTAL TRAINERS ================= */
    public int getTotalTrainers() throws SQLException {

        String sql = "SELECT COUNT(*) FROM Trainer";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /* ================= REVENUE BY MONTH ================= */
    public Map<Integer, Double> getRevenueByMonth() throws SQLException {

        String sql = """
            SELECT MONTH(paymentDate) AS month,
                   SUM(amount) AS total
            FROM Payment
            WHERE status = 'Completed'
              AND paymentDate IS NOT NULL
              AND YEAR(paymentDate) = YEAR(GETDATE())
            GROUP BY MONTH(paymentDate)
        """;

        Map<Integer, Double> map = new LinkedHashMap<>();

        // luôn đủ 12 tháng
        for (int i = 1; i <= 12; i++) {
            map.put(i, 0.0);
        }

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int month = rs.getInt("month");
                double total = rs.getDouble("total");

                map.put(month, total);
            }
        }

        return map;
    }
}