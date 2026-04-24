package business;

import data.MemberPackageDAO;
import data.MembershipPackageDAO;
import model.MemberPackage;
import model.MembershipPackage;

import java.time.LocalDate;

public class MemberPackageBUS {

    private final MemberPackageDAO memberPackageDAO = new MemberPackageDAO();
    private final MembershipPackageDAO membershipPackageDAO = new MembershipPackageDAO();

    /**
     * Đăng ký hoặc gia hạn gói tập cho hội viên
     * - KHÔNG tạo hóa đơn ở đây
     * - Hóa đơn sẽ tạo ở luồng thanh toán
     */
    public void registerPackage(int memberID, int packageID) {

        if (memberID <= 0)
            throw new IllegalArgumentException("MemberID không hợp lệ");

        if (packageID <= 0)
            throw new IllegalArgumentException("PackageID không hợp lệ");

        // 1. Lấy thông tin gói tập
        MembershipPackage pkg = membershipPackageDAO.findById(packageID);
        if (pkg == null)
            throw new IllegalArgumentException("Gói tập không tồn tại");

        // 2. Kiểm tra gói hiện tại của hội viên
        MemberPackage current = memberPackageDAO.getActiveByMember(memberID);

        LocalDate startDate;
        if (current == null) {
            // Mua mới
            startDate = LocalDate.now();
        } else {
            // Gia hạn – nối tiếp ngày
            startDate = current.getEndDate().plusDays(1);
        }

        // 3. Tính ngày kết thúc
        LocalDate endDate = startDate.plusMonths(pkg.getDuration());

        // 4. Tạo MemberPackage (KHÔNG invoice)
        MemberPackage mp = new MemberPackage();
        mp.setMemberID(memberID);
        mp.setPackageID(packageID);
        mp.setStartDate(startDate);
        mp.setEndDate(endDate);
        mp.setStatus("Active");

        // 5. Insert
        boolean ok = memberPackageDAO.insert(mp);
        if (!ok)
            throw new RuntimeException("Đăng ký / gia hạn gói tập thất bại");
    }
}
