package data;

import model.MemberPT;
import util.DBConnection;

import java.sql.*;

public class MemberPTDAO {

    /* ================= INSERT ================= */
    public boolean insert(MemberPT mp) {
        String sql = """
            INSERT INTO MemberPT
            (memberID, serviceID, usedSessions, invoiceID)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, mp.getMemberID());
            ps.setInt(2, mp.getServiceID());
            ps.setInt(3, mp.getUsedSessions());
            ps.setInt(4, mp.getInvoiceID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Insert MemberPT failed", e);
        }
    }

    /* ================= GET LATEST ================= */
    public MemberPT getByMember(int memberID) {
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
            mp.setMemberID(memberID);
            mp.setServiceID(rs.getInt("serviceID"));
            mp.setUsedSessions(rs.getInt("usedSessions"));
            mp.setInvoiceID(rs.getInt("invoiceID"));
            return mp;

        } catch (SQLException e) {
            throw new RuntimeException("Get MemberPT failed", e);
        }
    }

    /* ================= CONSUME SESSION ================= */
    public boolean consumeSession(int memberPTID) {
        String sql = """
            UPDATE MemberPT
            SET usedSessions = usedSessions + 1
            WHERE memberPTID = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberPTID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Consume PT session failed", e);
        }
    }
}