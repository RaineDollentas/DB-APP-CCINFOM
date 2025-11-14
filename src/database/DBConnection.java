package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lalamove_lite",
                "root",
                "password"
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
