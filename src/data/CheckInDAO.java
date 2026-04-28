package data;

import model.CheckIn;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckInDAO {

    private final Connection con;

    public CheckInDAO() {
        try {
            con = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kết nối database", e);
        }
    }

    /* ================= INSERT ================= */

    public void insert(CheckIn checkIn) throws SQLException {
        String sql = "INSERT INTO CheckIn(phoneNumber, checkInTime) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, checkIn.getPhoneNumber());
            ps.setTimestamp(2, Timestamp.valueOf(checkIn.getCheckInTime()));
            ps.executeUpdate();
        }
    }

    /* ================= CHECK TRÙNG NGÀY ================= */

    public boolean hasCheckedInToday(String phoneNumber) throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM CheckIn " +
                        "WHERE phoneNumber = ? " +
                        "AND CAST(checkInTime AS DATE) = CAST(GETDATE() AS DATE)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /* ================= TỔNG LƯỢT ================= */

    public int countByPhone(String phoneNumber) {

        String sql = "SELECT COUNT(*) FROM CheckIn WHERE phoneNumber = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("Count check-in failed", e);
        }
    }

    /* ================= LẦN GẦN NHẤT ================= */

    public LocalDateTime getLatestByPhone(String phoneNumber) {

        String sql =
                "SELECT MAX(checkInTime) FROM CheckIn WHERE phoneNumber = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getTimestamp(1) != null) {
                return rs.getTimestamp(1).toLocalDateTime();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get latest check-in failed", e);
        }

        return null;
    }

    /* ================= LỊCH SỬ ================= */

    public List<CheckIn> getByPhone(String phoneNumber) {

        List<CheckIn> list = new ArrayList<>();

        String sql =
                "SELECT checkInID, phoneNumber, checkInTime " +
                        "FROM CheckIn " +
                        "WHERE phoneNumber = ? " +
                        "ORDER BY checkInTime DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CheckIn ci = new CheckIn();
                ci.setCheckInID(rs.getInt("checkInID"));
                ci.setPhoneNumber(rs.getString("phoneNumber"));
                ci.setCheckInTime(
                        rs.getTimestamp("checkInTime").toLocalDateTime()
                );
                list.add(ci);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get check-in history failed", e);
        }

        return list;
    }
}