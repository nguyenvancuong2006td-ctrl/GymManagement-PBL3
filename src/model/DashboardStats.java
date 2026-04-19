package model;

import java.time.LocalDate;
import java.util.Map;

public class DashboardStats {

    // Tổng quan
    private int totalMembers;
    private int activeMembers;
    private int totalTrainers;

    // Hôm nay
    private int todayInvoices;
    private double todayRevenue;
    private int newMembersToday;

    // Biểu đồ nhẹ
    private Map<LocalDate, Double> revenueLast7Days;

    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int v) { totalMembers = v; }

    public int getActiveMembers() { return activeMembers; }
    public void setActiveMembers(int v) { activeMembers = v; }

    public int getTotalTrainers() { return totalTrainers; }
    public void setTotalTrainers(int v) { totalTrainers = v; }

    public int getTodayInvoices() { return todayInvoices; }
    public void setTodayInvoices(int v) { todayInvoices = v; }

    public double getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(double v) { todayRevenue = v; }

    public int getNewMembersToday() { return newMembersToday; }
    public void setNewMembersToday(int v) { newMembersToday = v; }

    public Map<LocalDate, Double> getRevenueLast7Days() { return revenueLast7Days; }
    public void setRevenueLast7Days(Map<LocalDate, Double> v) { revenueLast7Days = v; }
}