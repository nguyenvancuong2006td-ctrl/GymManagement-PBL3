package presentation;

import business.DashboardBUS;
import model.DashboardStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

public class DashboardUI extends JPanel {

    private JLabel lbMembersValue;
    private JLabel lbTrainersValue;
    private JLabel lbPlansValue;

    private final DashboardBUS dashboardBUS = new DashboardBUS();
    private DashboardStats stats;

    public DashboardUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createStatsPanel(), BorderLayout.NORTH);
        add(createChartPanel(), BorderLayout.CENTER);

        loadData();
    }

    /* ===================== TOP STATS ===================== */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);

        panel.add(createStatCard("Total Members", lbMembersValue = new JLabel("0"),
                new Color(66, 133, 244)));
        panel.add(createStatCard("Total Trainers", lbTrainersValue = new JLabel("0"),
                new Color(251, 140, 0)));
        panel.add(createStatCard("Active Plans", lbPlansValue = new JLabel("0"),
                new Color(46, 204, 113)));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        card.setPreferredSize(new Dimension(200, 110));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbTitle.setForeground(new Color(120, 120, 120));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(valueLabel, BorderLayout.CENTER);
        center.setBorder(new EmptyBorder(5, 0, 0, 0));

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        card.add(lbTitle, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);

        return card;
    }

    /* ===================== CHART ===================== */
    private JPanel createChartPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRevenueChart(g);
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 20, 20)
        ));
        return panel;
    }

    private void drawRevenueChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(Color.BLACK);
        g2.drawString("Revenue by Month", 20, 30);

        if (stats == null || stats.getRevenueByMonth() == null) return;

        Map<Integer, Double> data = stats.getRevenueByMonth();

        int chartX = 60;
        int chartY = getHeight() - 90;
        int barWidth = 30;
        int maxHeight = 240;

        double max = data.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1);

        int x = chartX;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // ✅ LUÔN VẼ 12 THÁNG – KỂ CẢ KHI = 0
        for (int month = 1; month <= 12; month++) {

            double value = data.getOrDefault(month, 0.0);
            int barHeight = (int) ((value / max) * maxHeight);

            // vẽ cột (tối thiểu cao 2px để nhìn thấy)
            g2.setColor(new Color(66, 133, 244, value == 0 ? 90 : 255));
            g2.fillRoundRect(
                    x,
                    chartY - Math.max(barHeight, 2),
                    barWidth,
                    Math.max(barHeight, 2),
                    8,
                    8
            );

            // text tháng
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("M" + month, x + 4, chartY + 15);

            x += barWidth + 18;
        }

        // ghi chú
        boolean allZero = data.values().stream().allMatch(v -> v == 0.0);
        if (allZero) {
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            g2.setColor(Color.GRAY);
            g2.drawString(
                    "No revenue data yet",
                    getWidth() / 2 - 70,
                    chartY + 40
            );
        }
    }


    /* ===================== LOAD DATA ===================== */
    private void loadData() {
        try {
            stats = dashboardBUS.loadDashboard();
            lbMembersValue.setText(String.valueOf(stats.getTotalMembers()));
            lbTrainersValue.setText(String.valueOf(stats.getTotalTrainers()));
            lbPlansValue.setText(String.valueOf(stats.getActivePlans()));
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot load dashboard data\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}