package model;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    TRANSFER("Chuyển khoản");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}