package model;

import java.math.BigDecimal;
import java.util.Objects;

public class InvoiceDetail {

    private int invoiceDetailID;
    private int invoiceID;
    private int productID;
    private int quantity;
    private BigDecimal price;

    public InvoiceDetail() {
    }

    public InvoiceDetail(int invoiceID, int productID,
                         int quantity, BigDecimal price) {
        this.invoiceID = invoiceID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }

    // ===== Getter & Setter =====

    public int getInvoiceDetailID() {
        return invoiceDetailID;
    }

    public void setInvoiceDetailID(int invoiceDetailID) {
        this.invoiceDetailID = invoiceDetailID;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "InvoiceDetail{" +
                "invoiceDetailID=" + invoiceDetailID +
                ", invoiceID=" + invoiceID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvoiceDetail)) return false;
        InvoiceDetail that = (InvoiceDetail) o;
        return invoiceDetailID > 0 &&
                invoiceDetailID == that.invoiceDetailID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceDetailID);
    }
}