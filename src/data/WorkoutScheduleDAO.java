package data;

import model.WorkoutSchedule;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WorkoutScheduleDAO {

    /* ===================== GET ALL ===================== */
    public List<WorkoutSchedule> getAll() {
        List<WorkoutSchedule> list = new ArrayList<>();
        String sql = """
            SELECT * FROM WorkoutSchedule
            ORDER BY date, startTime
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không load được lịch tập", e);
        }
        return list;
    }

    /* ===================== GET BY DATE ===================== */
    public List<WorkoutSchedule> getByDate(LocalDate date) {
        List<WorkoutSchedule> list = new ArrayList<>();
        String sql = """
            SELECT * FROM WorkoutSchedule
            WHERE date = ?
            ORDER BY startTime
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không load được lịch theo ngày", e);
        }
        return list;
    }

    /* ===================== INSERT ===================== */
    public boolean insert(WorkoutSchedule ws) {
        String sql = """
            INSERT INTO WorkoutSchedule
            (date, startTime, endTime, memberID, trainerID, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(ws.getDate()));
            ps.setTime(2, Time.valueOf(ws.getStartTime()));
            ps.setTime(3, Time.valueOf(ws.getEndTime()));
            ps.setInt(4, ws.getMemberID());
            ps.setInt(5, ws.getTrainerID());
            ps.setString(6, ws.getStatus()); // BOOKED

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể thêm lịch tập", e);
        }
    }

    /* ===================== UPDATE ===================== */
    public boolean update(WorkoutSchedule ws) {
        String sql = """
            UPDATE WorkoutSchedule
            SET date = ?, startTime = ?, endTime = ?,
                memberID = ?, trainerID = ?, status = ?
            WHERE scheduleID = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(ws.getDate()));
            ps.setTime(2, Time.valueOf(ws.getStartTime()));
            ps.setTime(3, Time.valueOf(ws.getEndTime()));
            ps.setInt(4, ws.getMemberID());
            ps.setInt(5, ws.getTrainerID());
            ps.setString(6, ws.getStatus());
            ps.setInt(7, ws.getScheduleID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể cập nhật lịch tập", e);
        }
    }

    /* ===================== DELETE ===================== */
    public boolean delete(int id) {
        String sql = "DELETE FROM WorkoutSchedule WHERE scheduleID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa lịch tập", e);
        }
    }

    /* ===================== CHECK TRÙNG LỊCH (THEO NGÀY) ===================== */
    public boolean isConflict(
            LocalDate date,
            LocalTime start,
            LocalTime end,
            int trainerID) {

        String sql = """
            SELECT COUNT(*)
            FROM WorkoutSchedule
            WHERE date = ?
              AND trainerID = ?
              AND status = 'BOOKED'
              AND (? < endTime AND ? > startTime)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, trainerID);
            ps.setTime(3, Time.valueOf(start));
            ps.setTime(4, Time.valueOf(end));

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trùng lịch", e);
        }
    }

    /* ===================== MAP ===================== */
    private WorkoutSchedule map(ResultSet rs) throws SQLException {
        WorkoutSchedule ws = new WorkoutSchedule();
        ws.setScheduleID(rs.getInt("scheduleID"));
        ws.setDate(rs.getDate("date").toLocalDate());
        ws.setStartTime(rs.getTime("startTime").toLocalTime());
        ws.setEndTime(rs.getTime("endTime").toLocalTime());
        ws.setMemberID(rs.getInt("memberID"));
        ws.setTrainerID(rs.getInt("trainerID"));
        ws.setStatus(rs.getString("status"));
        return ws;
    }
}