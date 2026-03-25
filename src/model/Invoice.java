package model;

import java.time.LocalDate;
import java.util.Objects;

public class Invoice {
    private int invoiceID;
    private LocalDate invoiceDate;
    private double totalAmount;
    private int staffID;

    public Invoice() {}

    public Invoice(int invoiceID, LocalDate invoiceDate, double totalAmount, int staffID) {
        this.invoiceID = invoiceID;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.staffID = staffID;
    }

    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    @Override
    public String toString() {
        return "Invoice{" + invoiceID + ", " + totalAmount + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Invoice) &&
                ((Invoice)o).invoiceID == invoiceID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceID);
    }
}