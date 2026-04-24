package business;

import data.PTServiceDAO;
import model.PTService;
import model.Permission;

import java.math.BigDecimal;
import java.util.List;

public class PTServiceBUS {

    private final PTServiceDAO dao = new PTServiceDAO();

    public List<PTService> getAll() {
        return dao.getAll();
    }

    public PTService getById(int id) {
        return dao.findById(id);
    }

    public void add(PTService s) {
        AuthorizationService.check(Permission.PTService_ADD);
        validate(s);
        dao.insert(s);
    }

    public void update(PTService s) {
        AuthorizationService.check(Permission.PTService_UPDATE);
        validate(s);
        dao.update(s);
    }

    public void delete(int id) {
        AuthorizationService.check(Permission.PTService_DELETE);
        dao.delete(id);
    }

    private void validate(PTService s) {
        if (s.getServiceName() == null || s.getServiceName().trim().isEmpty())
            throw new IllegalArgumentException("Tên dịch vụ không được trống");

        if (s.getTotalSessions() <= 0)
            throw new IllegalArgumentException("Số buổi phải > 0");

        if (s.getPrice() == null || s.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Giá phải > 0");
    }
    public List<PTService> getByTrainer(int trainerID) {
        return dao.getByTrainer(trainerID);
    }
}