package util;

import model.Account;
import model.Role;

public class Session {

    private static Account currentAccount;

    // ================= SET SESSION =================
    public static void set(Account acc) {
        currentAccount = acc;
    }

    // ================= GET SESSION =================
    public static Account getCurrentUser() {
        return currentAccount;
    }

    // (giữ lại để tương thích code cũ nếu có)
    public static Account getAccount() {
        return currentAccount;
    }

    public static Role getRole() {
        return currentAccount != null ? currentAccount.getRole() : null;
    }

    // ================= UTILS =================
    public static boolean isLoggedIn() {
        return currentAccount != null;
    }

    public static void clear() {
        currentAccount = null;
    }
}