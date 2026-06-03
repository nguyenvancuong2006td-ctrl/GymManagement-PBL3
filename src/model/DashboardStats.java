package model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardStats {

    // Tổng quan
    private int totalMembers;
    private int activeMembers;
    private int totalTrainers;

    private Map<Integer, Double> revenueByMonth = new LinkedHashMap<>();

    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int v) { totalMembers = v; }

    public int getActiveMembers() { return activeMembers; }
    public void setActiveMembers(int v) { activeMembers = v; }

    public int getTotalTrainers() { return totalTrainers; }
    public void setTotalTrainers(int v) { totalTrainers = v; }

    public Map<Integer, Double> getRevenueByMonth() {
        return revenueByMonth;
    }

    public void setRevenueByMonth(Map<Integer, Double> revenueByMonth) {
        this.revenueByMonth = revenueByMonth;
    }

    public double getTotalRevenue() {
        return revenueByMonth.values()
                .stream()
                .mapToDouble(v -> v)
                .sum();
    }

}