package business;

import data.CheckInDAO;
import data.MemberDAO;
import data.MemberPackageDAO;
import model.CheckIn;
import model.Member;
import model.MemberPackage;

import java.time.LocalDateTime;
import java.util.List;

public class CheckInBUS {

    private final MemberDAO memberDAO = new MemberDAO();
    private final CheckInDAO checkInDAO = new CheckInDAO();
    private final MemberPackageDAO packageDAO = new MemberPackageDAO();

    /* ================= CHECK-IN NGHIỆP VỤ ================= */

    public Member checkIn(String phoneNumber) throws Exception {

        // Kiểm tra hội viên tồn tại
        Member member = memberDAO.getByPhone(phoneNumber);
        if (member == null) {
            throw new Exception("Hội viên không tồn tại");
        }

        //  KIỂM TRA GÓI TẬP CÒN HIỆU LỰC
        MemberPackage pkg =
                packageDAO.getActiveByMember(member.getMemberID());

        if (pkg == null) {
            throw new Exception("Gói tập đã hết hạn hoặc chưa đăng ký");
        }

        // Không cho check-in trùng ngày
        if (checkInDAO.hasCheckedInToday(phoneNumber)) {
            throw new Exception("Hội viên đã check-in hôm nay");
        }

        // Ghi nhận check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setPhoneNumber(phoneNumber);
        checkIn.setCheckInTime(LocalDateTime.now());

        checkInDAO.insert(checkIn);

        return member; // trả về MEMBER thật cho UI
    }

    /* ================= DỮ LIỆU TAB CHECK-IN ================= */

    /**
     * Tổng số lượt check-in (COUNT trực tiếp từ DB)
     */
    public int getTotalCheckIn(String phoneNumber) {
        return checkInDAO.countByPhone(phoneNumber);
    }

    /**
     * Lần check-in gần nhất
     */
    public LocalDateTime getLatestCheckIn(String phoneNumber) {
        return checkInDAO.getLatestByPhone(phoneNumber);
    }

    /**
     * Lịch sử check-in (log)
     */
    public List<CheckIn> getCheckInHistory(String phoneNumber) {
        return checkInDAO.getByPhone(phoneNumber);
    }
}
