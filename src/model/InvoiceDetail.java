package model;

import java.math.BigDecimal;
import java.util.Objects;

public class InvoiceDetail {

    private int invoiceDetailID;
    private int invoiceID;
    private String itemType;
    private int itemID;
    private int quantity;
    private BigDecimal price;
    private String itemName;

    public InvoiceDetail() {
    }

    // ✅ CONSTRUCTOR CHUẨN MỚI
    public InvoiceDetail(int invoiceID,
                         String itemType,
                         int itemID,
                         int quantity,
                         BigDecimal price) {
        this.invoiceID = invoiceID;
        this.itemType = itemType;
        this.itemID = itemID;
        this.quantity = quantity;
        this.price = price;
    }

    /* ===== GETTER / SETTER ===== */

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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
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


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    /* ===== OBJECT METHODS ===== */

    @Override
    public String toString() {
        return "InvoiceDetail{" +
                "invoiceDetailID=" + invoiceDetailID +
                ", invoiceID=" + invoiceID +
                ", itemType='" + itemType + '\'' +
                ", itemID=" + itemID +
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