package presentation;

import business.DashboardBUS;
import model.DashboardStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class DashboardUI extends JPanel {

    private JLabel lbMembers, lbActive, lbRevenue, lbTrainers;
    private DashboardStats stats;

    private final DashboardBUS bus = new DashboardBUS();

    public DashboardUI() {

        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createCards(), BorderLayout.NORTH);
        add(createChartPanel(), BorderLayout.CENTER);

        loadData();
    }

    /* ================= CARDS ================= */

    private JPanel createCards() {

        JPanel p = new JPanel(new GridLayout(1, 4, 15, 15));
        p.setOpaque(false);

        lbMembers  = addCard(p, "TOTAL MEMBERS", new Color(66, 133, 244));
        lbActive   = addCard(p, "ACTIVE MEMBERS", new Color(52, 168, 83));
        lbRevenue  = addCard(p, "TOTAL REVENUE (YEAR)", new Color(251, 188, 5));
        lbTrainers = addCard(p, "TOTAL TRAINERS", new Color(219, 68, 55));

        return p;
    }

    private JLabel addCard(JPanel parent, String title, Color color) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel t = new JLabel(title);
        t.setForeground(new Color(120, 120, 120));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel v = new JLabel("0", SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 26));
        v.setForeground(color);

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        parent.add(card);
        return v;
    }

    /* ================= CHART ================= */

    private JPanel createChartPanel() {

        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, Color.WHITE,
                        0, getHeight(), new Color(245, 247, 250)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                drawChart(g2);
            }
        };

        panel.setPreferredSize(new Dimension(800, 400)); // 🔥 FIX KHÔNG MẤT THÁNG

        return panel;
    }

    private void drawChart(Graphics2D g2) {

        if (stats == null) return;


        Rectangle bounds = g2.getClipBounds();
        int width = bounds.width;
        int height = bounds.height;


        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(60, 60, 60));
        g2.drawString("Revenue by Month", 20, 30);

        Map<Integer, Double> rawMap = stats.getRevenueByMonth();
        if (rawMap == null || rawMap.isEmpty()) return;

        // ===== SORT MONTH =====
        List<Integer> months = new ArrayList<>(rawMap.keySet());
        Collections.sort(months);

        int base = height - 60;
        int x = 60;

        int barWidth = 40;
        int gap = 25;
        int maxHeight = 220;

        double max = rawMap.values()
                .stream()
                .mapToDouble(v -> v)
                .max()
                .orElse(1);

        // ===== TRỤC X =====
        g2.setColor(new Color(180, 180, 180));
        g2.drawLine(40, base, width - 40, base);

        // ===== DRAW BAR =====
        for (Integer month : months) {

            double value = rawMap.get(month);

            int h = (int) (value / max * maxHeight);

            // BAR
            g2.setColor(new Color(66, 133, 244));
            g2.fillRoundRect(x, base - h, barWidth, h, 12, 12);

            // VALUE (đẹp + dễ đọc)
            String text = String.format("%.0f", value);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

            int textWidth = g2.getFontMetrics().stringWidth(text);

            int tx = x + (barWidth - textWidth) / 2;
            int ty = base - h - 6;

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(tx - 4, ty - 12, textWidth + 8, 16, 6, 6);

            g2.setColor(new Color(40, 40, 40));
            g2.drawString(text, tx, ty);

            // MONTH (FIX CHẮC HIỆN)
            String m = "T" + month;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(80, 80, 80));

            int mw = g2.getFontMetrics().stringWidth(m);

            g2.drawString(m,
                    x + (barWidth - mw) / 2,
                    base + 25);

            x += barWidth + gap;
        }
    }

    /* ================= LOAD ================= */

    private void loadData() {

        stats = bus.loadDashboard();

        NumberFormat money =
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        lbMembers.setText(String.valueOf(stats.getTotalMembers()));
        lbActive.setText(String.valueOf(stats.getActiveMembers()));
        lbTrainers.setText(String.valueOf(stats.getTotalTrainers()));

        double totalRevenue = stats.getRevenueByMonth()
                .values()
                .stream()
                .mapToDouble(v -> v)
                .sum();

        lbRevenue.setText(money.format(totalRevenue));

        repaint();
    }
}