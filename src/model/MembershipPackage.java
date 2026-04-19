package model;

import java.util.Objects;

public class MembershipPackage {
    private int packageID;
    private String packageName;
    private int duration;
    private double price;

    public MembershipPackage() {}

    public MembershipPackage(int packageID, String packageName, int duration, double price) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.duration = duration;
        this.price = price;
    }

    public int getPackageID() { return packageID; }
    public void setPackageID(int packageID) { this.packageID = packageID; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }


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
        return packageName + " (" + duration + " tháng)";
    }

}