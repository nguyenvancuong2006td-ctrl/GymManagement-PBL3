package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {

    private int paymentID;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String status;
    private int memberID;
    private int staffID;
    private Integer packageID;
    private int invoiceID;

    public Payment() {
    }

    // Constructor dùng để INSERT (KHÔNG có paymentID)
    public Payment(BigDecimal amount, String paymentMethod, String status,
                   int memberID, int staffID,Integer packageID, int invoiceID) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.memberID = memberID;
        this.staffID = staffID;
        this.packageID = packageID;
        this.invoiceID = invoiceID;
    }

    // ===== Getter & Setter =====
    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public Integer getPackageID() {
        return packageID;
    }

    public void setPackageID(Integer packageID) {
        this.packageID = packageID;
    }


    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }


    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", amount=" + amount +
                ", memberID=" + memberID +
                ", staffID=" + staffID +
                ", packageID=" + packageID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return paymentID != 0 && paymentID == payment.paymentID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentID);
    }
}