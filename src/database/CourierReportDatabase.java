package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourierReportDatabase {

    // Daily Courier Performance Report
    public static ResultSet getDailyCourierPerformance(Connection conn, String date) throws SQLException {
        String sql = """
            WITH CourierParcels AS (
                
                SELECT DISTINCT 
                    c.courier_id,
                    c.first_name,
                    c.last_name, 
                    c.vehicle_type,
                    ps.parcel_id
                FROM couriers c
                LEFT JOIN parcel_status ps ON c.courier_id = ps.courier_id AND DATE(ps.timestamp) = ?
            ),
            ParcelLatestStatus AS (
                
                SELECT 
                    ps1.parcel_id,
                    ps1.status_update as final_status
                FROM parcel_status ps1
                INNER JOIN (
                    SELECT parcel_id, MAX(timestamp) as max_timestamp
                    FROM parcel_status
                    GROUP BY parcel_id
                ) ps2 ON ps1.parcel_id = ps2.parcel_id AND ps1.timestamp = ps2.max_timestamp
            )
            SELECT 
                cp.courier_id,
                cp.first_name,
                cp.last_name,
                cp.vehicle_type,
                
                COUNT(cp.parcel_id) as total_deliveries,
                
                SUM(CASE WHEN pls.final_status IN ('Delivered', 'Returned') THEN 1 ELSE 0 END) as successful_deliveries,
                
                SUM(CASE WHEN pls.final_status IN ('Cancelled', 'Failed') THEN 1 ELSE 0 END) as unsuccessful_deliveries,
                
                CASE 
                    WHEN COUNT(cp.parcel_id) > 0 
                    THEN ROUND(
                        (SUM(CASE WHEN pls.final_status IN ('Delivered', 'Returned') THEN 1 ELSE 0 END) / 
                         COUNT(cp.parcel_id) * 100), 2
                    )
                    ELSE 0 
                END as success_rate
            FROM CourierParcels cp
            LEFT JOIN ParcelLatestStatus pls ON cp.parcel_id = pls.parcel_id
            GROUP BY cp.courier_id, cp.first_name, cp.last_name, cp.vehicle_type
            ORDER BY success_rate DESC, total_deliveries DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, date);
        return stmt.executeQuery();
    }

    // Monthly Courier Performance Report
    public static ResultSet getMonthlyCourierPerformance(Connection conn, int year, int month) throws SQLException {
        String sql = """
            WITH CourierParcels AS (
                SELECT DISTINCT 
                    c.courier_id,
                    c.first_name,
                    c.last_name, 
                    c.vehicle_type,
                    ps.parcel_id
                FROM couriers c
                LEFT JOIN parcel_status ps ON c.courier_id = ps.courier_id 
                    AND YEAR(ps.timestamp) = ? AND MONTH(ps.timestamp) = ?
            ),
            ParcelLatestStatus AS (
                SELECT 
                    ps1.parcel_id,
                    ps1.status_update as final_status
                FROM parcel_status ps1
                INNER JOIN (
                    SELECT parcel_id, MAX(timestamp) as max_timestamp
                    FROM parcel_status
                    GROUP BY parcel_id
                ) ps2 ON ps1.parcel_id = ps2.parcel_id AND ps1.timestamp = ps2.max_timestamp
            )
            SELECT 
                cp.courier_id,
                cp.first_name,
                cp.last_name,
                cp.vehicle_type,
                COUNT(cp.parcel_id) as total_deliveries,
                
                SUM(CASE WHEN pls.final_status IN ('Delivered', 'Returned') THEN 1 ELSE 0 END) as successful_deliveries,
                
                SUM(CASE WHEN pls.final_status IN ('Cancelled', 'Failed') THEN 1 ELSE 0 END) as unsuccessful_deliveries,
                CASE 
                    WHEN COUNT(cp.parcel_id) > 0 
                    THEN ROUND(
                        (SUM(CASE WHEN pls.final_status IN ('Delivered', 'Returned') THEN 1 ELSE 0 END) / 
                         COUNT(cp.parcel_id) * 100), 2
                    )
                    ELSE 0 
                END as success_rate
            FROM CourierParcels cp
            LEFT JOIN ParcelLatestStatus pls ON cp.parcel_id = pls.parcel_id
            GROUP BY cp.courier_id, cp.first_name, cp.last_name, cp.vehicle_type
            ORDER BY success_rate DESC, total_deliveries DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, year);
        stmt.setInt(2, month);
        return stmt.executeQuery();
    }

    // Yearly Courier Performance Report
    public static ResultSet getYearlyCourierPerformance(Connection conn, int year) throws SQLException {
        String sql = """
            WITH CourierParcels AS (
                SELECT DISTINCT 
                    c.courier_id,
                    c.first_name,
                    c.last_name, 
                    c.vehicle_type,
                    ps.parcel_id
                FROM couriers c
                LEFT JOIN parcel_status ps ON c.courier_id = ps.courier_id AND YEAR(ps.timestamp) = ?
            ),
            ParcelLatestStatus AS (
                SELECT 
                    ps1.parcel_id,
                    ps1.status_update as final_status
                FROM parcel_status ps1
                INNER JOIN (
                    SELECT parcel_id, MAX(timestamp) as max_timestamp
                    FROM parcel_status
                    GROUP BY parcel_id
                ) ps2 ON ps1.parcel_id = ps2.parcel_id AND ps1.timestamp = ps2.max_timestamp
            )
            SELECT 
                cp.courier_id,
                cp.first_name,
                cp.last_name,
                cp.vehicle_type,
                COUNT(cp.parcel_id) as total_deliveries,
             
                SUM(CASE WHEN pls.final_status IN ('Delivered', 'Returned') THEN 1 ELSE 0 END) as successful_deliveries,
                
                SUM(CASE WHEN pls.final_status IN ('Cancelled', 'Failed') THEN 1 ELSE 0 END) as unsuccessful_deliveries,
                CASE 
                    WHEN COUNT(cp.parcel_id) > 0 
                    THEN ROUND(
                        (SUM(CASE WHEN pls.final_status IN ('Delivered', 'Returned') THEN 1 ELSE 0 END) / 
                         COUNT(cp.parcel_id) * 100), 2
                    )
                    ELSE 0 
                END as success_rate
            FROM CourierParcels cp
            LEFT JOIN ParcelLatestStatus pls ON cp.parcel_id = pls.parcel_id
            GROUP BY cp.courier_id, cp.first_name, cp.last_name, cp.vehicle_type
            ORDER BY success_rate DESC, total_deliveries DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, year);
        return stmt.executeQuery();
    }

    // Get available years
    public static List<Integer> getAvailableYears(Connection conn) throws SQLException {
        List<Integer> years = new ArrayList<>();
        String sql = """
            SELECT DISTINCT YEAR(booking_date) as year FROM parcels 
            UNION 
            SELECT DISTINCT YEAR(timestamp) as year FROM parcel_status 
            ORDER BY year DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            years.add(rs.getInt("year"));
        }

        if (years.isEmpty()) {
            years.add(java.time.LocalDate.now().getYear());
        }

        return years;
    }

    // Get performance summary
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
        int totalUnsuccessful = 0;
        double avgSuccessRate = 0;
        int activeCouriers = 0;

        while (rs.next()) {
            totalCouriers++;
            int deliveries = rs.getInt("total_deliveries");
            int successful = rs.getInt("successful_deliveries");

            totalDeliveries += deliveries;
            totalSuccessful += successful;
            totalUnsuccessful += rs.getInt("unsuccessful_deliveries");

            if (deliveries > 0) {
                avgSuccessRate += rs.getDouble("success_rate");
                activeCouriers++;
            }
        }

        if (activeCouriers > 0) {
            avgSuccessRate /= activeCouriers;
        }

        return String.format("""
            Performance Summary (%s):
            ==========================================
            Total Couriers: %d
            Active Couriers: %d
            Total Deliveries: %d
            Successful Deliveries: %d (Delivered + Returned)
            Unsuccessful Deliveries: %d (Cancelled + Failed)
            Average Success Rate: %.2f%%
            ==========================================
            Note: 'Returned' parcels count as successful deliveries
            """, period, totalCouriers, activeCouriers, totalDeliveries,
                totalSuccessful, totalUnsuccessful, avgSuccessRate);
    }

    // Get detailed breakdown for better insights
    public static ResultSet getDetailedCourierPerformance(Connection conn, int courierId, int year, int month) throws SQLException {
        String sql = """
            SELECT 
                final_status,
                COUNT(*) as count
            FROM (
                SELECT 
                    ps1.parcel_id,
                    ps1.status_update as final_status
                FROM parcel_status ps1
                INNER JOIN (
                    SELECT parcel_id, MAX(timestamp) as max_timestamp
                    FROM parcel_status
                    WHERE courier_id = ? 
                    AND YEAR(timestamp) = ? 
                    AND MONTH(timestamp) = ?
                    GROUP BY parcel_id
                ) ps2 ON ps1.parcel_id = ps2.parcel_id AND ps1.timestamp = ps2.max_timestamp
            ) status_breakdown
            GROUP BY final_status
            ORDER BY count DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, courierId);
        stmt.setInt(2, year);
        stmt.setInt(3, month);
        return stmt.executeQuery();
    }
}
