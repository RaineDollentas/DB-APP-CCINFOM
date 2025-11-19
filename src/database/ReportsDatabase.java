package database;

import java.sql.*;

public class ReportsDatabase {

    // Customer Trends Report - Customers with most deliveries (Corpuz)
    public static ResultSet getCustomerTrendsReport(Connection conn, String periodType, int month, int year) throws SQLException {
        String sql;

        if ("Monthly".equals(periodType)) {
            // Monthly report: Top customers for specific month/year
            sql = """
                SELECT 
                    c.customer_id,
                    CONCAT(c.first_name, ' ', c.last_name) as customer_name,
                    COUNT(p.parcel_id) as total_deliveries,
                    c.address,
                    c.contact_no
                FROM customers c
                LEFT JOIN parcels p ON c.customer_id = p.customer_id
                WHERE YEAR(p.booking_date) = ? AND MONTH(p.booking_date) = ?
                GROUP BY c.customer_id, c.first_name, c.last_name, c.address, c.contact_no
                ORDER BY total_deliveries DESC, customer_name
                LIMIT 20
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            return stmt.executeQuery();

        } else {
            // Yearly report: Top customers for specific year
            sql = """
                SELECT 
                    c.customer_id,
                    CONCAT(c.first_name, ' ', c.last_name) as customer_name,
                    COUNT(p.parcel_id) as total_deliveries,
                    c.address,
                    c.contact_no
                FROM customers c
                LEFT JOIN parcels p ON c.customer_id = p.customer_id
                WHERE YEAR(p.booking_date) = ?
                GROUP BY c.customer_id, c.first_name, c.last_name, c.address, c.contact_no
                ORDER BY total_deliveries DESC, customer_name
                LIMIT 20
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, year);
            return stmt.executeQuery();
        }
    }
}