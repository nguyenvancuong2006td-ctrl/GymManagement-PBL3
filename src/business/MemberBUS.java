package business;

import data.MemberDAO;
import model.Member;

import java.util.ArrayList;

public class MemberBUS {

    private MemberDAO dao = new MemberDAO();

    // ================== GET ALL ==================
    public ArrayList<Member> getAllMembers() {
        return dao.getAll();
    }

    // ================== FIND ==================
    public Member getById(int id) {
        return dao.findById(id);
    }

    public Member getByPhone(String phone) {
        return dao.findByPhone(phone);
    }

    // ================== VALIDATION ==================
    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("0\\d{9}");
    }

    private boolean isValidGender(String gender) {
        return gender != null &&
                (gender.equalsIgnoreCase("Male")
                        || gender.equalsIgnoreCase("Female"));
    }

    // ================== INSERT ==================
    public String addMember(Member m) {

        if (m == null) return "Dữ liệu không hợp lệ!";

        // Chuẩn hóa dữ liệu
        m.setFullName(m.getFullName().trim());
        m.setPhoneNumber(m.getPhoneNumber().trim());
        m.setGender(m.getGender().trim());

        if (!isValidName(m.getFullName())) {
            return "Tên không hợp lệ!";
        }

        if (!isValidPhone(m.getPhoneNumber())) {
            return "Số điện thoại phải đúng định dạng VN (10 số, bắt đầu bằng 0)!";
        }

        if (!isValidGender(m.getGender())) {
            return "Giới tính phải là Male hoặc Female!";
        }

        // Check trùng phone
        if (dao.findByPhone(m.getPhoneNumber()) != null) {
            return "Số điện thoại đã tồn tại!";
        }

        boolean result = dao.insert(m);
        return result ? "Thêm thành công!" : "Thêm thất bại!";
    }

    // ================== UPDATE ==================
    public String updateMember(Member m) {

        if (m == null) return "Dữ liệu không hợp lệ!";

        if (m.getMemberID() <= 0) {
            return "ID không hợp lệ!";
        }

        // Check tồn tại
        if (dao.findById(m.getMemberID()) == null) {
            return "Member không tồn tại!";
        }

        // Chuẩn hóa dữ liệu
        m.setFullName(m.getFullName().trim());
        m.setPhoneNumber(m.getPhoneNumber().trim());
        m.setGender(m.getGender().trim());

        if (!isValidName(m.getFullName())) {
            return "Tên không hợp lệ!";
        }

        if (!isValidPhone(m.getPhoneNumber())) {
            return "Số điện thoại phải đúng định dạng VN (10 số, bắt đầu bằng 0)!";
        }

        if (!isValidGender(m.getGender())) {
            return "Giới tính phải là Male hoặc Female!";
        }

        // Check trùng phone (trừ chính nó)
        Member exist = dao.findByPhone(m.getPhoneNumber());
        if (exist != null && exist.getMemberID() != m.getMemberID()) {
            return "Số điện thoại đã tồn tại!";
        }

        boolean result = dao.update(m);
        return result ? "Cập nhật thành công!" : "Cập nhật thất bại!";
    }

    // ================== DELETE ==================
    public String deleteMember(int id) {

        if (id <= 0) {
            return "ID không hợp lệ!";
        }

        // Check tồn tại
        if (dao.findById(id) == null) {
            return "Member không tồn tại!";
        }

        boolean result = dao.delete(id);
        return result ? "Xóa thành công!" : "Xóa thất bại!";
    }

    // ================== SEARCH ==================
    public ArrayList<Member> searchByName(String keyword) {

        ArrayList<Member> list = dao.getAll();
        ArrayList<Member> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return list;
        }

        keyword = keyword.toLowerCase().trim();

        for (Member m : list) {
            if (m.getFullName().toLowerCase().contains(keyword)) {
                result.add(m);
            }
        }

        return result;
    }
}