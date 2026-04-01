package data;

import model.Account;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    /* ================= MAP RESULT ================= */
    private Account map(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setAccountID(rs.getInt("accountID"));
        acc.setUsername(rs.getString("username"));
        acc.setPassword(rs.getString("password"));
        acc.setRole(rs.getString("role"));
        acc.setStatus(rs.getString("status"));

        Date date = rs.getDate("createDate");
        if (date != null) {
            acc.setCreateDate(date.toLocalDate());
        }
        return acc;
    }

    /* ================= SELECT ================= */
    public Account getById(int id) throws SQLException {
        String sql = "SELECT * FROM Account WHERE accountID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Account getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Account WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Account> getAll() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /* ================= INSERT ================= */

    // Dùng cho Admin/System (không cần ID)
    public boolean insert(Account acc) throws SQLException {
        String sql = "INSERT INTO Account(username,password,role,status) VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getRole());
            ps.setString(4, acc.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    // Dùng cho Staff (BẮT BUỘC)
    public int insertAndReturnID(Account acc) throws SQLException {
        String sql = "INSERT INTO Account(username,password,role,status) VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getRole());
            ps.setString(4, acc.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /* ================= UPDATE & DELETE ================= */
    public boolean update(Account acc) throws SQLException {
        String sql = """
            UPDATE Account 
            SET username=?, password=?, role=?, status=?
            WHERE accountID=?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getRole());
            ps.setString(4, acc.getStatus());
            ps.setInt(5, acc.getAccountID());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Account WHERE accountID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /* ================= CHECK RELATION ================= */
    public boolean isLinkedToStaff(int accountID) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE accountID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}