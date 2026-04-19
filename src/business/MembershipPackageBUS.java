package business;

import data.MembershipPackageDAO;
import model.MembershipPackage;
import model.Permission;

import java.util.ArrayList;
import java.util.List;

public class MembershipPackageBUS {

    private final MembershipPackageDAO dao = new MembershipPackageDAO();

    // ===== ADD =====
    public void add(MembershipPackage p) {
        AuthorizationService.check(Permission.PACKAGE_ADD);

        if (p == null)
            throw new IllegalArgumentException("Package không được null!");

        if (p.getPackageName() == null || p.getPackageName().isBlank())
            throw new IllegalArgumentException("Tên gói không được rỗng!");

        if (p.getDuration() <= 0)
            throw new IllegalArgumentException("Thời hạn phải > 0!");

        if (p.getPrice() < 0)
            throw new IllegalArgumentException("Giá không được âm!");

        if (!dao.insert(p))
            throw new RuntimeException("Thêm gói thất bại!");
    }

    // ===== UPDATE =====
    public void update(MembershipPackage p) {
        AuthorizationService.check(Permission.PACKAGE_UPDATE);

        if (p.getPackageID() <= 0)
            throw new IllegalArgumentException("PackageID không hợp lệ!");

        if (p.getPackageName().isBlank())
            throw new IllegalArgumentException("Tên gói không được rỗng!");

        if (p.getDuration() <= 0)
            throw new IllegalArgumentException("Thời hạn phải > 0!");

        if (p.getPrice() < 0)
            throw new IllegalArgumentException("Giá không được âm!");

        if (!dao.update(p))
            throw new RuntimeException("Cập nhật thất bại!");
    }

    // ===== DELETE =====
    public void delete(int id) {
        AuthorizationService.check(Permission.PACKAGE_DELETE);

        if (id <= 0)
            throw new IllegalArgumentException("PackageID không hợp lệ!");

        if (!dao.delete(id))
            throw new RuntimeException("Xóa thất bại!");
    }

    // ===== SEARCH =====
    public List<MembershipPackage> search(String keyword) {
        AuthorizationService.check(Permission.PACKAGE_VIEW);

        List<MembershipPackage> list = dao.getAll();
        if (keyword == null || keyword.isBlank()) return list;

        List<MembershipPackage> rs = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for (MembershipPackage p : list) {
            if (p.getPackageName().toLowerCase().contains(keyword))
                rs.add(p);
        }
        return rs;
    }

    public List<MembershipPackage> getAll() {
        AuthorizationService.check(Permission.PACKAGE_VIEW);
        return dao.getAll();
    }

    public MembershipPackage getById(int id) {
        AuthorizationService.check(Permission.PACKAGE_VIEW);
        return dao.findById(id);
    }
}