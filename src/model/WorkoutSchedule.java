package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class WorkoutSchedule {

    private int scheduleID;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int memberID;
    private int trainerID;
    private String status;

    // Constructor rỗng
    public WorkoutSchedule() {
    }

    // Constructor đầy đủ
    public WorkoutSchedule(int scheduleID,
                           LocalDate date,
                           LocalTime startTime,
                           LocalTime endTime,
                           int memberID,
                           int trainerID) {
        this.scheduleID = scheduleID;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memberID = memberID;
        this.trainerID = trainerID;
    }

    // ===== GETTERS & SETTERS =====

    public int getScheduleID() {return scheduleID;}
    public void setScheduleID(int scheduleID) {this.scheduleID = scheduleID;}

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public LocalTime getStartTime() {return startTime;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}

    public LocalTime getEndTime() {return endTime;}
    public void setEndTime(LocalTime endTime) {this.endTime = endTime;}

    public int getMemberID() {return memberID;}
    public void setMemberID(int memberID) {this.memberID = memberID;}

    public int getTrainerID() {return trainerID;}
    public void setTrainerID(int trainerID) {this.trainerID = trainerID;}


    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}


    // ===== equals & hashCode =====
    // So sánh theo PRIMARY KEY
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkoutSchedule)) return false;
        WorkoutSchedule that = (WorkoutSchedule) o;
        return scheduleID == that.scheduleID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleID);
    }

    // ===== toString =====
    @Override
    public String toString() {
        return "WorkoutSchedule{" +
                "scheduleID=" + scheduleID +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", memberID=" + memberID +
                ", trainerID=" + trainerID +
                '}';
    }
}