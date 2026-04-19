package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private int productID;
    private String productName;
    private String category;
    private BigDecimal price;
    private int quantityInitial;
    private int quantityInStock;
    private String description;
    private String imagePath;

    public Product() {}

    public Product(int productID, String productName, String category,
                   BigDecimal price, int quantityInitial, int quantityInStock,
                   String description, String imagePath) {
        this.productID = productID;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantityInitial = quantityInitial;
        this.quantityInStock = quantityInStock;
        this.description = description;
        this.imagePath = imagePath;
    }

    // ===== GET / SET =====
    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantityInitial() { return quantityInitial; }
    public void setQuantityInitial(int quantityInitial) { this.quantityInitial = quantityInitial; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // logic
    public int getQuantitySold() {
        return quantityInitial - quantityInStock;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Product && ((Product) o).productID == productID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID);
    }

    @Override
    public String toString() {
        return productName;
    }
}
