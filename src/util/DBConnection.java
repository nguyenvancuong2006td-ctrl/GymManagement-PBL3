package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=GymManagement;encrypt=false;";
    private static final String USER = "sa";
    private static final String PASS = "123456789";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Lỗi kết nối DB!");
            e.printStackTrace();
            return null;
        }
    }
}