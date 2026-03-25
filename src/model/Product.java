package model;

import java.util.Objects;

public class Product {
    private int productID;
    private String productName;
    private String category;
    private double price;
    private int quantityInStock;
    private String description;
    private String status;

    public Product() {}

    public Product(int productID, String productName, String category,
                   double price, int quantityInStock, String description, String status) {
        this.productID = productID;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.description = description;
        this.status = status;
    }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Product{" + productID + ", " + productName + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Product) &&
                ((Product)o).productID == productID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID);
    }
}