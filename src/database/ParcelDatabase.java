package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// databse operations for parcels (made this for booking parcels), btw no add edit delete here yet
// just the necessary ones for booking a parcel, add or modify nalang if needed
public class ParcelDatabase {

    // check if customer exists by ID
    public static boolean customerExists(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT customer_id FROM customers WHERE customer_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    // get customer address by ID, auto fills pick up address
    public static String getCustomerAddress(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT address FROM customers WHERE customer_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) return rs.getString("address");
        return null;
    }

    // get list of couriers for dropdown so customers can just pick between available couriers
    // btw no availability check to, only assumes all couriers are available since im not sure yet how we're gonna do that
    public static List<String> getCouriers(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();

        String sql = "SELECT courier_id, first_name, last_name FROM couriers";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("courier_id");
            String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
            list.add(id + " - " + fullName);
        }

        return list;
    }

    // insert parcel, returns generated parcel ID (auto generated too)
    public static int insertParcel(Connection conn, int customerId, int courierId) throws SQLException {

        String sql = """
            INSERT INTO parcels (customer_id, courier_id, status, booking_date)
            VALUES (?, ?, 'Booked', NOW())
        """;

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, customerId);
        stmt.setInt(2, courierId);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);

        return -1;
    }

    // insert parcel status
    public static void insertParcelStatus(Connection conn,
                                          int parcelId,
                                          int courierId,
                                          String recipientAddress) throws SQLException {

        String sql = """
            INSERT INTO parcel_status 
            (parcel_id, courier_id, status_update, recipient_address, timestamp, parcel_statuscol)
            VALUES (?, ?, 'Booked', ?, NOW(), 'Booked')
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        stmt.setInt(2, courierId);
        stmt.setString(3, recipientAddress);
        stmt.executeUpdate();
    }
}
