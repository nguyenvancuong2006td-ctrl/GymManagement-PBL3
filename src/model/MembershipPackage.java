package model;

import java.util.Objects;

public class MembershipPackage {
    private int packageID;
    private String packageName;
    private int duration;
    private double price;
    private String durationType;

    public MembershipPackage() {}

    public MembershipPackage(int packageID, String packageName, int duration, double price, String durationType) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.duration = duration;
        this.price = price;
        this.durationType = durationType;
    }

    public int getPackageID() { return packageID; }
    public void setPackageID(int packageID) { this.packageID = packageID; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof MembershipPackage) &&
                ((MembershipPackage)o).packageID == packageID;
    }


    @Override
    public int hashCode() {
        return Objects.hash(packageID);
    }


    @Override
    public String toString() {
        if ("DAY".equalsIgnoreCase(durationType)) {
            return packageName + " (" + duration + " ngày)";
        }
        return packageName + " (" + duration + " tháng)";
    }

}