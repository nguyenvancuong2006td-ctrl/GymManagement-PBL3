package business;

import data.*;
import model.CheckIn;
import model.Member;
import model.MemberPT;
import model.MemberPackage;

import java.time.LocalDateTime;
import java.util.List;

public class CheckInBUS {

    private final MemberDAO memberDAO = new MemberDAO();
    private final CheckInDAO checkInDAO = new CheckInDAO();
    private final MemberPackageDAO packageDAO = new MemberPackageDAO();
    private final MemberPTDAO memberPTDAO = new MemberPTDAO();
    private final WorkoutScheduleDAO scheduleDAO = new WorkoutScheduleDAO();


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

        if (java.time.LocalDate.now().isBefore(pkg.getStartDate())) {

            throw new Exception(
                    "Gói tập chưa đến ngày sử dụng.\n" +
                            "Ngày bắt đầu: " +
                            pkg.getStartDate()
            );
        }

        if (checkInDAO.isChecking(phoneNumber)) {
            throw new Exception("Hội viên chưa check-out");
        }

        // Ghi nhận check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setPhoneNumber(phoneNumber);
        checkIn.setCheckInTime(LocalDateTime.now());

        checkInDAO.insert(checkIn);

        return member;
    }

    public void checkOut(String phoneNumber) throws Exception {

        // 1. check member
        Member member = memberDAO.getByPhone(phoneNumber);
        if (member == null) {
            throw new Exception("Hội viên không tồn tại");
        }

        if (!checkInDAO.isChecking(phoneNumber)) {
            throw new Exception("Hội viên chưa check-in");
        }

        // 2. Lấy check-in gần nhất (trước khi update)
        LocalDateTime checkInTime = checkInDAO.getLatestByPhone(phoneNumber);
        LocalDateTime checkOutTime = LocalDateTime.now();

        // 3. update checkout
        checkInDAO.checkOut(phoneNumber);

        // 4. lấy memberPT
        MemberPT pt = memberPTDAO.getByMember(member.getMemberID());

        if (pt != null) {
            // 5. gọi logic DONE
            scheduleDAO.markDoneIfValid(
                    pt.getMemberPTID(),
                    checkInTime.toLocalTime(),
                    checkOutTime.toLocalTime()
            );
        }
    }

    /* ================= DỮ LIỆU TAB CHECK-IN ================= */
    public int getTotalCheckIn(String phoneNumber) {
        return checkInDAO.countByPhone(phoneNumber);
    }
    public LocalDateTime getLatestCheckIn(String phoneNumber) {
        return checkInDAO.getLatestByPhone(phoneNumber);
    }
    public List<CheckIn> getCheckInHistory(String phoneNumber) {
        return checkInDAO.getByPhone(phoneNumber);
    }
}
