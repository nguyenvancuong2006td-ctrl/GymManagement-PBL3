package presentation;

import business.MemberPTBUS;
import business.WorkoutScheduleBUS;
import model.MemberPTItem;
import model.WorkoutSchedule;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import util.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkoutScheduleUI extends JPanel {

    /* ================= STYLE ================= */
    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);

    /* ================= FORM ================= */
    private DatePicker dpDate;
    private TimePicker tpStart;
    private TimePicker tpEnd;
    private JComboBox<MemberPTItem> cboMemberPT;

    /* ================= TABLE ================= */
    private JTable table;
    private DefaultTableModel model;

    /* ================= BUS ================= */
    private final MemberPTBUS memberPTBUS = new MemberPTBUS();
    private final WorkoutScheduleBUS scheduleBUS = new WorkoutScheduleBUS();

    public WorkoutScheduleUI() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("QUẢN LÝ LỊCH TẬP PT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel form = buildFormPanel();
        JPanel tablePanel = buildTablePanel();

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                form,
                tablePanel
        );

        split.setDividerLocation(250);
        add(split, BorderLayout.CENTER);

        loadMemberPT();
        loadTable();
    }

    /* ================= FORM ================= */

    private JPanel buildFormPanel() {

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Nhập lịch tập"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        dpDate = new DatePicker();
        tpStart = new TimePicker();
        tpEnd = new TimePicker();

        cboMemberPT = new JComboBox<>();

        int y = 0;
        addRow(p, g, y++, "Ngày", dpDate);
        addRow(p, g, y++, "Bắt đầu", tpStart);
        addRow(p, g, y++, "Kết thúc", tpEnd);
        addRow(p, g, y++, "Hội viên", cboMemberPT);

        JButton btnAdd = new RoundedButton("THÊM", new Color(46, 204, 113));
        JButton btnUpdate = new RoundedButton("CẬP NHẬT", new Color(52, 152, 219));
        JButton btnDelete = new RoundedButton("XÓA", new Color(231, 76, 60));
        JButton btnClear = new RoundedButton("CLEAR", new Color(127, 140, 141));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 1;
        g.gridy = y;
        p.add(btnPanel, g);

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        return p;
    }

    /* ================= TABLE ================= */

    private JPanel buildTablePanel() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Danh sách lịch"));
        p.setBackground(Color.WHITE);

        model = new DefaultTableModel(
                new String[]{"ID", "Ngày", "Bắt đầu", "Kết thúc", "Hội viên", "PT", "Trạng thái"},
                0
        );

        table = new JTable(model);
        table.setRowHeight(25);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scroll = new JScrollPane(table);
        p.add(scroll, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = table.getSelectedRow();
                if (r >= 0) loadSelected(r);
            }
        });

        return p;
    }

    /* ================= LOAD ================= */

    private void loadTable() {
        model.setRowCount(0);
        for (Object[] r : scheduleBUS.loadTable()) {
            model.addRow(r);
        }
    }

    private void loadMemberPT() {
        cboMemberPT.removeAllItems();
        for (MemberPTItem i : memberPTBUS.getActiveMemberPTItems()) {
            cboMemberPT.addItem(i);
        }
    }

    /* ================= SELECT ROW ================= */

    private void loadSelected(int row) {

        try {
            // ===== DATE =====
            Object dateObj = model.getValueAt(row, 1);
            if (dateObj != null) {
                LocalDate date = null;

                if (dateObj instanceof LocalDate) {
                    date = (LocalDate) dateObj;
                } else {
                    date = LocalDate.parse(dateObj.toString());
                }

                dpDate.setDate(date);
            }

            // ===== START TIME =====
            Object startObj = model.getValueAt(row, 2);
            if (startObj != null) {
                LocalTime start = LocalTime.parse(startObj.toString());
                tpStart.setTime(start);
            }

            // ===== END TIME =====
            Object endObj = model.getValueAt(row, 3);
            if (endObj != null) {
                LocalTime end = LocalTime.parse(endObj.toString());
                tpEnd.setTime(end);
            }

            // ===== MEMBER PT =====
            String memberName = model.getValueAt(row, 4).toString();

            for (int i = 0; i < cboMemberPT.getItemCount(); i++) {
                MemberPTItem item = cboMemberPT.getItemAt(i);

                if (item.toString().equals(memberName)) {
                    cboMemberPT.setSelectedIndex(i);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không load được dữ liệu lên form");
        }
    }

    /* ================= ADD ================= */

    private void add() {

        MemberPTItem item = (MemberPTItem) cboMemberPT.getSelectedItem();
        if (item == null) return;

        LocalDate date = dpDate.getDate();
        LocalTime start = tpStart.getTime();
        LocalTime end = tpEnd.getTime();

        if (date == null || start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Thiếu dữ liệu");
            return;
        }

        if (!end.isAfter(start)) {
            JOptionPane.showMessageDialog(this, "Giờ không hợp lệ");
            return;
        }

        WorkoutSchedule ws = new WorkoutSchedule();
        ws.setDate(date);
        ws.setStartTime(start);
        ws.setEndTime(end);
        ws.setMemberPTID(item.getMemberPTID());
        ws.setTrainerID(item.getTrainerID());

        scheduleBUS.register(ws);
        loadTable();
    }

    /* ================= UPDATE ================= */

    private void update() {

        int r = table.getSelectedRow();
        if (r < 0) return;

        int id = Integer.parseInt(model.getValueAt(r, 0).toString());

        MemberPTItem item = (MemberPTItem) cboMemberPT.getSelectedItem();
        if (item == null) return;

        WorkoutSchedule ws = new WorkoutSchedule();
        ws.setScheduleID(id);
        ws.setDate(dpDate.getDate());
        ws.setStartTime(tpStart.getTime());
        ws.setEndTime(tpEnd.getTime());
        ws.setMemberPTID(item.getMemberPTID());
        ws.setTrainerID(item.getTrainerID());
        ws.setStatus("BOOKED");

        scheduleBUS.update(ws);
        loadTable();
    }

    /* ================= DELETE ================= */

    private void delete() {

        int r = table.getSelectedRow();
        if (r < 0) return;

        int id = Integer.parseInt(model.getValueAt(r, 0).toString());

        scheduleBUS.delete(id);
        loadTable();
        clear();
    }

    /* ================= CLEAR ================= */

    private void clear() {
        dpDate.setDate(null);
        tpStart.setTime(null);
        tpEnd.setTime(null);
        table.clearSelection();
    }

    /* ================= UTIL ================= */

    private void addRow(JPanel p, GridBagConstraints g, int y, String l, JComponent c) {
        g.gridx = 0;
        g.gridy = y;
        p.add(new JLabel(l), g);

        g.gridx = 1;
        p.add(c, g);
    }
}