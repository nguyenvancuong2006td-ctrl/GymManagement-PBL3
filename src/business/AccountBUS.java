package business;

import data.AccountDAO;
import model.Account;

public class AccountBUS {

    private final AccountDAO dao = new AccountDAO();

    /* ================= STAFF FLOW ================= */

    // Tạo account cho STAFF (bắt buộc trả ID)
    public int createStaffAccount(Account acc) throws Exception {

        if (dao.getByUsername(acc.getUsername()) != null)
            throw new Exception("Username already exists");

        acc.setStatus("Active");
        return dao.insertAndReturnID(acc);
    }

    /* ================= ADMIN FLOW ================= */

    public boolean createAdminAccount(Account acc) throws Exception {

        if (!"Admin".equalsIgnoreCase(acc.getRole()))
            throw new Exception("Only Admin role allowed");

        if (dao.getByUsername(acc.getUsername()) != null)
            throw new Exception("Username already exists");

        acc.setStatus("Active");
        return dao.insert(acc);
    }

    /* ================= UPDATE ================= */

    public boolean updateAccount(Account acc) throws Exception {

        Account old = dao.getById(acc.getAccountID());
        if (old == null)
            throw new Exception("Account not found");

        // Không cho đổi role account Staff
        if (!old.getRole().equals(acc.getRole()) && dao.isLinkedToStaff(acc.getAccountID()))
            throw new Exception("Cannot change role of Staff account");

        return dao.update(acc);
    }

    /* ================= DELETE ================= */

    public boolean deleteAccount(int accountID) throws Exception {

        if (dao.isLinkedToStaff(accountID))
            throw new Exception("Cannot delete account linked to Staff");

        return dao.delete(accountID);
    }

    /* ================= AUTH ================= */

    public Account login(String username, String password) throws Exception {

        Account acc = dao.getByUsername(username);
        if (acc == null) return null;

        if (!acc.getPassword().equals(password)) return null;
        if (!"Active".equals(acc.getStatus())) return null;

        return acc;
    }
}