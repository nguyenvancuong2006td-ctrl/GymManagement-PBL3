package business;

import data.ReportDAO;
import model.CheckIn;
import model.Invoice;

import java.time.LocalDate;
import java.util.List;

public class ReportBUS {

    private final ReportDAO dao = new ReportDAO();

    /* ========== CHECK-IN REPORT ========== */

    public List<Object[]> getCheckInReport(LocalDate from, LocalDate to) {
        validateRange(from, to);
        return dao.getCheckInByDate(from, to);
    }

    public List<CheckIn> getCheckInDetail(LocalDate date) {
        return dao.getCheckInDetail(date);
    }

    /* ========== REVENUE REPORT ========== */

    public List<Object[]> getRevenueReport(LocalDate from, LocalDate to) {
        validateRange(from, to);
        return dao.getRevenueByDate(from, to);
    }

    public List<Invoice> getInvoiceDetail(LocalDate date) {
        return dao.getInvoiceByDate(date);
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ");
        }
    }

    // ReportBUS.java
    public List<Object[]> getMembersExpiring(int days) {
        if (days <= 0) throw new IllegalArgumentException("Số ngày không hợp lệ");
        return dao.getMembersExpiring(days);
    }
}