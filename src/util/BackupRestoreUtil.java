package util;

public class BackupRestoreUtil {

    private static final String DB_NAME = "GymManagement";
    private static final String BACKUP_PATH =
            "C:\\Program Files\\Microsoft SQL Server\\MSSQL16.SQLEXPRESS\\MSSQL\\Backup\\gym_backup.bak";

    public static boolean backupDatabase() {
        try {
            String sql =
                    "BACKUP DATABASE " + DB_NAME +
                            " TO DISK = '" + BACKUP_PATH + "' WITH INIT";

            DBConnection.execute(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean restoreDatabase() {
        try {
            // ✅ ÉP DB về SINGLE_USER để ngắt connection
            DBConnection.execute(
                    "ALTER DATABASE " + DB_NAME +
                            " SET SINGLE_USER WITH ROLLBACK IMMEDIATE"
            );

            // ✅ PHỤC HỒI DATABASE
            DBConnection.execute(
                    "RESTORE DATABASE " + DB_NAME +
                            " FROM DISK = '" + BACKUP_PATH + "' WITH REPLACE"
            );

            // ✅ MỞ LẠI CHẾ ĐỘ MULTI_USER
            DBConnection.execute(
                    "ALTER DATABASE " + DB_NAME +
                            " SET MULTI_USER"
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
