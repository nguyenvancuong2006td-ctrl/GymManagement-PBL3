package util;

import model.Account;
import model.Role;
import model.Staff;

public class Session {

    private static Account currentAccount;
    private static Staff currentStaff;

    /* ================= SET SESSION ================= */

    public static void set(Account acc) {
        currentAccount = acc;
    }


    public static void setStaff(Staff staff) {
        currentStaff = staff;
    }

    /* ================= GET SESSION ================= */

    public static Account getAccount() {
        return currentAccount;
    }

    public static Role getRole() {
        return currentAccount != null
                ? currentAccount.getRole()
                : null;
    }

    /* ================= STAFF ================= */

    public static Staff getStaff() {
        return currentStaff;
    }

    public static Integer getStaffID() {
        return currentStaff != null
                ? currentStaff.getStaffID()
                : null;
    }

    /* ================= SHORTCUT / UTILS ================= */

    public static int getAccountID() {
        if (currentAccount == null)
            throw new IllegalStateException("Chưa đăng nhập");
        return currentAccount.getAccountID();
    }

    public static String getUsername() {
        return currentAccount != null
                ? currentAccount.getUsername()
                : null;
    }

    public static boolean isAdmin() {
        return getRole() == Role.Admin;
    }

    public static boolean isStaff() {
        return getRole() == Role.Staff;
    }

    public static boolean isLoggedIn() {
        return currentAccount != null;
    }

    public static void clear() {
        currentAccount = null;
        currentStaff = null;
    }
}
