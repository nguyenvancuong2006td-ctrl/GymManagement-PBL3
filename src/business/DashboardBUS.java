package business;

import data.DashboardDAO;
import model.DashboardStats;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardBUS {

    private final DashboardDAO dao = new DashboardDAO();

    public DashboardStats loadDashboard() throws Exception {

        DashboardStats d = new DashboardStats();

        // ✅ luôn khởi tạo trước 12 tháng
        Map<Integer, Double> revenue = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            revenue.put(i, 0.0);
        }

        // ✅ gộp dữ liệu từ DB (nếu có)
        Map<Integer, Double> dbRevenue = dao.getMonthlyRevenue();
        if (dbRevenue != null) {
            for (Map.Entry<Integer, Double> e : dbRevenue.entrySet()) {
                revenue.put(e.getKey(), e.getValue());
            }
        }

        d.setRevenueByMonth(revenue);
        d.setTotalMembers(dao.getTotalMembers());
        d.setTotalTrainers(dao.getTotalTrainers());
        d.setActivePlans(dao.getActivePlans());

        return d;
    }
}