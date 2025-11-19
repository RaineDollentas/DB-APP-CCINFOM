package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelDatabase {

    // ---------------- VALIDATION ----------------

    public static boolean customerExists(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT customer_id FROM customers WHERE customer_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public static boolean courierExists(Connection conn, int courierId) throws SQLException {
        String sql = "SELECT courier_id FROM couriers WHERE courier_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, courierId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    // ---------------- READ OPERATIONS ----------------

    public static ResultSet getAllParcels(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM parcels").executeQuery();
    }

    public static ResultSet getParcelById(Connection conn, int parcelId) throws SQLException {
        String sql = "SELECT * FROM parcels WHERE parcel_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeQuery();
    }

    // ---------------- INSERT PARCEL ----------------

    public static int insertParcel(Connection conn, int customerId, int courierId, String status) throws SQLException {

        String sql = "INSERT INTO parcels (customer_id, courier_id, status, booking_date) VALUES (?, ?, ?, NOW())";

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, customerId);
        stmt.setInt(2, courierId);
        stmt.setString(3, status);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        return -1;
    }

    // ---------------- UPDATE PARCEL ----------------

    public static int updateParcel(Connection conn, int parcelId, int customerId, int courierId, String status)
            throws SQLException {

        String sql = "UPDATE parcels SET customer_id = ?, courier_id = ?, status = ? WHERE parcel_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        stmt.setInt(2, courierId);
        stmt.setString(3, status);
        stmt.setInt(4, parcelId);

        return stmt.executeUpdate();
    }

    // ---------------- DELETE PARCEL ----------------

    public static int deleteParcel(Connection conn, int parcelId) throws SQLException {
        String sql = "DELETE FROM parcels WHERE parcel_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeUpdate();
    }

    // ---------------- BOOKING LOGIC ----------------

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

    public static String getCustomerAddress(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT address FROM customers WHERE customer_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getString("address");
        return null;
    }

    public static void insertParcelStatus(Connection conn,
                                          int parcelId,
                                          int courierId,
                                          String recipientAddress) throws SQLException {

        String sql =
                "INSERT INTO parcel_status " +
                "(parcel_id, courier_id, status_update, recipient_address, timestamp, remarks) " +
                "VALUES (?, ?, 'Booked', ?, NOW(), 'Booked')";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        stmt.setInt(2, courierId);
        stmt.setString(3, recipientAddress);
        stmt.executeUpdate();
    }

    // ---------------- DELIVERY COMPLETION (Corpuz) ----------------

    public static ResultSet getParcelForDelivery(Connection conn, int parcelId) throws SQLException {
        String sql = """
            SELECT p.status, ps.recipient_address
            FROM parcels p
            LEFT JOIN parcel_status ps ON p.parcel_id = ps.parcel_id
            WHERE p.parcel_id = ?
            ORDER BY ps.timestamp DESC
            LIMIT 1
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeQuery();
    }

    public static boolean completeDelivery(Connection conn, int parcelId, String remarks) throws SQLException {

        // Update parcels table
        String sql1 = "UPDATE parcels SET status = 'Delivered' WHERE parcel_id = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setInt(1, parcelId);

        int updated = ps1.executeUpdate();
        if (updated == 0) return false;

        // Insert new parcel_status record
        String sql2 = """
            INSERT INTO parcel_status (parcel_id, courier_id, status_update, recipient_address, timestamp, remarks)
            SELECT parcel_id, courier_id, 'Delivered', recipient_address, NOW(), ?
            FROM parcel_status
            WHERE parcel_id = ?
            ORDER BY timestamp DESC
            LIMIT 1
        """;

        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps2.setString(1, remarks);
        ps2.setInt(2, parcelId);
        ps2.executeUpdate();

        return true;
    }

    // ---------------- BOOKING CANCELLATION (Hernane) ----------------

    public static boolean cancelBookingByTrackingId(Connection conn, int trackingId) throws SQLException {

        // Find record
        String check = "SELECT parcel_id, status_update FROM parcel_status WHERE tracking_id = ?";
        PreparedStatement cs = conn.prepareStatement(check);
        cs.setInt(1, trackingId);
        ResultSet rs = cs.executeQuery();

        if (!rs.next()) return false;

        int parcelId = rs.getInt("parcel_id");
        String status = rs.getString("status_update").trim().toLowerCase();

        // Cannot cancel these:
        if (status.equals("in transit") ||
            status.equals("out for delivery") ||
            status.equals("delivered") ||
            status.equals("cancelled")) {
            return false;
        }

        // Update parcel table
        String updParcel = "UPDATE parcels SET status = 'Cancelled' WHERE parcel_id = ?";
        PreparedStatement p1 = conn.prepareStatement(updParcel);
        p1.setInt(1, parcelId);
        p1.executeUpdate();

        // Update parcel_status entry
        String updStatus = "UPDATE parcel_status SET status_update = 'Cancelled', remarks = 'Cancelled' WHERE tracking_id = ?";
        PreparedStatement p2 = conn.prepareStatement(updStatus);
        p2.setInt(1, trackingId);
        p2.executeUpdate();

        return true;
    }
}
