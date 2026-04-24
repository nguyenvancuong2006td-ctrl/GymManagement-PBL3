package business;

import data.WorkoutScheduleDAO;
import model.WorkoutSchedule;

import java.util.List;

public class WorkoutScheduleBUS {

    private final WorkoutScheduleDAO dao = new WorkoutScheduleDAO();
    private final MemberPTBUS memberPTBUS = new MemberPTBUS();

    public void register(WorkoutSchedule ws) {

        if (!memberPTBUS.canUseSession(ws.getMemberPTID()))
            throw new IllegalStateException("Gói PT đã hết buổi");

        if (dao.isSlotBooked(
                ws.getTrainerID(),
                ws.getDate(),
                ws.getStartTime(),
                ws.getEndTime()))
            throw new IllegalStateException("Trùng lịch PT");

        ws.setStatus("BOOKED");
        if (!dao.insert(ws))
            throw new RuntimeException("Đăng ký thất bại");
    }

    public List<Object[]> loadTable() {
        return dao.getAllForTable();
    }
}