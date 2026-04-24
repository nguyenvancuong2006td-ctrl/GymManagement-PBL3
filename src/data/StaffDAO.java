package data;

import model.Account;
import model.Staff;
import model.StaffAccount;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();

        s.setStaffID(rs.getInt("staffID"));
        s.setFullName(rs.getString("fullName"));
        s.setGender(rs.getString("gender"));
        s.setPhoneNumber(rs.getString("phoneNumber"));
        s.setSalary(rs.getDouble("salary"));

        Date hire = rs.getDate("hireDate");
        if (hire != null) {
            s.setHireDate(hire.toLocalDate());
        }

        Account acc = new Account();
        acc.setAccountID(rs.getInt("accountID"));
        s.setAccount(acc);

        return s;
    }

    public boolean insert(Staff s) {

        String sql = """
            INSERT INTO Staff
            (fullName, gender, phoneNumber, hireDate, salary, accountID)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getGender());
            ps.setString(3, s.getPhoneNumber());
            ps.setDate(4, Date.valueOf(s.getHireDate()));
            ps.setDouble(5, s.getSalary());
            ps.setInt(6, s.getAccountID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Insert staff failed", e);
        }
    }


    // Lấy Staff theo staffID
    public Staff getByID(int staffID) {

        String sql = "SELECT * FROM Staff WHERE staffID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, staffID);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? map(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Get staff by ID failed", e);
        }
    }

    // ✅ QUAN TRỌNG: Lấy Staff theo accountID
    public Staff getByAccountID(int accountID) {

        String sql = "SELECT * FROM Staff WHERE accountID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? map(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Get staff by accountID failed", e);
        }
    }


    public String getNameByID(int staffID) {

        String sql = "SELECT fullName FROM Staff WHERE staffID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, staffID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("fullName");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get staff name by ID failed", e);
        }

        return "Không xác định";
    }

    public String getNameByAccountID(int accountID) {

        String sql = "SELECT fullName FROM Staff WHERE accountID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("fullName");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get staff name by accountID failed", e);
        }

        return null;
    }

    public List<Staff> getAll() {

        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get staff list failed", e);
        }

        return list;
    }

    /* =====================================================
       =================== UPDATE ==========================
       ===================================================== */

    public boolean update(Staff s) {

        String sql = """
            UPDATE Staff
            SET fullName = ?, gender = ?, phoneNumber = ?, hireDate = ?, salary = ?
            WHERE staffID = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getGender());
            ps.setString(3, s.getPhoneNumber());
            ps.setDate(4, Date.valueOf(s.getHireDate()));
            ps.setDouble(5, s.getSalary());
            ps.setInt(6, s.getStaffID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Update staff failed", e);
        }
    }

    /* =====================================================
       =================== DELETE ==========================
       ===================================================== */

    public boolean delete(int staffID) {

        String sql = "DELETE FROM Staff WHERE staffID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, staffID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Delete staff failed", e);
        }
    }

    /* =====================================================
       ===== JOIN STAFF + ACCOUNT (FOR ADMIN UI) ============
       ===================================================== */

    public List<StaffAccount> getAllWithAccount() {

        List<StaffAccount> list = new ArrayList<>();

        String sql = """
            SELECT
                s.staffID, s.fullName, s.gender, s.phoneNumber, s.salary, s.accountID,
                a.username, a.password
            FROM Staff s
            JOIN Account a ON s.accountID = a.accountID
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StaffAccount dto = new StaffAccount();

                dto.setStaffID(rs.getInt("staffID"));
                dto.setFullName(rs.getString("fullName"));
                dto.setGender(rs.getString("gender"));
                dto.setPhone(rs.getString("phoneNumber"));
                dto.setSalary(rs.getDouble("salary"));

                dto.setAccountID(rs.getInt("accountID"));
                dto.setUsername(rs.getString("username"));
                dto.setPassword(rs.getString("password"));

                list.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Get staff-account list failed", e);
        }

        return list;
    }
}