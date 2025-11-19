package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryTrendsDatabase {

    public static List<Integer> getAvailableYears(Connection conn) throws SQLException {
        List<Integer> years = new ArrayList<>();

        String sql = "SELECT DISTINCT YEAR(timestamp) AS yr FROM parcel_status ORDER BY yr DESC";
        ResultSet rs = conn.prepareStatement(sql).executeQuery();

        while (rs.next()) years.add(rs.getInt("yr"));
        if (years.isEmpty()) years.add(2024);

        return years;
    }

    public static String buildDailyQuery(int year, int month) {

        return """
            SELECT 
                DATE(timestamp) AS 'Date',
                COUNT(*) AS 'Total Deliveries'
            FROM parcel_status
            WHERE YEAR(timestamp) = """ + year +
                " AND MONTH(timestamp) = " + month + """
            GROUP BY DATE(timestamp)
            ORDER BY DATE(timestamp)
        """;
    }

    public static String buildGeoQuery(int year, int month) {

        return """
            SELECT 
                recipient_address AS 'Location',
                COUNT(*) AS 'Deliveries'
            FROM parcel_status
            WHERE YEAR(timestamp) = """ + year +
                " AND MONTH(timestamp) = " + month + """
            GROUP BY recipient_address
            ORDER BY Deliveries DESC
            LIMIT 10
        """;
    }

    public static String generateSummary(Connection conn, int year, int month) throws SQLException {

        String sql = """
            SELECT 
                DATE(timestamp) AS day,
                COUNT(*) AS count
            FROM parcel_status
            WHERE YEAR(timestamp)=? AND MONTH(timestamp)=?
            GROUP BY DATE(timestamp)
            ORDER BY count DESC
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, year);
        ps.setInt(2, month);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return "No deliveries found.";

        String topDay = rs.getString("day");
        int count = rs.getInt("count");

        return String.format("""
                DELIVERY TRENDS SUMMARY
                ===============================
                Month: %d-%02d

                Peak Delivery Day:
                - %s  (%d deliveries)

                View the bottom table for top locations.
                ===============================
                """, year, month, topDay, count);
    }
}
