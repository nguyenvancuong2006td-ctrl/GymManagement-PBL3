package business;

import data.*;
import model.Invoice;
import model.InvoiceDetail;
import model.Payment;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceBUS {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();

    // Các DAO cần bổ sung để UI không bị lỗi
    private final StaffDAO staffDAO = new StaffDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    /* ================= INVOICE ================= */

    public List<Invoice> getAllInvoices() {
        try {
            return invoiceDAO.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Không tải được danh sách hóa đơn", e);
        }
    }

    public Invoice getInvoiceByID(int id) {
        try {
            return invoiceDAO.getByID(id);
        } catch (Exception e) {
            throw new RuntimeException("Không tìm thấy hóa đơn #" + id, e);
        }
    }

    /* ================= INVOICE DETAIL ================= */

    public List<InvoiceDetail> getInvoiceDetails(int invoiceID) {
        try {
            return detailDAO.getByInvoiceID(invoiceID);
        } catch (Exception e) {
            throw new RuntimeException("Không tải được chi tiết hóa đơn", e);
        }
    }

    /* ================= SUPPORT CHO UI ================= */

    public String getInvoiceDate(int invoiceID) {
        return getInvoiceByID(invoiceID)
                .getInvoiceDate()
                .toString();
    }

    public String getStaffName(int invoiceID) {
        int staffID = getInvoiceByID(invoiceID).getStaffID();
        return staffDAO.getNameByID(staffID);
    }

    public String getMemberName(int invoiceID) {
        int memberID = getInvoiceByID(invoiceID).getMemberID();
        return memberDAO.getNameByID(memberID);
    }

    public String getProductName(int productID) {
        return productDAO.getNameByID(productID);
    }


    public List<Payment> getPayments(int invoiceID) {
        try {
            return paymentDAO.getByInvoiceID(invoiceID);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể lấy danh sách thanh toán cho hóa đơn #" + invoiceID,
                    e
            );
        }
    }

    public BigDecimal getInvoiceTotal(int invoiceID) {

        try {
            return invoiceDAO.getTotalAmount(invoiceID);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể lấy tổng tiền của hóa đơn #" + invoiceID,
                    e
            );
        }

    }
}