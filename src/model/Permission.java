package model;

public enum Permission {

    // Dashboard
    DASHBOARD_VIEW,

    // Member
    MEMBER_VIEW,
    MEMBER_ADD,
    MEMBER_UPDATE,
    MEMBER_DELETE,

    // Staff
    STAFF_VIEW,
    STAFF_ADD,
    STAFF_UPDATE,
    STAFF_DELETE,

    // Package
    PACKAGE_VIEW,
    PACKAGE_ADD,
    PACKAGE_UPDATE,
    PACKAGE_DELETE,

    // Trainer
    TRAINER_VIEW,
    TRAINER_ADD,
    TRAINER_UPDATE,
    TRAINER_DELETE,

    // Schedule
    SCHEDULE_VIEW,
    SCHEDULE_ADD,
    SCHEDULE_UPDATE,
    SCHEDULE_DELETE,

    // Product
    PRODUCT_SHOP_VIEW,
    PRODUCT_MANAGE,
    PRODUCT_ADD,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,

    // Payment & Invoice
    PAYMENT_CREATE,
    PAYMENT_VIEW,
    INVOICE_CREATE,
    INVOICE_VIEW,

    // Report & System
    REPORT_VIEW,
    ACCOUNT_MANAGE
}