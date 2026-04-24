package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Invoice {

    private int invoiceID;
    private LocalDateTime invoiceDate;
    private BigDecimal totalAmount;
    private int staffID;
    private int memberID;
    private String staffName;


    public Invoice() {
    }

    // Constructor dùng khi INSERT (KHÔNG invoiceID, KHÔNG invoiceDate)
    public Invoice(BigDecimal totalAmount, int staffID, int memberID) {
        this.totalAmount = totalAmount;
        this.staffID = staffID;
        this.memberID = memberID;
    }

    // ===== Getter / Setter =====
    public int getInvoiceID() {
        return invoiceID;
    }
    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }
    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getStaffID() {
        return staffID;
    }
    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public int getMemberID() {
        return memberID;
    }
    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }



    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID=" + invoiceID +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice invoice)) return false;
        return invoiceID > 0 && invoiceID == invoice.invoiceID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceID);
    }


}