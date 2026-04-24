package data;

import model.MemberPT;
import model.MemberPTItem;
import util.DBConnection;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberPTDAO {

    /* ===================== INSERT (DÙNG TRONG CHECKOUT) ===================== */
    public boolean insert(MemberPT m, Connection conn) throws SQLException {

        String sql = """
            INSERT INTO MemberPT (memberID, serviceID, usedSessions, invoiceID)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getMemberID());
            ps.setInt(2, m.getServiceID());
            ps.setInt(3, m.getUsedSessions());
            ps.setInt(4, m.getInvoiceID());
            return ps.executeUpdate() > 0;
        }
    }

    /* ===================== FIND BY ID ===================== */
    public MemberPT findById(int memberPTID) {

        String sql = "SELECT * FROM MemberPT WHERE memberPTID = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberPTID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            MemberPT mp = new MemberPT();
            mp.setMemberPTID(rs.getInt("memberPTID"));
            mp.setMemberID(rs.getInt("memberID"));
            mp.setServiceID(rs.getInt("serviceID"));
            mp.setUsedSessions(rs.getInt("usedSessions"));
            mp.setInvoiceID(rs.getInt("invoiceID"));
            return mp;

        } catch (SQLException e) {
            throw new RuntimeException("Find MemberPT by ID failed", e);
        }
    }

    /* ===================== UPDATE USED SESSIONS ===================== */
    public boolean updateUsedSessions(int memberPTID, int usedSessions) {

        String sql = """
            UPDATE MemberPT
            SET usedSessions = ?
            WHERE memberPTID = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, usedSessions);
            ps.setInt(2, memberPTID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Update usedSessions failed", e);
        }
    }

    /* ===================== GET LATEST MEMBER PT BY MEMBER ===================== */
    public MemberPT getByMember(int memberID) {

        if (memberID <= 0)
            throw new IllegalArgumentException("MemberID không hợp lệ");

        String sql = """
        SELECT TOP 1 *
        FROM MemberPT
        WHERE memberID = ?
        ORDER BY memberPTID DESC
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            MemberPT mp = new MemberPT();
            mp.setMemberPTID(rs.getInt("memberPTID"));
            mp.setMemberID(rs.getInt("memberID"));
            mp.setServiceID(rs.getInt("serviceID"));
            mp.setUsedSessions(rs.getInt("usedSessions"));
            mp.setInvoiceID(rs.getInt("invoiceID"));
            return mp;

        } catch (SQLException e) {
            throw new RuntimeException("Get MemberPT by member failed", e);
        }
    }

    public List<MemberPTItem> getActiveMemberPTItems() {

        List<MemberPTItem> list = new ArrayList<>();

        String sql = """
        SELECT 
            mp.memberPTID,
            m.fullName       AS memberName,
            s.serviceName,
            t.trainerID,
            t.fullName       AS trainerName,
            (s.totalSessions - mp.usedSessions) AS remaining
        FROM MemberPT mp
        JOIN Member m      ON mp.memberID = m.memberID
        JOIN PTService s   ON mp.serviceID = s.serviceID
        JOIN Trainer t     ON s.trainerID = t.trainerID
        WHERE mp.usedSessions < s.totalSessions
        ORDER BY m.fullName
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int memberPTID = rs.getInt("memberPTID");
                int trainerID  = rs.getInt("trainerID");
                String trainerName = rs.getString("trainerName");

                String display =
                        "#" + memberPTID + " | " +
                                rs.getString("memberName") + " | " +
                                rs.getString("serviceName") + " | " +
                                "Còn " + rs.getInt("remaining");

                list.add(new MemberPTItem(
                        memberPTID,
                        trainerID,
                        trainerName,
                        display
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

}