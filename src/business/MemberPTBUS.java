package business;

import data.MemberPTDAO;
import data.PTServiceDAO;
import model.MemberPT;
import model.MemberPTItem;
import model.PTService;

public class MemberPTBUS {

    private final MemberPTDAO memberPTDAO = new MemberPTDAO();
    private final PTServiceDAO ptServiceDAO = new PTServiceDAO();

    /* ===================== CHECK CÒN BUỔI ===================== */

    /**
     * Kiểm tra gói PT còn buổi để ĐĂNG KÝ LỊCH hay không
     */
    public boolean canUseSession(int memberPTID) {

        if (memberPTID <= 0)
            throw new IllegalArgumentException("MemberPTID không hợp lệ");

        MemberPT mp = memberPTDAO.findById(memberPTID);
        if (mp == null)
            throw new IllegalArgumentException("Không tìm thấy gói PT");

        PTService service = ptServiceDAO.findById(mp.getServiceID());
        if (service == null)
            throw new IllegalStateException("Dịch vụ PT không tồn tại");

        return mp.getUsedSessions() < service.getTotalSessions();
    }

    /* ===================== TRỪ BUỔI (KHI DONE) ===================== */

    /**
     * Trừ 1 buổi PT
     * ⚠️ CHỈ gọi khi buổi tập đã HOÀN THÀNH (status = DONE)
     */
    public void useSessionAfterDone(int memberPTID) {

        if (memberPTID <= 0)
            throw new IllegalArgumentException("MemberPTID không hợp lệ");

        MemberPT mp = memberPTDAO.findById(memberPTID);
        if (mp == null)
            throw new IllegalArgumentException("Không tìm thấy gói PT");

        PTService service = ptServiceDAO.findById(mp.getServiceID());
        if (service == null)
            throw new IllegalStateException("Dịch vụ PT không tồn tại");

        int used = mp.getUsedSessions();
        int total = service.getTotalSessions();

        if (used >= total)
            throw new IllegalStateException("Gói PT đã dùng hết buổi");

        memberPTDAO.updateUsedSessions(memberPTID, used + 1);
    }

    public Iterable<MemberPTItem> getActiveMemberPTItems() {
        return memberPTDAO.getActiveMemberPTItems();
    }
}
