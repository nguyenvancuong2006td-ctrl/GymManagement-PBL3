package data;

import model.CheckIn;
import model.MemberPT;
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
                "SELECT checkInID, phoneNumber, checkInTime, checkOutTime " +
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

                Timestamp outTs = rs.getTimestamp("checkOutTime");
                if (outTs != null) {
                    ci.setCheckOutTime(outTs.toLocalDateTime());
                }

                list.add(ci);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get check-in history failed", e);
        }

        return list;
    }

    /* ================= CHECK-OUT ================= */

    public void checkOut(String phoneNumber) throws SQLException {

        // 1. lấy check-in chưa checkout
        String getSql = """
        SELECT TOP 1 *
        FROM CheckIn
        WHERE phoneNumber = ?
        AND checkOutTime IS NULL
        ORDER BY checkInTime DESC
    """;

        LocalDateTime checkInTime = null;

        try (PreparedStatement ps = con.prepareStatement(getSql)) {
            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                checkInTime = rs.getTimestamp("checkInTime").toLocalDateTime();
            }
        }

        if (checkInTime == null) return;

        // 2. update checkout
        String updateSql = """
        UPDATE CheckIn
        SET checkOutTime = GETDATE()
        WHERE phoneNumber = ?
        AND checkOutTime IS NULL
    """;

        LocalDateTime checkOutTime = LocalDateTime.now();

        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
            ps.setString(1, phoneNumber);
            ps.executeUpdate();
        }

        // 3. update PT DONE
        MemberDAO memberDAO = new MemberDAO();
        MemberPTDAO memberPTDAO = new MemberPTDAO();
        WorkoutScheduleDAO scheduleDAO = new WorkoutScheduleDAO();

        int memberID = memberDAO.getByPhone(phoneNumber).getMemberID();
        MemberPT pt = memberPTDAO.getByMember(memberID);

        if (pt != null) {
            scheduleDAO.markDoneIfValid(
                    pt.getMemberPTID(),
                    checkInTime.toLocalTime(),
                    checkOutTime.toLocalTime()
            );
        }
    }

    public boolean isChecking(String phoneNumber) throws SQLException {

        String sql = """
        SELECT COUNT(*) FROM CheckIn
        WHERE phoneNumber = ?
        AND checkOutTime IS NULL
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

}