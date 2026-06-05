package business;

import data.MemberPackageDAO;
import data.MembershipPackageDAO;
import model.MemberPackage;
import model.MembershipPackage;

import java.time.LocalDate;

public class MemberPackageBUS {

    private final MemberPackageDAO memberPackageDAO = new MemberPackageDAO();
    private final MembershipPackageDAO membershipPackageDAO = new MembershipPackageDAO();

    public void registerPackage(int memberID, int packageID, LocalDate startDate) {

        if (memberID <= 0)
            throw new IllegalArgumentException("MemberID không hợp lệ");

        if (packageID <= 0)
            throw new IllegalArgumentException("PackageID không hợp lệ");

        // Lấy thông tin gói tập
        MembershipPackage pkg = membershipPackageDAO.findById(packageID);
        if (pkg == null)
            throw new IllegalArgumentException("Gói tập không tồn tại");

        // Nếu người dùng không chọn ngày bắt đầu
        if (startDate == null) {
            startDate = LocalDate.now();
        }

        // Kiểm tra gói hiện tại
        MemberPackage current = memberPackageDAO.getActiveByMember(memberID);

        if (current != null) {
            throw new IllegalArgumentException(
                    "Hội viên đang có gói tập còn hiệu lực. Vui lòng gia hạn sau."
            );
        }

        // Tính ngày kết thúc dựa trên ngày bắt đầu đã chọn
        LocalDate endDate = startDate.plusMonths(pkg.getDuration());

        MemberPackage mp = new MemberPackage();
        mp.setMemberID(memberID);
        mp.setPackageID(packageID);
        mp.setStartDate(startDate);
        mp.setEndDate(endDate);
        mp.setStatus("Active");

        boolean ok = memberPackageDAO.insert(mp);

        if (!ok)
            throw new RuntimeException("Đăng ký gói tập thất bại");
    }
}
