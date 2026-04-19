package business;

import data.StaffDAO;
import model.Staff;
import model.StaffAccount;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StaffBUS {

    private final StaffDAO dao = new StaffDAO();

    /* ================= ADD ================= */
    public boolean addStaff(Staff s) throws Exception {

        if (s.getAccountID() <= 0)
            throw new Exception("Staff must have Account first");

        if (dao.getByAccountID(s.getAccountID()) != null)
            throw new Exception("This account already linked to a Staff");

        if (s.getHireDate() == null)
            s.setHireDate(LocalDate.now());

        return dao.insert(s);
    }

    /* ================= UPDATE ================= */
    public boolean updateStaff(Staff s) throws Exception {

        if (s.getStaffID() <= 0)
            throw new Exception("Invalid staff ID");

        return dao.update(s);
    }

    /* ================= DELETE ================= */
    public boolean deleteStaff(int staffID) throws Exception {
        return dao.delete(staffID);
    }

    public List<Staff> getAll() throws SQLException {
        return dao.getAll();
    }

    public List<StaffAccount> getAllWithAccount() {
        return dao.getAllWithAccount();
    }

}