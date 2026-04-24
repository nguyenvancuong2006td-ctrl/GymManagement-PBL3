package presentation;

import business.TrainerBUS;
import model.Trainer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SelectTrainerDialog extends JDialog {

    private JTable table;
    private int selectedTrainerID = -1;

    public SelectTrainerDialog(Window owner) {
        super(owner, "Chọn huấn luyện viên PT", ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        TrainerBUS bus = new TrainerBUS();
        List<Trainer> list = bus.getAll();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Tên huấn luyện viên"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (Trainer t : list) {
            model.addRow(new Object[]{
                    t.getTrainerID(),
                    t.getFullName()
            });
        }

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnSelect = new JButton("Chọn");
        btnSelect.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn huấn luyện viên");
                return;
            }
            selectedTrainerID = (int) table.getValueAt(r, 0);
            dispose();
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnSelect, BorderLayout.SOUTH);
    }

    public boolean isSelected() {
        return selectedTrainerID != -1;
    }

    public int getSelectedTrainerID() {
        return selectedTrainerID;
    }
}
