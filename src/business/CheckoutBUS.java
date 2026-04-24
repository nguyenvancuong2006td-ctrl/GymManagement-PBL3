package business;

import data.InvoiceDAO;
import data.InvoiceDetailDAO;
import data.PaymentDAO;
import data.ProductDAO;
import model.*;
import util.DBConnection;

import java.sql.Connection;

public class CheckoutBUS {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public int checkout(
            CartBUS cartBUS,
            int staffID,
            Member member,
            PaymentMethod method
    ) {

        /* ===== VALIDATE NGHIỆP VỤ ===== */
        if (staffID <= 0) {
            throw new IllegalArgumentException("StaffID không hợp lệ");
        }

        if (member == null) {
            throw new IllegalArgumentException("Hội viên không hợp lệ");
        }

        if (cartBUS == null || cartBUS.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
        }

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            /* ===== 1. TẠO INVOICE ===== */
            Invoice invoice = new Invoice(
                    cartBUS.getTotalAmount(),
                    staffID,
                    member.getMemberID()
            );

            int invoiceID = invoiceDAO.insert(invoice, conn);

            /* ===== 2. INVOICE DETAIL + TRỪ KHO ===== */
            cartBUS.getItems().forEach((product, qty) -> {
                try {
                    // InvoiceDetail dùng itemType + itemID
                    InvoiceDetail d = new InvoiceDetail(
                            invoiceID,
                            "PRODUCT",                      // itemType
                            product.getProductID(),          // itemID
                            qty,
                            product.getPrice()
                    );
                    detailDAO.insert(d, conn);

                    //  TRỪ TỒN KHO PRODUCT
                    productDAO.decreaseStock(
                            product.getProductID(),
                            qty,
                            conn
                    );

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            /* ===== 3. PAYMENT ===== */
            Payment payment = new Payment();
            payment.setAmount(cartBUS.getTotalAmount());
            payment.setPaymentMethod(method.name());
            payment.setStatus("Completed");
            payment.setMemberID(member.getMemberID());
            payment.setStaffID(staffID);
            payment.setPackageID(null);
            payment.setInvoiceID(invoiceID);

            paymentDAO.insert(payment, conn);

            conn.commit();
            cartBUS.clear();

            return invoiceID;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Thanh toán thất bại", e);
        }
    }
}