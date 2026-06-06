package data;

import model.WorkoutSchedule;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WorkoutScheduleDAO {

    public boolean insert(WorkoutSchedule ws) {

        String sql = """
            INSERT INTO WorkoutSchedule
            (date, startTime, endTime, memberPTID, trainerID, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(ws.getDate()));
            ps.setTime(2, Time.valueOf(ws.getStartTime()));
            ps.setTime(3, Time.valueOf(ws.getEndTime()));
            ps.setInt(4, ws.getMemberPTID());
            ps.setInt(5, ws.getTrainerID());
            ps.setString(6, ws.getStatus());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Insert schedule failed", e);
        }
    }

    public boolean isSlotBooked(
            int trainerID,
            LocalDate date,
            LocalTime newStart,
            LocalTime newEnd,
            int scheduleID) {

        String sql = """
        SELECT 1
        FROM WorkoutSchedule
        WHERE trainerID = ?
          AND date = ?
          AND status = 'BOOKED'
          AND scheduleID <> ?
          AND NOT (
                endTime <= CAST(? AS TIME)
             OR startTime >= CAST(? AS TIME)
          )
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, trainerID);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setInt(3, scheduleID);
            ps.setTime(4, java.sql.Time.valueOf(newStart));
            ps.setTime(5, java.sql.Time.valueOf(newEnd));

            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Check slot failed", e);
        }
    }


    /* ===== LOAD TABLE DATA ===== */
    public List<Object[]> getAllForTable() {

        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT
                ws.scheduleID,
                ws.date,
                ws.startTime,
                ws.endTime,
                ws.memberPTID,
                m.fullName AS memberName,
                t.fullName AS trainerName,
                ws.status
            FROM WorkoutSchedule ws
            JOIN MemberPT mp ON ws.memberPTID = mp.memberPTID
            JOIN Member m ON mp.memberID = m.memberID
            JOIN Trainer t ON ws.trainerID = t.trainerID
            ORDER BY ws.date DESC, ws.startTime
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("scheduleID"),
                        rs.getDate("date"),
                        rs.getTime("startTime"),
                        rs.getTime("endTime"),
                        rs.getInt("memberPTID"),
                        rs.getString("memberName"),
                        rs.getString("trainerName"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Load table failed", e);
        }
        return list;
    }

    public List<Object[]> getByMemberPT(int memberPTID) {

        List<Object[]> list = new ArrayList<>();

        String sql = """
        SELECT
            ws.date,
            ws.startTime,
            ws.endTime,
            t.fullName AS trainerName,
            ws.status
        FROM WorkoutSchedule ws
        JOIN Trainer t ON ws.trainerID = t.trainerID
        WHERE ws.memberPTID = ?
        ORDER BY ws.date DESC, ws.startTime
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberPTID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getDate("date"),
                            rs.getTime("startTime"),
                            rs.getTime("endTime"),
                            rs.getString("trainerName"),
                            rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Load PT schedule failed", e);
        }

        return list;
    }

    public boolean delete(int scheduleID) {

        String sql = "DELETE FROM WorkoutSchedule WHERE scheduleID=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, scheduleID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    public boolean update(WorkoutSchedule ws) {

        String sql = """
        UPDATE WorkoutSchedule
        SET date=?,
            startTime=?,
            endTime=?,
            memberPTID=?,
            trainerID=?,
            status=?
        WHERE scheduleID=?
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(ws.getDate()));
            ps.setTime(2, java.sql.Time.valueOf(ws.getStartTime()));
            ps.setTime(3, java.sql.Time.valueOf(ws.getEndTime()));
            ps.setInt(4, ws.getMemberPTID());
            ps.setInt(5, ws.getTrainerID());
            ps.setString(6, ws.getStatus());
            ps.setInt(7, ws.getScheduleID());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Update failed", e);
        }
    }

}