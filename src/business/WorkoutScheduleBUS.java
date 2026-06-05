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
                ws.getEndTime(),
                -1
        )) {
            throw new IllegalStateException("Trùng lịch PT");
        }

        ws.setStatus("BOOKED");
        if (!dao.insert(ws))
            throw new RuntimeException("Đăng ký thất bại");
    }

    public List<Object[]> loadTable() {
        return dao.getAllForTable();
    }

    public void update(WorkoutSchedule ws) {

        // 1. Kiểm tra gói PT còn buổi không
        if (!memberPTBUS.canUseSession(ws.getMemberPTID())) {
            throw new IllegalStateException("Gói PT đã hết buổi");
        }

        // 2. Kiểm tra trùng lịch với trainer
        if (dao.isSlotBooked(
                ws.getTrainerID(),
                ws.getDate(),
                ws.getStartTime(),
                ws.getEndTime(),
                ws.getScheduleID()
        )) {
            throw new IllegalStateException("Trùng lịch PT");
        }

        // 3. Gọi DAO update
        boolean ok = dao.update(ws);

        // 4. Nếu thất bại thì báo lỗi
        if (!ok) {
            throw new RuntimeException("Cập nhật lịch thất bại");
        }
    }

    public void delete(int scheduleID) {

        if (!dao.delete(scheduleID)) {
            throw new RuntimeException("Xóa lịch thất bại");
        }
    }
}