package data;

import model.Member;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    // ================== MAP RESULTSET ==================
    private Member mapResultSet(ResultSet rs) throws SQLException {
        Member m = new Member();

        m.setMemberID(rs.getInt("memberID"));
        m.setFullName(rs.getString("fullName"));
        m.setGender(rs.getString("gender"));
        m.setPhoneNumber(rs.getString("phoneNumber"));

        Timestamp ts = rs.getTimestamp("joinDate");
        if (ts != null) {
            m.setJoinDate(ts.toLocalDateTime());
        }

        return m;
    }

    // ================== GET ALL ==================
    public List<Member> getAll() {
        List<Member> list = new ArrayList<>();

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

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching members", e);
        }

        return list;
    }

    // ================== FIND BY ID ==================
    public Member findById(int id) {
        String sql = """
            SELECT memberID, fullName, gender, phoneNumber, joinDate
            FROM Member
            WHERE memberID = ?
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
            throw new RuntimeException("Error finding member by ID", e);
        }

        return null;
    }

    // ================== FIND BY PHONE ==================
    public Member findByPhone(String phone) {
        String sql = """
            SELECT memberID, fullName, gender, phoneNumber, joinDate
            FROM Member
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
            throw new RuntimeException("Error finding member by phone", e);
        }

        return null;
    }

    // ================== INSERT ==================
    // joinDate KHÔNG truyền → DB tự DEFAULT GETDATE()
    public boolean insert(Member m) {
        String sql = """
            INSERT INTO Member (fullName, gender, phoneNumber)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getFullName());
            ps.setString(2, m.getGender());
            ps.setString(3, m.getPhoneNumber());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                throw new RuntimeException("Phone number already exists", e);
            }
            throw new RuntimeException("Error inserting member", e);
        }
    }

    // ================== UPDATE ==================
    // KHÔNG update joinDate
    public boolean update(Member m) {
        String sql = """
            UPDATE Member
            SET fullName = ?, gender = ?, phoneNumber = ?
            WHERE memberID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getFullName());
            ps.setString(2, m.getGender());
            ps.setString(3, m.getPhoneNumber());
            ps.setInt(4, m.getMemberID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating member", e);
        }
    }

    // ================== DELETE ==================
    public boolean delete(int memberID) {
        String sql = "DELETE FROM Member WHERE memberID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting member", e);
        }
    }


    // ================== GET NAME BY ID ==================
    public String getNameByID(int memberID) {
        String sql = """
            SELECT fullName
            FROM Member
            WHERE memberID = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("fullName");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting member name by ID", e);
        }

        return "Không xác định";
    }


    public Member getLatest() {
        String sql = """
        SELECT TOP 1 *
        FROM Member
        ORDER BY memberID DESC
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return null;

            Member m = new Member();
            m.setMemberID(rs.getInt("memberID"));
            m.setFullName(rs.getString("fullName"));
            m.setGender(rs.getString("gender"));
            m.setPhoneNumber(rs.getString("phoneNumber"));
            m.setJoinDate(rs.getTimestamp("joinDate").toLocalDateTime());
            return m;

        } catch (Exception e) {
            throw new RuntimeException("Get latest member failed", e);
        }
    }

}