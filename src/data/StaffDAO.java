package data;

import model.Staff;
import model.Account;
import model.StaffAccount;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    /* ================= MAP STAFF ================= */
    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffID(rs.getInt("staffID"));
        s.setFullName(rs.getString("fullName"));
        s.setGender(rs.getString("gender"));
        s.setPhoneNumber(rs.getString("phoneNumber"));
        s.setSalary(rs.getDouble("salary"));

        Date d = rs.getDate("hireDate");
        if (d != null) s.setHireDate(d.toLocalDate());

        Account acc = new Account();
        acc.setAccountID(rs.getInt("accountID"));
        s.setAccount(acc);

        return s;
    }

    /* ================= INSERT ================= */
    public boolean insert(Staff s) throws SQLException {
        String sql = """
            INSERT INTO Staff(fullName, gender, phoneNumber, salary, hireDate, accountID)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getGender());
            ps.setString(3, s.getPhoneNumber());
            ps.setDouble(4, s.getSalary());
            ps.setDate(5, Date.valueOf(s.getHireDate()));
            ps.setInt(6, s.getAccountID());

            return ps.executeUpdate() > 0;
        }
    }

    /* ================= SELECT STAFF ================= */

    public Staff getByAccountID(int accountID) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE accountID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Staff> getAll() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /* ================= UPDATE ================= */

    public boolean update(Staff s) throws SQLException {
        String sql = """
            UPDATE Staff 
            SET fullName=?, gender=?, phoneNumber=?, salary=?, hireDate=?
            WHERE staffID=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getGender());
            ps.setString(3, s.getPhoneNumber());
            ps.setDouble(4, s.getSalary());
            ps.setDate(5, Date.valueOf(s.getHireDate()));
            ps.setInt(6, s.getStaffID());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int staffID) throws SQLException {
        String sql = "DELETE FROM Staff WHERE staffID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, staffID);
            return ps.executeUpdate() > 0;
        }
    }

    /* =========================================================
             JOIN STAFF + ACCOUNT
       ========================================================= */

    public List<StaffAccount> getAllWithAccount() {
        List<StaffAccount> list = new ArrayList<>();

        String sql = """
            SELECT s.staffID, s.fullName, s.gender, s.phoneNumber, s.salary,
                   s.accountID,
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getNameByID(int staffID) {
        String sql = "SELECT fullName FROM Staff WHERE staffID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, staffID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("fullName");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting staff name by ID", e);
        }

        return "Không xác định";
    }
}
