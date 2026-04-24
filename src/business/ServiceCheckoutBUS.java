package business;

import data.*;
import model.*;
import util.DBConnection;
import util.Session;

import java.math.BigDecimal;
import java.sql.Connection;

public class ServiceCheckoutBUS {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final MembershipPackageDAO packageDAO = new MembershipPackageDAO();
    private final PTServiceDAO ptServiceDAO = new PTServiceDAO();
    private final MemberPTDAO memberPTDAO = new MemberPTDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    public int checkoutService(
            int memberID,
            Integer packageID,
            Integer ptServiceID,
            PaymentMethod method
    ) {

        if (packageID == null && ptServiceID == null) {
            throw new IllegalArgumentException("Không có dịch vụ để thanh toán");
        }

        Staff staff = staffDAO.getByAccountID(Session.getAccountID());
        if (staff == null) {
            throw new IllegalStateException("Tài khoản chưa được gán Staff");
        }

        int staffID = staff.getStaffID();

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            /* ===== 1. TÍNH TIỀN ===== */
            BigDecimal total = BigDecimal.ZERO;

            if (packageID != null) {
                total = total.add(
                        BigDecimal.valueOf(packageDAO.findById(packageID).getPrice())
                );
            }

            if (ptServiceID != null) {
                total = total.add(ptServiceDAO.findById(ptServiceID).getPrice());
            }

            /* ===== 2. TẠO INVOICE ===== */
            Invoice invoice = new Invoice(total, staffID, memberID);
            int invoiceID = invoiceDAO.insert(invoice, conn);

            /* ===== 3. INVOICE DETAIL ===== */
            if (packageID != null) {
                detailDAO.insert(new InvoiceDetail(
                        invoiceID,
                        "PACKAGE",
                        packageID,
                        1,
                        BigDecimal.valueOf(packageDAO.findById(packageID).getPrice())
                ), conn);
            }

            if (ptServiceID != null) {
                detailDAO.insert(new InvoiceDetail(
                        invoiceID,
                        "PT",
                        ptServiceID,
                        1,
                        ptServiceDAO.findById(ptServiceID).getPrice()
                ), conn);

                MemberPT mp = new MemberPT();
                mp.setMemberID(memberID);
                mp.setServiceID(ptServiceID);
                mp.setUsedSessions(0);
                mp.setInvoiceID(invoiceID);
                memberPTDAO.insert(mp, conn);
            }

            /* ===== 4. PAYMENT ===== */
            Payment pay = new Payment();
            pay.setAmount(total);
            pay.setPaymentMethod(method.name());
            pay.setStatus("Completed");
            pay.setMemberID(memberID);
            pay.setStaffID(staffID);
            pay.setInvoiceID(invoiceID);

            paymentDAO.insert(pay, conn);
            conn.commit();
            return invoiceID;

        } catch (Exception e) {
            throw new RuntimeException("Thanh toán dịch vụ thất bại", e);
        }
    }
}