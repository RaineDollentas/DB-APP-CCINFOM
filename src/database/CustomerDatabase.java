package database;

import java.sql.*;

public class CustomerDatabase {

    // get all customers, used by LoadTable
    public static ResultSet getAllCustomers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM customers";
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt.executeQuery();
    }

    // insert customer
    public static void insertCustomer(Connection conn,
                                      String firstName,
                                      String lastName,
                                      String address,
                                      String contactNo,
                                      String email,
                                      Date joinDate) throws SQLException {

        String sql = "INSERT INTO customers (first_name, last_name, address, contact_no, email, join_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, address);
        stmt.setString(4, contactNo);
        stmt.setString(5, email);
        stmt.setDate(6, joinDate);

        stmt.executeUpdate();
    }

    // update customer
    public static void updateCustomer(Connection conn,
                                      int customerId,
                                      String firstName,
                                      String lastName,
                                      String address,
                                      String contactNo,
                                      String email,
                                      Date joinDate) throws SQLException {

        String sql = "UPDATE customers SET first_name=?, last_name=?, address=?, contact_no=?, email=?, join_date=? "
                   + "WHERE customer_id=?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, address);
        stmt.setString(4, contactNo);
        stmt.setString(5, email);
        stmt.setDate(6, joinDate);
        stmt.setInt(7, customerId);

        stmt.executeUpdate();
    }

    // delete customer
    public static void deleteCustomer(Connection conn, int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        stmt.executeUpdate();
    }
    
    // Get specific customer with their parcels (Corpuz)
    public static ResultSet getCustomerWithParcels(Connection conn, int customerId) throws SQLException {
        String sql = """
            SELECT 
                c.customer_id, c.first_name, c.last_name, c.address, c.contact_no, c.email, c.join_date,
                p.parcel_id, p.status, p.booking_date,
                cr.first_name as courier_first_name, cr.last_name as courier_last_name
            FROM customers c
            LEFT JOIN parcels p ON c.customer_id = p.customer_id
            LEFT JOIN couriers cr ON p.courier_id = cr.courier_id
            WHERE c.customer_id = ?
            ORDER BY p.booking_date DESC
        """;
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        return stmt.executeQuery();
    }
}
