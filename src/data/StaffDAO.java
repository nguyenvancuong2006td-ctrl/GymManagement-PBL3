package data;

import model.Staff;
import model.Account;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    /* ================= MAP ================= */
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

    /* ================= SELECT ================= */
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

    /* ================= UPDATE / DELETE ================= */
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
}
