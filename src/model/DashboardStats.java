package model;

import java.util.Map;

public class DashboardStats {

    private int totalMembers;
    private int totalTrainers;
    private int activePlans;
    private Map<Integer, Double> revenueByMonth;

    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int v) { totalMembers = v; }

    public int getTotalTrainers() { return totalTrainers; }
    public void setTotalTrainers(int v) { totalTrainers = v; }

    public int getActivePlans() { return activePlans; }
    public void setActivePlans(int v) { activePlans = v; }

    public Map<Integer, Double> getRevenueByMonth() { return revenueByMonth; }
    public void setRevenueByMonth(Map<Integer, Double> v) { revenueByMonth = v; }
}