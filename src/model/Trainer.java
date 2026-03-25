package model;

import java.time.LocalDate;
import java.util.Objects;

public class Trainer {
    private int trainerID;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private LocalDate hireDate;
    private double salary;

    public Trainer() {}

    public int getTrainerID() { return trainerID; }
    public void setTrainerID(int trainerID) { this.trainerID = trainerID; }

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

    @Override
    public String toString() {
        return "Trainer{" + trainerID + ", " + fullName + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Trainer) && ((Trainer)o).trainerID == trainerID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerID);
    }
}