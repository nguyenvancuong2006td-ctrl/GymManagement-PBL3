package model;

import java.time.LocalDate;
import java.util.Objects;

public class Account {

    private int accountID;
    private String username;
    private String password;
    private Role role;
    private LocalDate createDate;
    private String status;

    public Account() {}

    public Account(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = "Active";
    }

    public Account(int accountID, String username, String password,
                   Role role, LocalDate createDate, String status) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createDate = createDate;
        this.status = status;
    }

    /* ===== GET / SET ===== */

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }

    //  MAP TỪ DB
    public void setRole(String roleStr) {
        this.role = Role.valueOf(roleStr);
    }

    //  DÙNG CHO DAO
    public String getRoleString() {
        return role != null ? role.name() : null;
    }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean hasPermission(Permission permission) {
        return role != null && role.hasPermission(permission);
    }


    /* ===== OBJECT METHODS ===== */

    @Override
    public String toString() {
        return "Account{" + accountID + ", " + username + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Account) && ((Account)o).accountID == accountID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID);
    }
}