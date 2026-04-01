package data;

import model.Member;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class MemberDAO {

    // ================== MAP RESULTSET ==================
    private Member mapResultSet(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setMemberID(rs.getInt("memberID"));
        m.setFullName(rs.getString("fullName"));
        m.setGender(rs.getString("gender"));
        m.setPhoneNumber(rs.getString("phoneNumber"));

        Date d = rs.getDate("joinDate");
        if (d != null) {
            m.setJoinDate(d.toLocalDate());
        }

        return m;
    }

    // ================== GET ALL ==================
    public ArrayList<Member> getAll() {
        ArrayList<Member> list = new ArrayList<>();

        String sql = """
            SELECT memberID, fullName, gender, phoneNumber, joinDate
            FROM Member
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            System.err.println("Error at getAll: " + e.getMessage());
        }

        return list;
    }

    // ================== FIND BY ID ==================
    public Member findById(int id) {
        String sql = "SELECT * FROM Member WHERE memberID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error at findById: " + e.getMessage());
        }

        return null;
    }

    // ================== FIND BY PHONE ==================
    public Member findByPhone(String phone) {
        String sql = "SELECT * FROM Member WHERE phoneNumber = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error at findByPhone: " + e.getMessage());
        }

        return null;
    }

    // ================== INSERT ==================
    public boolean insert(Member m) {
        String sql = """
            INSERT INTO Member(fullName, gender, phoneNumber, joinDate)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getFullName());
            ps.setString(2, m.getGender());
            ps.setString(3, m.getPhoneNumber());

            if (m.getJoinDate() != null) {
                ps.setDate(4, Date.valueOf(m.getJoinDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique")) {
                System.err.println("Phone number already exists!");
            } else {
                System.err.println("Error at insert: " + e.getMessage());
            }
        }
        return false;
    }

    // ================== UPDATE ==================
    public boolean update(Member m) {
        String sql = """
            UPDATE Member
            SET fullName = ?, gender = ?, phoneNumber = ?, joinDate = ?
            WHERE memberID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getFullName());
            ps.setString(2, m.getGender());
            ps.setString(3, m.getPhoneNumber());

            if (m.getJoinDate() != null) {
                ps.setDate(4, Date.valueOf(m.getJoinDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setInt(5, m.getMemberID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error at update: " + e.getMessage());
        }
        return false;
    }

    // ================== DELETE ==================
    public boolean delete(int memberID) {
        String sql = "DELETE FROM Member WHERE memberID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error at delete: " + e.getMessage());
        }
        return false;
    }
}