package business;

import data.MemberPTDAO;
import data.PTServiceDAO;
import model.MemberPT;
import model.PTService;

public class MemberPTBUS {

    private final MemberPTDAO memberPTDAO = new MemberPTDAO();
    private final PTServiceDAO ptServiceDAO = new PTServiceDAO();

    /**
     * Mua dịch vụ PT cho hội viên
     */
    public void buyPT(int memberID, int serviceID) {

        if (memberID <= 0)
            throw new IllegalArgumentException("MemberID không hợp lệ");

        if (serviceID <= 0)
            throw new IllegalArgumentException("ServiceID không hợp lệ");

        // 1. Kiểm tra dịch vụ PT
        PTService service = ptServiceDAO.findById(serviceID);
        if (service == null)
            throw new IllegalArgumentException("Dịch vụ PT không tồn tại");

        // 2. Tạo MemberPT
        MemberPT mp = new MemberPT();
        mp.setMemberID(memberID);
        mp.setServiceID(serviceID);
        mp.setUsedSessions(0);
        mp.setInvoiceID(0); // sau này gắn hóa đơn

        boolean ok = memberPTDAO.insert(mp);
        if (!ok)
            throw new RuntimeException("Mua dịch vụ PT thất bại");
    }
}