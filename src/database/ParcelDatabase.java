package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelDatabase {

    // validation

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

    // read operations

    public static ResultSet getAllParcels(Connection conn) throws SQLException {
        String sql = "SELECT * FROM parcels";
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public static ResultSet getParcelById(Connection conn, int parcelId) throws SQLException {
        String sql = "SELECT * FROM parcels WHERE parcel_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeQuery();
    }

    // insert parcel

    public static int insertParcel(Connection conn, int customerId, int courierId, String status) throws SQLException {

        String sql = "INSERT INTO parcels (customer_id, courier_id, status, booking_date) " +
                     "VALUES (?, ?, ?, NOW())";

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, customerId);
        stmt.setInt(2, courierId);
        stmt.setString(3, status);

        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        return -1;
    }

    // update parcel

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

    // delete parcel

    public static int deleteParcel(Connection conn, int parcelId) throws SQLException {
        String sql = "DELETE FROM parcels WHERE parcel_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeUpdate();
    }

    // booking logic 

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
            "(parcel_id, courier_id, status_update, recipient_address, timestamp, parcel_statuscol) " +
            "VALUES (?, ?, 'Booked', ?, NOW(), 'Booked')";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        stmt.setInt(2, courierId);
        stmt.setString(3, recipientAddress);
        stmt.executeUpdate();
    }
}
