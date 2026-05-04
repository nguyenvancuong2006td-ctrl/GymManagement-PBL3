package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=GymManagement;"
                    + "encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "123456789";
    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }


    public static void execute(String sql) {

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi thực thi SQL hệ thống");
        }
    }

    // KẾT NỐI DATABASE master (dùng cho BACKUP / RESTORE)
    public static Connection getMasterConnection() throws SQLException {
        String masterUrl =
                "jdbc:sqlserver://localhost:1433;databaseName=master;"
                        + "encrypt=false;trustServerCertificate=true";

        return DriverManager.getConnection(masterUrl, USER, PASS);
    }

    //  THỰC THI SQL TRÊN master
    public static void executeOnMaster(String sql) {
        try (
                Connection conn = getMasterConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi thực thi SQL hệ thống (master)");
        }
    }
}