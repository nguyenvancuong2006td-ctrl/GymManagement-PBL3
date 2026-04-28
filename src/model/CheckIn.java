package model;

import java.time.LocalDateTime;

public class CheckIn {

    private int checkInID;
    private String phoneNumber;
    private LocalDateTime checkInTime;

    // ===== CONSTRUCTOR =====
    public CheckIn() {}

    public CheckIn(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.checkInTime = LocalDateTime.now();
    }

    // ===== GETTERS & SETTERS =====
    public int getCheckInID() {
        return checkInID;
    }

    public void setCheckInID(int checkInID) {
        this.checkInID = checkInID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
}
