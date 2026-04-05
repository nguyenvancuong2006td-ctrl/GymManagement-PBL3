package data;

import util.DBConnection;

import java.sql.*;
import java.util.*;

public class DashboardDAO {

    public int getTotalMembers() throws Exception {
        String sql = "SELECT COUNT(*) FROM Member";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int getTotalTrainers() throws Exception {
        String sql = "SELECT COUNT(*) FROM Trainer";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int getActivePlans() throws Exception {
        String sql = """
            SELECT COUNT(*)
            FROM Payment p
            JOIN MembershipPackage mp ON p.packageID = mp.packageID
            WHERE DATEADD(DAY, mp.duration, p.paymentDate) >= GETDATE()
              AND p.status = 'Completed'
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public Map<Integer, Double> getMonthlyRevenue() throws Exception {
        Map<Integer, Double> map = new TreeMap<>();
        String sql = """
            SELECT MONTH(paymentDate), SUM(amount)
            FROM Payment
            WHERE YEAR(paymentDate) = YEAR(GETDATE())
              AND status = 'Completed'
            GROUP BY MONTH(paymentDate)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getInt(1), rs.getDouble(2));
            }
        }
        return map;
    }
}