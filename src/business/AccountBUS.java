package business;

import data.AccountDAO;
import model.Account;
import model.Role;
import model.Permission;
import util.Session;

public class AccountBUS {

    private final AccountDAO dao = new AccountDAO();

    /* ================= STAFF FLOW ================= */

    // Tạo account cho STAFF
    public int createStaffAccount(Account acc) throws Exception {

        AuthorizationService.check(Permission.ACCOUNT_MANAGE);

        if (dao.getByUsername(acc.getUsername()) != null)
            throw new Exception("Username already exists");

        acc.setRole("Staff");
        acc.setStatus("Active");

        return dao.insertAndReturnID(acc);
    }

    /* ================= ADMIN FLOW ================= */

    public boolean createAdminAccount(Account acc) throws Exception {

        AuthorizationService.check(Permission.ACCOUNT_MANAGE);

        if (acc.getRole() != Role.Admin)
            throw new Exception("Only ADMIN role allowed");

        if (dao.getByUsername(acc.getUsername()) != null)
            throw new Exception("Username already exists");

        acc.setStatus("Active");
        return dao.insert(acc);
    }

    /* ================= UPDATE ================= */

    public boolean updateAccount(Account acc) throws Exception {

        AuthorizationService.check(Permission.ACCOUNT_MANAGE);

        Account old = dao.getById(acc.getAccountID());
        if (old == null)
            throw new Exception("Account not found");

        // Không cho đổi role nếu account đang gắn với Staff
        if (old.getRole() != acc.getRole() &&
                dao.isLinkedToStaff(acc.getAccountID()))
            throw new Exception("Cannot change role of Staff account");

        return dao.update(acc);
    }

    /* ================= DELETE ================= */

    public boolean deleteAccount(int accountID) throws Exception {

        AuthorizationService.check(Permission.ACCOUNT_MANAGE);

        if (dao.isLinkedToStaff(accountID))
            throw new Exception("Cannot delete account linked to Staff");

        return dao.delete(accountID);
    }

    /* ================= AUTH ================= */

    public Account login(String username, String password) throws Exception {

        Account acc = dao.getByUsername(username);
        if (acc == null) return null;

        // ⚠️ Demo: password plain text
        if (!acc.getPassword().equals(password)) return null;

        if (!"Active".equalsIgnoreCase(acc.getStatus())) return null;

        //  Lưu session
        Session.set(acc);

        return acc;
    }
}