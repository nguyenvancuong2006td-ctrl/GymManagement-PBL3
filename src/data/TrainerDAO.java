package data;

import model.Trainer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDAO {

    // ================== MAP RESULTSET ==================
    private Trainer mapResultSet(ResultSet rs) throws SQLException {
        Trainer t = new Trainer();

        t.setTrainerID(rs.getInt("trainerID"));
        t.setFullName(rs.getString("fullName"));
        t.setGender(rs.getString("gender"));
        t.setPhoneNumber(rs.getString("phoneNumber"));

        Timestamp ts = rs.getTimestamp("hireDate");
        if (ts != null) {
            t.setHireDate(ts.toLocalDateTime().toLocalDate());
        }

        // ✅ salary là double
        t.setSalary(rs.getDouble("salary"));

        return t;
    }

    // ================== GET ALL ==================
    public List<Trainer> getAll() {
        List<Trainer> list = new ArrayList<>();

        String sql = """
            SELECT trainerID, fullName, gender, phoneNumber, hireDate, salary
            FROM Trainer
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trainers", e);
        }

        return list;
    }

    // ================== FIND BY ID ==================
    public Trainer findById(int id) {
        String sql = """
            SELECT trainerID, fullName, gender, phoneNumber, hireDate, salary
            FROM Trainer
            WHERE trainerID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding trainer by ID", e);
        }

        return null;
    }

    // ================== FIND BY PHONE ==================
    public Trainer findByPhone(String phone) {
        String sql = """
            SELECT trainerID, fullName, gender, phoneNumber, hireDate, salary
            FROM Trainer
            WHERE phoneNumber = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding trainer by phone", e);
        }

        return null;
    }

    // ================== INSERT ==================
    public boolean insert(Trainer t) {
        String sql = """
            INSERT INTO Trainer (fullName, gender, phoneNumber, salary)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getFullName());
            ps.setString(2, t.getGender());
            ps.setString(3, t.getPhoneNumber());
            ps.setDouble(4, t.getSalary()); // ✅ double

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("unique")) {
                throw new RuntimeException("Phone number already exists", e);
            }
            throw new RuntimeException("Error inserting trainer", e);
        }
    }

    // ================== UPDATE ==================
    public boolean update(Trainer t) {
        String sql = """
            UPDATE Trainer
            SET fullName = ?, gender = ?, phoneNumber = ?, salary = ?
            WHERE trainerID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getFullName());
            ps.setString(2, t.getGender());
            ps.setString(3, t.getPhoneNumber());
            ps.setDouble(4, t.getSalary()); // ✅ double
            ps.setInt(5, t.getTrainerID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating trainer", e);
        }
    }

    // ================== DELETE ==================
    public boolean delete(int trainerID) {
        String sql = "DELETE FROM Trainer WHERE trainerID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trainerID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting trainer", e);
        }
    }
}