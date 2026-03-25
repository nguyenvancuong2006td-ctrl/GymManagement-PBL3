package model;

import java.time.LocalDate;
import java.util.Objects;

public class Staff {
    private int staffID;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private LocalDate hireDate;
    private double salary;
    private int accountID;

    public Staff() {}

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }

    @Override
    public String toString() {
        return "Staff{" + staffID + ", " + fullName + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Staff) && ((Staff)o).staffID == staffID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffID);
    }
}