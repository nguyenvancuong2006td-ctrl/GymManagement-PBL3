package data;

import model.MemberPackage;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberPackageDAO {

    /* ================= INSERT ================= */
    public boolean insert(MemberPackage mp) {
        String sql = """
            INSERT INTO MemberPackage
            (memberID, packageID, startDate, endDate, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, mp.getMemberID());
            ps.setInt(2, mp.getPackageID());
            ps.setDate(3, Date.valueOf(mp.getStartDate()));
            ps.setDate(4, Date.valueOf(mp.getEndDate()));
            ps.setString(5, mp.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Insert MemberPackage failed", e);
        }
    }

    /* ================= GET ACTIVE ================= */
    public MemberPackage getActiveByMember(int memberID) {
        String sql = """
            SELECT TOP 1 *
            FROM MemberPackage
            WHERE memberID = ?
              AND status = 'ACTIVE'
              AND endDate >= CAST(GETDATE() AS DATE)
            ORDER BY endDate DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;
            return map(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Get active MemberPackage failed", e);
        }
    }

    /* ================= GET ALL BY MEMBER ================= */
    public List<MemberPackage> getAllByMember(int memberID) {
        List<MemberPackage> list = new ArrayList<>();
        String sql = """
            SELECT *
            FROM MemberPackage
            WHERE memberID = ?
            ORDER BY startDate DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get MemberPackage history failed", e);
        }
        return list;
    }

    /* ================= MAP ================= */
    private MemberPackage map(ResultSet rs) throws SQLException {
        MemberPackage mp = new MemberPackage();
        mp.setMemberPackageID(rs.getInt("memberPackageID"));
        mp.setMemberID(rs.getInt("memberID"));
        mp.setPackageID(rs.getInt("packageID"));
        mp.setStartDate(rs.getDate("startDate").toLocalDate());
        mp.setEndDate(rs.getDate("endDate").toLocalDate());
        mp.setStatus(rs.getString("status"));
        return mp;
    }
}