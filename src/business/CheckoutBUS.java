package business;

import data.InvoiceDAO;
import data.InvoiceDetailDAO;
import data.PaymentDAO;
import model.*;
import util.DBConnection;

import javax.swing.*;
import java.sql.Connection;

public class CheckoutBUS {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public int checkout(
            CartBUS cartBUS,
            int staffID,
            Member member,
            PaymentMethod method
    ) {
        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            // 1️⃣ Tạo Invoice
            Invoice invoice = new Invoice(
                    cartBUS.getTotalAmount(),
                    staffID,
                    member.getMemberID()
            );

            int invoiceID = invoiceDAO.insert(invoice, conn);

            // 2️⃣ Tạo InvoiceDetail
            cartBUS.getItems().forEach((product, qty) -> {
                try {
                    InvoiceDetail d = new InvoiceDetail(
                            invoiceID,
                            product.getProductID(),
                            qty,
                            product.getPrice()
                    );
                    detailDAO.insert(d, conn);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // 3️⃣ Tạo Payment (DÙNG SETTER – KHÔNG DÙNG CONSTRUCTOR)
            Payment payment = new Payment();
            payment.setAmount(cartBUS.getTotalAmount());
            payment.setPaymentMethod(method.name());
            payment.setStatus("Completed");
            payment.setMemberID(member.getMemberID());
            payment.setStaffID(staffID);
            payment.setPackageID(null);
            payment.setInvoiceID(invoiceID); // ✅ LIÊN KẾT VỚI INVOICE

            paymentDAO.insert(payment, conn);

            conn.commit();
            cartBUS.clear();

            return invoiceID;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi chi tiết:\n" + e.getMessage(),
                    "Lỗi thanh toán",
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }
    }
}