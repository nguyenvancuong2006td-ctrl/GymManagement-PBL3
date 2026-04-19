package business;

import data.MemberPTDAO;
import data.PTServiceDAO;
import data.WorkoutScheduleDAO;
import model.MemberPT;
import model.PTService;
import model.WorkoutSchedule;
import model.Permission;

import java.util.List;

public class WorkoutScheduleBUS {

    private final WorkoutScheduleDAO scheduleDAO = new WorkoutScheduleDAO();
    private final MemberPTDAO memberPTDAO = new MemberPTDAO();
    private final PTServiceDAO ptServiceDAO = new PTServiceDAO();

    /* ================= VALIDATE ================= */
    private void validate(WorkoutSchedule ws) {

        if (ws == null)
            throw new IllegalArgumentException("WorkoutSchedule không được null!");

        if (ws.getDate() == null)
            throw new IllegalArgumentException("Ngày tập không được trống!");

        if (ws.getStartTime() == null || ws.getEndTime() == null)
            throw new IllegalArgumentException("Thời gian không được trống!");

        if (!ws.getEndTime().isAfter(ws.getStartTime()))
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu!");

        if (ws.getMemberID() <= 0)
            throw new IllegalArgumentException("MemberID không hợp lệ!");

        if (ws.getTrainerID() <= 0)
            throw new IllegalArgumentException("TrainerID không hợp lệ!");
    }

    /* ================= GET ALL ================= */
    public List<WorkoutSchedule> getAll() {
        AuthorizationService.check(Permission.SCHEDULE_VIEW);
        return scheduleDAO.getAll();
    }

    /* ================= ADD ================= */
    public void add(WorkoutSchedule ws) {
        AuthorizationService.check(Permission.SCHEDULE_ADD);
        validate(ws);

        boolean conflict = scheduleDAO.isConflict(
                ws.getDate(),
                ws.getStartTime(),
                ws.getEndTime(),
                ws.getTrainerID()
        );

        if (conflict)
            throw new IllegalArgumentException(
                    "Huấn luyện viên đã có lịch trong khung giờ này!"
            );

        MemberPT memberPT = memberPTDAO.getByMember(ws.getMemberID());
        if (memberPT == null)
            throw new IllegalArgumentException(
                    "Hội viên chưa mua dịch vụ PT!"
            );

        PTService ptService = ptServiceDAO.findById(memberPT.getServiceID());
        if (ptService == null)
            throw new IllegalArgumentException("Không tìm thấy dịch vụ PT!");

        if (memberPT.getUsedSessions() >= ptService.getTotalSessions())
            throw new IllegalArgumentException(
                    "Hội viên đã dùng hết số buổi PT!"
            );

        ws.setStatus("BOOKED");

        if (!scheduleDAO.insert(ws))
            throw new RuntimeException("Thêm lịch tập thất bại!");
    }

    /* ================= UPDATE ================= */
    public void update(WorkoutSchedule ws) {
        AuthorizationService.check(Permission.SCHEDULE_UPDATE);

        if (ws.getScheduleID() <= 0)
            throw new IllegalArgumentException("ScheduleID không hợp lệ!");

        validate(ws);

        boolean conflict = scheduleDAO.isConflict(
                ws.getDate(),
                ws.getStartTime(),
                ws.getEndTime(),
                ws.getTrainerID()
        );

        if (conflict)
            throw new IllegalArgumentException(
                    "Huấn luyện viên đã có lịch trùng!"
            );

        if (!scheduleDAO.update(ws))
            throw new RuntimeException("Cập nhật lịch tập thất bại!");
    }

    /* ================= COMPLETE ================= */
    public void complete(WorkoutSchedule ws) {
        AuthorizationService.check(Permission.SCHEDULE_UPDATE);

        if (!"BOOKED".equals(ws.getStatus()))
            throw new IllegalArgumentException(
                    "Chỉ hoàn thành được lịch đang BOOKED!"
            );

        MemberPT memberPT = memberPTDAO.getByMember(ws.getMemberID());
        if (memberPT == null)
            throw new IllegalArgumentException("Không tìm thấy quyền PT!");

        memberPTDAO.consumeSession(memberPT.getMemberPTID());

        ws.setStatus("COMPLETED");

        if (!scheduleDAO.update(ws))
            throw new RuntimeException("Hoàn thành lịch thất bại!");
    }

    /* ================= CANCEL ================= */
    public void cancel(WorkoutSchedule ws) {
        AuthorizationService.check(Permission.SCHEDULE_UPDATE);

        if (!"BOOKED".equals(ws.getStatus()))
            throw new IllegalArgumentException(
                    "Chỉ hủy được lịch đang BOOKED!"
            );

        ws.setStatus("CANCELLED");

        if (!scheduleDAO.update(ws))
            throw new RuntimeException("Hủy lịch thất bại!");
    }

    /* ================= DELETE (ADMIN ONLY) ================= */
    public void delete(int id) {
        AuthorizationService.check(Permission.SCHEDULE_DELETE);

        if (id <= 0)
            throw new IllegalArgumentException("ScheduleID không hợp lệ!");

        if (!scheduleDAO.delete(id))
            throw new RuntimeException("Xóa lịch tập thất bại!");
    }
}