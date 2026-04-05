package business;

import data.MemberDAO;
import model.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberBUS {

    private final MemberDAO dao = new MemberDAO();

    private boolean isAdmin(String role) {
        return "Admin".equalsIgnoreCase(role);
    }

    private boolean isStaff(String role) {
        return "Staff".equalsIgnoreCase(role);
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("0\\d{9}");
    }

    // ===== ADD (ADMIN + STAFF) =====
    public String add(Member m, String role) {
        if (!isAdmin(role) && !isStaff(role))
            return "Không có quyền!";

        String phone = m.getPhoneNumber();

        // Kiểm tra định dạng
        if (!isValidPhone(phone))
            return "Số điện thoại không hợp lệ! Phải bắt đầu bằng 0 và đủ 10 số.";

        // Kiểm tra trùng
        if (dao.findByPhone(phone) != null)
            return "Số điện thoại đã tồn tại!";

        return dao.insert(m) ? "Thêm thành công!" : "Thêm thất bại!";
    }

    // ===== UPDATE (ADMIN) =====
    public String update(Member m, String role) {
        if (!isAdmin(role))
            return "Chỉ ADMIN được cập nhật!";

        if (!isValidPhone(m.getPhoneNumber()))
            return "Số điện thoại không hợp lệ!";

        Member exist = dao.findByPhone(m.getPhoneNumber());
        if (exist != null && exist.getMemberID() != m.getMemberID())
            return "Số điện thoại đã tồn tại!";

        return dao.update(m) ? "Cập nhật thành công!" : "Cập nhật thất bại!";
    }

    // ===== DELETE (ADMIN) =====
    public String delete(int id, String role) {
        if (!isAdmin(role))
            return "Chỉ ADMIN được xóa!";

        return dao.delete(id) ? "Xóa thành công!" : "Xóa thất bại!";
    }

    // ===== SEARCH (ADMIN + STAFF) =====
    public List<Member> search(String keyword) {
        List<Member> list = dao.getAll();
        if (keyword == null || keyword.isBlank()) return list;

        List<Member> rs = new ArrayList<>();
        keyword = keyword.toLowerCase();
        for (Member m : list) {
            if (m.getFullName().toLowerCase().contains(keyword))
                rs.add(m);
        }
        return rs;
    }
}
