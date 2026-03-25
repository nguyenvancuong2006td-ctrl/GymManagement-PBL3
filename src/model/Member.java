package model;

import java.time.LocalDate;
import java.util.Objects;

public class Member {
    private int memberID;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private LocalDate joinDate;

    public Member() {}

    public int getMemberID() { return memberID; }
    public void setMemberID(int memberID) { this.memberID = memberID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    @Override
    public String toString() {
        return "Member{" + memberID + ", " + fullName + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Member) && ((Member)o).memberID == memberID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberID);
    }
}