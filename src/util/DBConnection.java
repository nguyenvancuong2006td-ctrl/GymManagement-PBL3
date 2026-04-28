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

}