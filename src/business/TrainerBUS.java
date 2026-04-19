package business;

import data.TrainerDAO;
import model.Trainer;
import model.Permission;

import java.util.ArrayList;
import java.util.List;

public class TrainerBUS {

    private final TrainerDAO dao = new TrainerDAO();

    // ================== VALIDATE PHONE ==================
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("0\\d{9}");
    }

    // ================== VALIDATE SALARY ==================
    private boolean isValidSalary(double salary) {
        return salary > 0;
    }

    // ===== ADD (ADMIN + STAFF) =====
    public void add(Trainer t) {
        AuthorizationService.check(Permission.TRAINER_ADD);

        if (t == null)
            throw new IllegalArgumentException("Trainer không được null!");

        if (t.getFullName() == null || t.getFullName().isBlank())
            throw new IllegalArgumentException("Tên huấn luyện viên không được rỗng!");

        if (!isValidPhone(t.getPhoneNumber()))
            throw new IllegalArgumentException(
                    "Số điện thoại không hợp lệ! Phải bắt đầu bằng 0 và đủ 10 số."
            );

        //  CHECK SALARY
        if (!isValidSalary(t.getSalary()))
            throw new IllegalArgumentException("Lương phải lớn hơn 0!");

        if (dao.findByPhone(t.getPhoneNumber()) != null)
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");

        if (!dao.insert(t))
            throw new RuntimeException("Thêm huấn luyện viên thất bại!");
    }

    // ===== UPDATE (ADMIN) =====
    public void update(Trainer t) {
        AuthorizationService.check(Permission.TRAINER_UPDATE);

        if (t == null || t.getTrainerID() <= 0)
            throw new IllegalArgumentException("TrainerID không hợp lệ!");

        if (t.getFullName() == null || t.getFullName().isBlank())
            throw new IllegalArgumentException("Tên huấn luyện viên không được rỗng!");

        if (!isValidPhone(t.getPhoneNumber()))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ!");

        //  CHECK SALARY
        if (!isValidSalary(t.getSalary()))
            throw new IllegalArgumentException("Lương phải lớn hơn 0!");

        Trainer exist = dao.findByPhone(t.getPhoneNumber());
        if (exist != null && exist.getTrainerID() != t.getTrainerID())
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");

        if (!dao.update(t))
            throw new RuntimeException("Cập nhật huấn luyện viên thất bại!");
    }

    // ===== DELETE (ADMIN) =====
    public void delete(int id) {
        AuthorizationService.check(Permission.TRAINER_DELETE);

        if (id <= 0)
            throw new IllegalArgumentException("TrainerID không hợp lệ!");

        if (!dao.delete(id))
            throw new RuntimeException("Xóa huấn luyện viên thất bại!");
    }

    // ===== SEARCH / VIEW (ADMIN + STAFF) =====
    public List<Trainer> search(String keyword) {
        AuthorizationService.check(Permission.TRAINER_VIEW);

        List<Trainer> list = dao.getAll();
        if (keyword == null || keyword.isBlank()) return list;

        List<Trainer> rs = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for (Trainer t : list) {
            if (t.getFullName() != null &&
                    t.getFullName().toLowerCase().contains(keyword)) {
                rs.add(t);
            }
        }
        return rs;
    }

    // ===== GET ALL (ADMIN + STAFF) =====
    public List<Trainer> getAll() {
        AuthorizationService.check(Permission.TRAINER_VIEW);
        return dao.getAll();
    }

    // ===== GET BY ID (ADMIN + STAFF) =====
    public Trainer getById(int id) {
        AuthorizationService.check(Permission.TRAINER_VIEW);
        return dao.findById(id);
    }
}