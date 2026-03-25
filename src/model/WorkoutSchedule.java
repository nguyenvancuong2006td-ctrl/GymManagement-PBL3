package model;

import java.time.LocalDate;
import java.util.Objects;

public class WorkoutSchedule {
    private int scheduleID;
    private LocalDate date;
    private int memberID;
    private int trainerID;

    public WorkoutSchedule() {}

    public WorkoutSchedule(int scheduleID, LocalDate date, int memberID, int trainerID) {
        this.scheduleID = scheduleID;
        this.date = date;
        this.memberID = memberID;
        this.trainerID = trainerID;
    }

    public int getScheduleID() { return scheduleID; }
    public void setScheduleID(int scheduleID) { this.scheduleID = scheduleID; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getMemberID() { return memberID; }
    public void setMemberID(int memberID) { this.memberID = memberID; }

    public int getTrainerID() { return trainerID; }
    public void setTrainerID(int trainerID) { this.trainerID = trainerID; }

    @Override
    public String toString() {
        return "Schedule{" + scheduleID + ", " + date + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof WorkoutSchedule) &&
                ((WorkoutSchedule)o).scheduleID == scheduleID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleID);
    }
}