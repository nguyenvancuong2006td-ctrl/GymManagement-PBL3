package presentation;

import business.MemberPTBUS;
import business.WorkoutScheduleBUS;
import model.MemberPTItem;
import model.WorkoutSchedule;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class WorkoutScheduleUI extends JPanel {

    /* ================= STYLE ================= */
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Color BG_APP     = new Color(240, 242, 245);

    /* ================= FORM ================= */
    private final JSpinner spDate;
    private final JSpinner spStart;
    private final JSpinner spEnd;
    private final JComboBox<MemberPTItem> cboMemberPT;

    /* ================= TABLE ================= */
    private final JTable tblSchedule;
    private final DefaultTableModel tblModel;

    private final MemberPTBUS memberPTBUS = new MemberPTBUS();
    private final WorkoutScheduleBUS scheduleBUS = new WorkoutScheduleBUS();

    public WorkoutScheduleUI() {

        setLayout(new BorderLayout(15, 15));
        setBackground(BG_APP);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        applyGlobalStyle();

        /* ================= TITLE ================= */
        JLabel title = new JLabel("QUẢN LÝ LỊCH TẬP PT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        /* ================= FORM CARD ================= */
        JPanel formCard = createCard("Đăng ký lịch mới");
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 12, 10, 12);
        g.fill = GridBagConstraints.HORIZONTAL;

        spDate = createDateSpinner("yyyy-MM-dd");
        spStart = createDateSpinner("HH:mm");
        spEnd = createDateSpinner("HH:mm");

        cboMemberPT = new JComboBox<>();
        cboMemberPT.setPreferredSize(new Dimension(220, 28));
        loadMemberPT();

        int y = 0;

        addFormRow(formCard, g, y++, "Ngày tập", spDate);
        addFormRow(formCard, g, y++, "Giờ bắt đầu", spStart);
        addFormRow(formCard, g, y++, "Giờ kết thúc", spEnd);
        addFormRow(formCard, g, y++, "Hội viên (PT)", cboMemberPT);

        JButton btnAdd = primaryButton("ĐĂNG KÝ LỊCH");
        btnAdd.addActionListener(e -> addSchedule());

        g.gridx = 1;
        g.gridy = y;
        g.anchor = GridBagConstraints.EAST;
        formCard.add(btnAdd, g);

        /* ================= TABLE CARD ================= */
        JPanel tableCard = createCard("Danh sách lịch tập");
        tableCard.setLayout(new BorderLayout(8, 8));

        tblModel = new DefaultTableModel(
                new String[]{"Ngày", "Bắt đầu", "Kết thúc", "Hội viên", "PT", "Trạng thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSchedule = new JTable(tblModel);
        tblSchedule.setRowHeight(28);
        styleTable(tblSchedule);

        loadTable();

        tableCard.add(new JScrollPane(tblSchedule), BorderLayout.CENTER);

        /* ================= CENTER ================= */
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(formCard, BorderLayout.WEST);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    /* ================= UI STYLE ================= */

    private void applyGlobalStyle() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("TextField.font", FONT_NORMAL);
        UIManager.put("Spinner.font", FONT_NORMAL);
        UIManager.put("ComboBox.font", FONT_NORMAL);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("TableHeader.font", FONT_BOLD);
    }

    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);

        TitledBorder t = BorderFactory.createTitledBorder(title);
        t.setTitleFont(FONT_BOLD);
        t.setTitleColor(new Color(60, 60, 60));

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        p.setBorder(t);
        return p;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(52, 120, 208));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(160, 32));
        return b;
    }

    private void addFormRow(JPanel panel, GridBagConstraints g,
                            int y, String label, JComponent field) {

        g.gridy = y;
        g.gridx = 0;
        g.weightx = 0;
        panel.add(new JLabel(label + ":"), g);

        g.gridx = 1;
        g.weightx = 1;
        panel.add(field, g);
    }

    private JSpinner createDateSpinner(String format) {
        JSpinner s = new JSpinner(new SpinnerDateModel());
        s.setEditor(new JSpinner.DateEditor(s, format));
        s.setPreferredSize(new Dimension(220, 28));
        return s;
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setBackground(new Color(245, 246, 248));
        table.getTableHeader().setForeground(new Color(60, 60, 60));
        table.setGridColor(new Color(230, 230, 230));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(248, 248, 248));
                }
                return c;
            }
        });
    }

    /* ================= DATA ================= */

    private void loadMemberPT() {
        cboMemberPT.removeAllItems();
        memberPTBUS.getActiveMemberPTItems().forEach(cboMemberPT::addItem);
    }

    private void loadTable() {
        tblModel.setRowCount(0);
        for (Object[] row : scheduleBUS.loadTable()) {
            tblModel.addRow(row);
        }
    }

    /* ================= ACTION (KHÔNG ĐỔI) ================= */

    private void addSchedule() {

        MemberPTItem item = (MemberPTItem) cboMemberPT.getSelectedItem();
        if (item == null) return;

        LocalDate date =
                new java.sql.Date(((Date) spDate.getValue()).getTime())
                        .toLocalDate();

        LocalTime start =
                ((Date) spStart.getValue()).toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalTime();

        LocalTime end =
                ((Date) spEnd.getValue()).toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalTime();

        if (!end.isAfter(start)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Giờ kết thúc phải sau giờ bắt đầu",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        WorkoutSchedule ws = new WorkoutSchedule();
        ws.setDate(date);
        ws.setStartTime(start);
        ws.setEndTime(end);
        ws.setMemberPTID(item.getMemberPTID());
        ws.setTrainerID(item.getTrainerID());

        try {
            scheduleBUS.register(ws);
            loadTable();
            JOptionPane.showMessageDialog(this, "Đăng ký lịch thành công");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}