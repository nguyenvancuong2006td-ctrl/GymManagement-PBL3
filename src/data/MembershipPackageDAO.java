package data;

import model.MembershipPackage;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipPackageDAO {

    private MembershipPackage map(ResultSet rs) throws SQLException {
        MembershipPackage p = new MembershipPackage();
        p.setPackageID(rs.getInt("packageID"));
        p.setPackageName(rs.getString("packageName"));
        p.setDuration(rs.getInt("duration"));
        p.setPrice(rs.getDouble("price"));
        return p;
    }

    public List<MembershipPackage> getAll() {
        List<MembershipPackage> list = new ArrayList<>();
        String sql = "SELECT * FROM MembershipPackage";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public MembershipPackage findById(int id) {
        String sql = "SELECT * FROM MembershipPackage WHERE packageID=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insert(MembershipPackage p) {
        String sql = """
            INSERT INTO MembershipPackage (packageName, duration, price)
            VALUES (?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getPackageName());
            ps.setInt(2, p.getDuration());
            ps.setDouble(3, p.getPrice());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(MembershipPackage p) {
        String sql = """
            UPDATE MembershipPackage
            SET packageName=?, duration=?, price=?
            WHERE packageID=?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getPackageName());
            ps.setInt(2, p.getDuration());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getPackageID());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM MembershipPackage WHERE packageID=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNameByID(int packageID) {

        String sql = "SELECT packageName FROM MembershipPackage WHERE packageID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, packageID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("packageName");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Không lấy được tên gói tập", e);
        }

        return "Không xác định";
    }

}