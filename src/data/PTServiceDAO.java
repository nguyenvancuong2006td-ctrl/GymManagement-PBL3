package data;

import model.PTService;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PTServiceDAO {

    /* ================= INSERT ================= */
    public void insert(PTService s) {

        String sql = """
        INSERT INTO PTService (trainerID, serviceName, totalSessions, price)
        VALUES (?, ?, ?, ?)
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, s.getTrainerID());
            ps.setString(2, s.getServiceName());
            ps.setInt(3, s.getTotalSessions());
            ps.setBigDecimal(4, s.getPrice());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Thêm dịch vụ PT thất bại", e);
        }
    }

    /* ================= UPDATE ================= */
    public void update(PTService s) {

        String sql = """
        UPDATE PTService
        SET trainerID = ?, serviceName = ?, totalSessions = ?, price = ?
        WHERE serviceID = ?
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, s.getTrainerID());
            ps.setString(2, s.getServiceName());
            ps.setInt(3, s.getTotalSessions());
            ps.setBigDecimal(4, s.getPrice());
            ps.setInt(5, s.getServiceID());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Cập nhật dịch vụ PT thất bại", e);
        }
    }

    /* ================= DELETE ================= */
    public void delete(int serviceID) {

        String sql = "DELETE FROM PTService WHERE serviceID = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, serviceID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Xóa dịch vụ PT thất bại", e);
        }
    }

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

    public String getNameByID(int ptServiceID) {

        String sql = "SELECT serviceName FROM PTService WHERE serviceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ptServiceID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("serviceName");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Không lấy được tên dịch vụ PT", e);
        }

        return "Không xác định";
    }

    public List<PTService> getByTrainer(int trainerID) {
        List<PTService> list = new ArrayList<>();
        String sql = "SELECT * FROM PTService WHERE trainerID = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, trainerID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}