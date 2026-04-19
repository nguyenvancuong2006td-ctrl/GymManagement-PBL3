package model;

import java.util.Set;

public enum Role {

    // ===== ADMIN: TOÀN QUYỀN =====
    Admin(Set.of(Permission.values())),

    // ===== STAFF: NGHIỆP VỤ HÀNG NGÀY =====
    Staff(Set.of(
            // Dashboard
            Permission.DASHBOARD_VIEW,

            // Member
            Permission.MEMBER_VIEW,
            Permission.MEMBER_ADD,
            Permission.MEMBER_UPDATE,

            // PT
            Permission.TRAINER_VIEW,
            Permission.TRAINER_ADD,
            Permission.TRAINER_UPDATE,

            // Schedule
            Permission.SCHEDULE_VIEW,
            Permission.SCHEDULE_ADD,
            Permission.SCHEDULE_UPDATE,

            // Package
            Permission.PACKAGE_VIEW,

            // Product / Sales
            Permission.PRODUCT_SHOP_VIEW,

            // Payment / Invoice
            Permission.PAYMENT_VIEW,
            Permission.PAYMENT_CREATE,
            Permission.INVOICE_VIEW,
            Permission.INVOICE_CREATE,

            // Report
            Permission.REPORT_VIEW
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}