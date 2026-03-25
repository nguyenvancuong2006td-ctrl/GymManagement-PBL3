package model;

import java.util.Objects;

public class InvoiceDetail {
    private int invoiceDetailID;
    private int invoiceID;
    private int productID;
    private int quantity;
    private double price;

    public InvoiceDetail() {}

    public InvoiceDetail(int invoiceDetailID, int invoiceID,
                         int productID, int quantity, double price) {
        this.invoiceDetailID = invoiceDetailID;
        this.invoiceID = invoiceID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }

    public int getInvoiceDetailID() { return invoiceDetailID; }
    public void setInvoiceDetailID(int invoiceDetailID) { this.invoiceDetailID = invoiceDetailID; }

    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Detail{" + invoiceDetailID + ", qty=" + quantity + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof InvoiceDetail) &&
                ((InvoiceDetail)o).invoiceDetailID == invoiceDetailID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceDetailID);
    }
}