package data;

import model.Account;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // ================== MAP RESULT ==================
    private Account mapResultSet(ResultSet rs) throws SQLException {

        Account acc = new Account();

        acc.setAccountID(rs.getInt("accountID"));
        acc.setUsername(rs.getString("username"));


        acc.setPassword(rs.getString("password"));

        acc.setRole(rs.getString("role"));

        Date date = rs.getDate("createDate");
        if (date != null) {
            acc.setCreateDate(date.toLocalDate());
        }

        acc.setStatus(rs.getString("status"));

        return acc;
    }

    // ================== SELECT ALL ==================
    public List<Account> getAll() throws SQLException {

        List<Account> list = new ArrayList<>();

        String sql = "SELECT accountID, username, password, role, createDate, status FROM Account";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }

        return list;
    }

    // ================== SELECT BY ID ==================
    public Account getById(int id) throws SQLException {

        String sql = "SELECT accountID, username, password, role, createDate, status FROM Account WHERE accountID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    // ================== SELECT BY USERNAME ==================
    public Account getByUsername(String username) throws SQLException {

        String sql = "SELECT accountID, username, password, role, createDate, status FROM Account WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    // ================== INSERT ==================
    public boolean insert(Account acc) throws SQLException {

        String sql = "INSERT INTO Account(username, password, role, status) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword()); // ✅ KHÔNG HASH
            ps.setString(3, acc.getRole());
            ps.setString(4, acc.getStatus());

            return ps.executeUpdate() > 0;
        }
    }

    // ================== UPDATE ==================
    public boolean update(Account acc) throws SQLException {

        String sql = "UPDATE Account SET username=?, password=?, role=?, status=? WHERE accountID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword()); // ✅ KHÔNG HASH
            ps.setString(3, acc.getRole());
            ps.setString(4, acc.getStatus());
            ps.setInt(5, acc.getAccountID());

            return ps.executeUpdate() > 0;
        }
    }

    // ================== DELETE ==================
    public boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM Account WHERE accountID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}