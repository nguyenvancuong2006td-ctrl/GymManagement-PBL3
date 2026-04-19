package data;

import model.PTService;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PTServiceDAO {

    /* ================= GET ALL ================= */
    public List<PTService> getAll() {
        List<PTService> list = new ArrayList<>();
        String sql = "SELECT * FROM PTService";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get PTService list failed", e);
        }
        return list;
    }

    /* ================= FIND BY ID ================= */
    public PTService findById(int serviceID) {
        String sql = "SELECT * FROM PTService WHERE serviceID = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, serviceID);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? map(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Find PTService by ID failed", e);
        }
    }

    /* ================= MAP ================= */
    private PTService map(ResultSet rs) throws SQLException {
        PTService s = new PTService();
        s.setServiceID(rs.getInt("serviceID"));
        s.setTrainerID(rs.getInt("trainerID"));
        s.setServiceName(rs.getString("serviceName"));
        s.setTotalSessions(rs.getInt("totalSessions"));
        s.setPrice(rs.getBigDecimal("price"));
        return s;
    }
}