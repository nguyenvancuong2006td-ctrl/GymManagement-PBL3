package model;

import java.time.LocalDate;
import java.util.Objects;

public class Payment {
    private int paymentID;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String status;
    private int memberID;
    private int packageID;

    public Payment() {}

    public Payment(int paymentID, double amount, LocalDate paymentDate,
                   String paymentMethod, String status, int memberID, int packageID) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.memberID = memberID;
        this.packageID = packageID;
    }

    public int getPaymentID() { return paymentID; }
    public void setPaymentID(int paymentID) { this.paymentID = paymentID; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMemberID() { return memberID; }
    public void setMemberID(int memberID) { this.memberID = memberID; }

    public int getPackageID() { return packageID; }
    public void setPackageID(int packageID) { this.packageID = packageID; }

    @Override
    public String toString() {
        return "Payment{" + paymentID + ", " + amount + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Payment) &&
                ((Payment)o).paymentID == paymentID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentID);
    }
}