package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourierReportDatabase {

    // Daily Courier Performance Report
    public static ResultSet getDailyCourierPerformance(Connection conn, String date) throws SQLException {
        String sql = """
            SELECT 
                c.courier_id,
                c.first_name,
                c.last_name,
                COUNT(ps.parcel_id) as total_deliveries,
                SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) as successful_deliveries,
                SUM(CASE WHEN ps.status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) as unsuccessful_deliveries,
                ROUND((SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) / COUNT(ps.parcel_id) * 100), 2) as success_rate
            FROM parcel_status ps
            JOIN couriers c ON ps.courier_id = c.courier_id
            WHERE DATE(ps.timestamp) = ?
            GROUP BY c.courier_id, c.first_name, c.last_name
            ORDER BY success_rate DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, date);
        return stmt.executeQuery();
    }

    // Monthly Courier Performance Report
    public static ResultSet getMonthlyCourierPerformance(Connection conn, int year, int month) throws SQLException {
        String sql = """
            SELECT 
                c.courier_id,
                c.first_name,
                c.last_name,
                COUNT(ps.parcel_id) as total_deliveries,
                SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) as successful_deliveries,
                SUM(CASE WHEN ps.status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) as unsuccessful_deliveries,
                ROUND((SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) / COUNT(ps.parcel_id) * 100), 2) as success_rate
            FROM parcel_status ps
            JOIN couriers c ON ps.courier_id = c.courier_id
            WHERE YEAR(ps.timestamp) = ? AND MONTH(ps.timestamp) = ?
            GROUP BY c.courier_id, c.first_name, c.last_name
            ORDER BY success_rate DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, year);
        stmt.setInt(2, month);
        return stmt.executeQuery();
    }

    // Yearly Courier Performance Report
    public static ResultSet getYearlyCourierPerformance(Connection conn, int year) throws SQLException {
        String sql = """
            SELECT 
                c.courier_id,
                c.first_name,
                c.last_name,
                COUNT(ps.parcel_id) as total_deliveries,
                SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) as successful_deliveries,
                SUM(CASE WHEN ps.status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) as unsuccessful_deliveries,
                ROUND((SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) / COUNT(ps.parcel_id) * 100), 2) as success_rate
            FROM parcel_status ps
            JOIN couriers c ON ps.courier_id = c.courier_id
            WHERE YEAR(ps.timestamp) = ?
            GROUP BY c.courier_id, c.first_name, c.last_name
            ORDER BY success_rate DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, year);
        return stmt.executeQuery();
    }

    // Get available years for dropdown
    public static List<Integer> getAvailableYears(Connection conn) throws SQLException {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(timestamp) as year FROM parcel_status ORDER BY year DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            years.add(rs.getInt("year"));
        }
        return years;
    }

    // Get courier performance summary for a period
    public static String getPerformanceSummary(Connection conn, String period, String date, int year, int month) throws SQLException {
        ResultSet rs;

        switch (period) {
            case "Daily":
                rs = getDailyCourierPerformance(conn, date);
                break;
            case "Monthly":
                rs = getMonthlyCourierPerformance(conn, year, month);
                break;
            case "Yearly":
                rs = getYearlyCourierPerformance(conn, year);
                break;
            default:
                return "Invalid period";
        }

        int totalCouriers = 0;
        int totalDeliveries = 0;
        int totalSuccessful = 0;
        double avgSuccessRate = 0;

        while (rs.next()) {
            totalCouriers++;
            totalDeliveries += rs.getInt("total_deliveries");
            totalSuccessful += rs.getInt("successful_deliveries");
            avgSuccessRate += rs.getDouble("success_rate");
        }

        if (totalCouriers > 0) {
            avgSuccessRate /= totalCouriers;
        }

        return String.format("""
            Performance Summary (%s):
            Total Couriers: %d
            Total Deliveries: %d
            Successful Deliveries: %d
            Average Success Rate: %.2f%%
            """, period, totalCouriers, totalDeliveries, totalSuccessful, avgSuccessRate);
    }
}