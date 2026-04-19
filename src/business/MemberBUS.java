package business;

import data.MemberDAO;
import model.Member;
import model.Permission;
import util.Session;

import java.util.ArrayList;
import java.util.List;

public class MemberBUS {

    private final MemberDAO dao = new MemberDAO();

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("0\\d{9}");
    }

    // ===== ADD (ADMIN + STAFF) =====
    public Member add(Member m) {
        AuthorizationService.check(Permission.MEMBER_ADD);

        if (!isValidPhone(m.getPhoneNumber()))
            throw new IllegalArgumentException(
                    "Số điện thoại không hợp lệ! Phải bắt đầu bằng 0 và đủ 10 số."
            );

        if (dao.findByPhone(m.getPhoneNumber()) != null)
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");

        if (!dao.insert(m))
            throw new RuntimeException("Thêm thất bại!");

        // ✅ Trả về hội viên vừa tạo (có memberID)
        return dao.getLatest();
    }

    // ===== UPDATE (ADMIN) =====
    public void update(Member m) {
        AuthorizationService.check(Permission.MEMBER_UPDATE);

        if (!isValidPhone(m.getPhoneNumber()))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ!");

        Member exist = dao.findByPhone(m.getPhoneNumber());
        if (exist != null && exist.getMemberID() != m.getMemberID())
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");

        if (!dao.update(m))
            throw new RuntimeException("Cập nhật thất bại!");
    }

    // ===== DELETE (ADMIN) =====
    public void delete(int id) {
        AuthorizationService.check(Permission.MEMBER_DELETE);

        if (!dao.delete(id))
            throw new RuntimeException("Xóa thất bại!");
    }

    // ===== SEARCH (ADMIN + STAFF) =====
    public List<Member> search(String keyword) {
        AuthorizationService.check(Permission.MEMBER_VIEW);

        List<Member> list = dao.getAll();
        if (keyword == null || keyword.isBlank()) return list;

        List<Member> rs = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for (Member m : list) {
            if (m.getFullName().toLowerCase().contains(keyword)) {
                rs.add(m);
            }
        }
        return rs;
    }
}