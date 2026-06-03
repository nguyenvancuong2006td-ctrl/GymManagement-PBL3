package business;

import data.DashboardDAO;
import model.DashboardStats;
import model.Permission;

public class DashboardBUS {

    private final DashboardDAO dao = new DashboardDAO();

    public DashboardStats loadDashboard() {
        try {
            AuthorizationService.check(Permission.DASHBOARD_VIEW);

            DashboardStats s = new DashboardStats();

            s.setTotalMembers(dao.getTotalMembers());
            s.setActiveMembers(dao.getActiveMembers());
            s.setTotalTrainers(dao.getTotalTrainers());

            s.setRevenueByMonth(dao.getRevenueByMonth());

            return s;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load dashboard", e);
        }
    }
}