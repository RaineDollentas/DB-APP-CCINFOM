package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Management for Couriers table
 * @author Imperial
 */
public class CourierDatabase {

    // Get all couriers
    public static ResultSet getAllCouriers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM couriers";
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt.executeQuery();
    }

    // Insert courier
    public static void insertCourier(Connection conn,
                                     int courierId,
                                     String firstName,
                                     String lastName,
                                     String vehicleType,
                                     String email,
                                     String contactNo) throws SQLException {

        String sql = "INSERT INTO couriers (courier_id, first_name, last_name, vehicle_type, hire_date, email, contact_no) "
                + "VALUES (?, ?, ?, ?, CURDATE(), ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, courierId);
        stmt.setString(2, firstName);
        stmt.setString(3, lastName);
        stmt.setString(4, vehicleType);
        stmt.setString(5, email);
        stmt.setString(6, contactNo);

        stmt.executeUpdate();
    }

    // Update courier
    public static void updateCourier(Connection conn,
                                     int courierId,
                                     String firstName,
                                     String lastName,
                                     String vehicleType,
                                     String email,
                                     String contactNo) throws SQLException {

        String sql = "UPDATE couriers SET first_name=?, last_name=?, vehicle_type=?, email=?, contact_no=? "
                + "WHERE courier_id=?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, vehicleType);
        stmt.setString(4, email);
        stmt.setString(5, contactNo);
        stmt.setInt(6, courierId);

        stmt.executeUpdate();
    }

    // Delete courier
    public static void deleteCourier(Connection conn, int courierId) throws SQLException {
        String sql = "DELETE FROM couriers WHERE courier_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, courierId);
        stmt.executeUpdate();
    }

    // Get available couriers for dropdown
    public static List<String> getAvailableCouriers(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT courier_id, first_name, last_name FROM couriers ORDER BY courier_id";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("courier_id");
            String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
            list.add(id + " - " + fullName);
        }
        return list;
    }

    // Check if courier exists
    public static boolean courierExists(Connection conn, int courierId) throws SQLException {
        String sql = "SELECT courier_id FROM couriers WHERE courier_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, courierId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}