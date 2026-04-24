package model;

import java.time.LocalDate;

public class MemberPackage {

    private int memberPackageID;
    private int memberID;
    private int packageID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;


    public MemberPackage() {
    }

    public int getMemberPackageID() {
        return memberPackageID;
    }

    public void setMemberPackageID(int memberPackageID) {
        this.memberPackageID = memberPackageID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}