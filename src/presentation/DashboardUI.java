package presentation;

import business.DashboardBUS;
import model.DashboardStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public class DashboardUI extends JPanel {

    private JLabel lbMembers, lbActive, lbRevenue, lbInvoices, lbNewMembers;
    private DashboardStats stats;

    private final DashboardBUS bus = new DashboardBUS();

    public DashboardUI() {
        setLayout(new BorderLayout(18,18));
        setBackground(new Color(240,242,245));
        setBorder(new EmptyBorder(20,20,20,20));

        add(createCards(), BorderLayout.NORTH);
        add(createChartPanel(), BorderLayout.CENTER);

        loadData();
    }

    /* ========== CARDS ========== */

    private JPanel createCards() {
        JPanel p = new JPanel(new GridLayout(2,3,15,15));
        p.setOpaque(false);

        lbMembers     = addCard(p,"TOTAL MEMBERS",new Color(66,133,244));
        lbActive      = addCard(p,"ACTIVE MEMBERS",new Color(52,168,83));
        lbRevenue     = addCard(p,"TODAY REVENUE",new Color(251,188,5));
        lbInvoices    = addCard(p,"TODAY INVOICES",new Color(219,68,55));
        lbNewMembers  = addCard(p,"NEW MEMBERS TODAY",new Color(15,157,88));

        return p;
    }

    private JLabel addCard(JPanel parent,String title,Color c) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220,220,220)));

        JLabel t = new JLabel(title);
        t.setBorder(new EmptyBorder(10,12,0,12));
        t.setForeground(Color.GRAY);

        JLabel v = new JLabel("0");
        v.setFont(new Font("Segoe UI",Font.BOLD,26));
        v.setForeground(c);
        v.setBorder(new EmptyBorder(8,12,12,12));

        card.add(t,BorderLayout.NORTH);
        card.add(v,BorderLayout.CENTER);
        parent.add(card);
        return v;
    }

    /* ========== CHART ========== */

    private JPanel createChartPanel() {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g);
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(220,220,220)));
        return panel;
    }

    private void drawChart(Graphics g) {
        if (stats == null) return;

        Graphics2D g2=(Graphics2D)g;
        g2.setFont(new Font("Segoe UI",Font.BOLD,15));
        g2.drawString("Revenue – Last 7 Days",20,30);

        Map<LocalDate,Double> map = stats.getRevenueLast7Days();
        if(map.isEmpty()) return;

        int x=50, base=getHeight()-60;
        int w=30, maxH=200;

        double max=map.values().stream().mapToDouble(v->v).max().orElse(1);

        for(Double v : map.values()){
            int h=(int)(v/max*maxH);
            g2.setColor(new Color(66,133,244));
            g2.fillRoundRect(x,base-h,w,h,8,8);
            x+=w+25;
        }
    }

    /* ========== LOAD ========== */

    private void loadData() {
        stats = bus.loadDashboard();

        NumberFormat money =
                NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

        lbMembers.setText(String.valueOf(stats.getTotalMembers()));
        lbActive.setText(String.valueOf(stats.getActiveMembers()));
        lbRevenue.setText(money.format(stats.getTodayRevenue()));
        lbInvoices.setText(String.valueOf(stats.getTodayInvoices()));
        lbNewMembers.setText(String.valueOf(stats.getNewMembersToday()));

        repaint();
    }
}