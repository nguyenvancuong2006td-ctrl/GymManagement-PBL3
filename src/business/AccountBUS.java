package business;

import data.AccountDAO;
import model.Account;

import java.sql.SQLException;
import java.util.List;

public class AccountBUS {

    private final AccountDAO dao = new AccountDAO();

    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";

    private static final String ADMIN = "ADMIN";
    private static final String STAFF = "STAFF";

    // ================== GET ==================
    public List<Account> getAllAccounts() throws SQLException {
        return dao.getAll();
    }

    public Account getAccountById(int id) throws SQLException {
        if (id <= 0) return null;
        return dao.getById(id);
    }

    public Account getByUsername(String username) throws SQLException {
        if (username == null || username.isBlank()) return null;
        return dao.getByUsername(username.trim().toLowerCase());
    }

    // ================== ADD ==================
    public boolean addAccount(Account acc) throws SQLException {

        if (acc == null) return false;

        normalize(acc);

        if (!validateForAdd(acc)) return false;

        if (dao.getByUsername(acc.getUsername()) != null)
            throw new RuntimeException("Username đã tồn tại");

        acc.setStatus(ACTIVE);
        return dao.insert(acc);
    }

    // ================== UPDATE ==================
    public boolean updateAccount(Account acc) throws SQLException {

        if (acc == null || acc.getAccountID() <= 0) return false;

        Account old = dao.getById(acc.getAccountID());
        if (old == null) return false;

        normalize(acc);

        Account existing = dao.getByUsername(acc.getUsername());
        if (existing != null && existing.getAccountID() != acc.getAccountID())
            throw new RuntimeException("Username đã tồn tại");

        // nếu không nhập password mới → giữ password cũ
        if (acc.getPassword() == null || acc.getPassword().isBlank()) {
            acc.setPassword(old.getPassword());
        }

        return dao.update(acc);
    }

    // ================== DELETE / DEACTIVATE ==================
    public boolean deleteAccount(int id) throws SQLException {
        Account acc = dao.getById(id);
        if (acc == null) return false;

        acc.setStatus(INACTIVE);
        return dao.update(acc);
    }

    // ================== LOGIN ==================
    public Account login(String username, String password) throws SQLException {

        if (username == null || username.isBlank())
            throw new RuntimeException("Vui lòng nhập username");

        if (password == null || password.isBlank())
            throw new RuntimeException("Vui lòng nhập mật khẩu");

        username = username.trim().toLowerCase();
        password = password.trim();

        Account acc = dao.getByUsername(username);
        if (acc == null)
            throw new RuntimeException("Tài khoản không tồn tại");

        // SO SÁNH PASSWORD
        if (!password.equals(acc.getPassword()))
            throw new RuntimeException("Sai mật khẩu");

        if (!ACTIVE.equals(acc.getStatus()))
            throw new RuntimeException("Tài khoản đã bị khóa");

        return acc;
    }

    // ================== NORMALIZE ==================
    private void normalize(Account acc) {

        if (acc.getUsername() != null)
            acc.setUsername(acc.getUsername().trim().toLowerCase());

        if (acc.getPassword() != null)
            acc.setPassword(acc.getPassword().trim());

        if (acc.getRole() != null)
            acc.setRole(acc.getRole().trim().toUpperCase());

        if (acc.getStatus() != null)
            acc.setStatus(acc.getStatus().trim().toUpperCase());
    }

    // ================== VALIDATE ==================
    private boolean validateForAdd(Account acc) {

        if (acc.getUsername() == null || acc.getUsername().length() < 4)
            return false;

        if (acc.getPassword() == null || acc.getPassword().length() < 6)
            return false;

        if (!ADMIN.equals(acc.getRole()) && !STAFF.equals(acc.getRole()))
            return false;

        return true;
    }
}