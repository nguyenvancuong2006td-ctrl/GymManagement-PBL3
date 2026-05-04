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
            String sql =
                    "USE master; " +
                            "ALTER DATABASE " + DB_NAME +
                            " SET SINGLE_USER WITH ROLLBACK IMMEDIATE; " +

                            "RESTORE DATABASE " + DB_NAME +
                            " FROM DISK = '" + BACKUP_PATH + "' WITH REPLACE; " +

                            "ALTER DATABASE " + DB_NAME +
                            " SET MULTI_USER;";

            //  BẮT BUỘC dùng master
            DBConnection.executeOnMaster(sql);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
