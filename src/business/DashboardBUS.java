package business;

import data.DashboardDAO;
import model.DashboardStats;

public class DashboardBUS {

    private final DashboardDAO dao = new DashboardDAO();

    public DashboardStats loadDashboard() {
        try {
            DashboardStats s = new DashboardStats();

            s.setTotalMembers(dao.getTotalMembers());
            s.setActiveMembers(dao.getActiveMembers());
            s.setTotalTrainers(dao.getTotalTrainers());

            s.setTodayInvoices(dao.getTodayInvoices());
            s.setTodayRevenue(dao.getTodayRevenue());
            s.setNewMembersToday(dao.getNewMembersToday());

            s.setRevenueLast7Days(dao.getRevenueLast7Days());

            return s;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load dashboard", e);
        }
    }
}