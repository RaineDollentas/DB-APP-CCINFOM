package database;

import java.sql.*;

public class DeliveryStatusDatabase {

    public static ResultSet getDailyDeliveryReport(Connection conn, String date) throws SQLException{
        String sql = """
                SELECT
                    DATE(timestamp) AS delivery_date,
                    COUNT(*) AS total_deliveries,
                    SUM(CASE WHEN status_update = 'Delivered' THEN 1 ELSE 0 END) AS total_successful_deliveries,
                    SUM(CASE WHEN status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) AS total_unsuccessful_deliveries,
                    ROUND((SUM(CASE WHEN status_update = 'Delivered' THEN 1.0 ELSE 0 END) / COUNT(parcel_id) * 100), 2) as success_rate
                FROM parcel_status
                WHERE DATE(timestamp) = ?
                GROUP BY DATE(timestamp)
            """;

        PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, date);
        return pStmt.executeQuery();
    }

    public static ResultSet getMonthlyDeliveryReport(Connection conn, int year, int month) throws SQLException{
        String sql = """
            SELECT
                DATE_FORMAT(timestamp, '%Y-%m') AS delivery_month,
                COUNT(*) AS total_deliveries,
                SUM(CASE WHEN status_update = 'Delivered' THEN 1 ELSE 0 END) AS total_successful_deliveries,
                SUM(CASE WHEN status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) AS total_unsuccessful_deliveries,
                ROUND((SUM(CASE WHEN status_update = 'Delivered' THEN 1.0 ELSE 0 END) / COUNT(parcel_id) * 100), 2) as success_rate
            FROM parcel_status
            WHERE YEAR(timestamp) = ? AND MONTH(timestamp) = ?  -- Replace with specific year and month
            GROUP BY DATE_FORMAT(timestamp, '%Y-%m');
        """;

        PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setInt(1, year);
        pStmt.setInt(2, month);
        return pStmt.executeQuery();
    }

    public static ResultSet getYearlyDeliveryReport(Connection conn, int year) throws SQLException{
        String sql = """
            SELECT
                YEAR(timestamp) AS delivery_year,
                COUNT(*) AS total_deliveries,
                SUM(CASE WHEN status_update = 'Delivered' THEN 1 ELSE 0 END) AS total_successful_deliveries,
                SUM(CASE WHEN status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) AS total_unsuccessful_deliveries,
                ROUND((SUM(CASE WHEN status_update = 'Delivered' THEN 1.0 ELSE 0 END) / COUNT(parcel_id) * 100), 2) as success_rate
            FROM parcel_status
            WHERE YEAR(timestamp) = ?
            GROUP BY YEAR(timestamp)
            """;

        PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setInt(1, year);

        return pStmt.executeQuery();
    }




}
