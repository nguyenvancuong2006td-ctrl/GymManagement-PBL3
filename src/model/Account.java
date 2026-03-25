package model;

import java.time.LocalDate;
import java.util.Objects;

public class Account {
    private int accountID;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDate createDate;
    private String status;

    public Account() {}

    public Account(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Account(int accountID, String username, String passwordHash,
                   String role, LocalDate createDate, String status) {
        this.accountID = accountID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createDate = createDate;
        this.status = status;
    }

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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