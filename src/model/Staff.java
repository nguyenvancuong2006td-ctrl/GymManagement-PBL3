package model;

import java.time.LocalDate;

public class Staff {
    private int staffID;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private double salary;
    private LocalDate hireDate;
    private Account account;

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }


    public Account getAccount() {return account;}
    public void setAccount(Account account) { this.account = account;}

    public int getAccountID() {return account != null ? account.getAccountID() : 0;}


    public void setAccountID(int accountID) {
        if (this.account == null) {
            this.account = new Account();
        }
        this.account.setAccountID(accountID);
    }

}
