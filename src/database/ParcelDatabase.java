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
        return conn.prepareStatement(sql).executeQuery();
    }

    public static ResultSet getParcelById(Connection conn, int parcelId) throws SQLException {
        String sql = "SELECT * FROM parcels WHERE parcel_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeQuery();
    }

    // insert parcel

    public static int insertParcel(Connection conn, int customerId, int courierId, String status) throws SQLException {

        String sql = """
            INSERT INTO parcels (customer_id, courier_id, status, booking_date)
            VALUES (?, ?, ?, NOW())
        """;

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, customerId);
        stmt.setInt(2, courierId);
        stmt.setString(3, status);

        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        return keys.next() ? keys.getInt(1) : -1;
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

    // book logic

    public static List<String> getCouriers(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();

        String sql = "SELECT courier_id, first_name, last_name FROM couriers";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(rs.getInt("courier_id") + " - " +
                    rs.getString("first_name") + " " + rs.getString("last_name"));
        }
        return list;
    }

    public static String getCustomerAddress(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT address FROM customers WHERE customer_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getString("address") : null;
    }

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

    // find parcel

    public static ResultSet getParcelForDelivery(Connection conn, int parcelId) throws SQLException {
        String sql = """
            SELECT 
                p.parcel_id, p.status, 
                ps.recipient_address, ps.status_update,
                c.first_name, c.last_name
            FROM parcels p
            LEFT JOIN parcel_status ps ON p.parcel_id = ps.parcel_id
            LEFT JOIN customers c ON p.customer_id = c.customer_id
            WHERE p.parcel_id = ?
            ORDER BY ps.timestamp DESC
            LIMIT 1
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, parcelId);
        return stmt.executeQuery();
    }

    // complete delivery

    public static boolean completeDelivery(Connection conn, int parcelId, String remarks) throws SQLException {
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            String checkStatusSQL = "SELECT status FROM parcels WHERE parcel_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkStatusSQL);
            checkStmt.setInt(1, parcelId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) throw new SQLException("Parcel not found");

            String currentStatus = rs.getString("status");
            if (!"In Transit".equalsIgnoreCase(currentStatus) &&
                !"Out for Delivery".equalsIgnoreCase(currentStatus)) {
                throw new SQLException("Parcel is not in transit. Current: " + currentStatus);
            }

            String getAddressSQL = """
                SELECT recipient_address 
                FROM parcel_status 
                WHERE parcel_id = ? 
                ORDER BY timestamp DESC 
                LIMIT 1
            """;

            PreparedStatement addrStmt = conn.prepareStatement(getAddressSQL);
            addrStmt.setInt(1, parcelId);
            ResultSet ars = addrStmt.executeQuery();

            String recipientAddress = ars.next() ? ars.getString("recipient_address") : "";

            PreparedStatement upd = conn.prepareStatement(
                    "UPDATE parcels SET status = 'Delivered' WHERE parcel_id = ?");
            upd.setInt(1, parcelId);
            upd.executeUpdate();

            String insertSQL = """
                INSERT INTO parcel_status 
                (parcel_id, courier_id, status_update, recipient_address, timestamp, remarks)
                SELECT parcel_id, courier_id, 'Delivered', ?, NOW(), ?
                FROM parcels WHERE parcel_id = ?
            """;

            PreparedStatement ins = conn.prepareStatement(insertSQL);
            ins.setString(1, recipientAddress);
            ins.setString(2, remarks);
            ins.setInt(3, parcelId);
            ins.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    // ------------------------------------------------------------------
    // --------------------- CANCELLATION LOGIC -------------------------
    // ------------------------------------------------------------------

    public static boolean cancelBookingByTrackingId(Connection conn, int trackingId) throws SQLException {

        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            // load booking info
            String sql = """
                SELECT 
                    p.parcel_id,
                    p.status AS parcel_status,
                    ps.courier_id,
                    ps.recipient_address
                FROM parcel_status ps
                JOIN parcels p ON ps.parcel_id = p.parcel_id
                WHERE ps.tracking_id = ?
                ORDER BY ps.timestamp DESC
                LIMIT 1
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trackingId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next())
                throw new SQLException("Tracking ID does not exist.");

            int parcelId = rs.getInt("parcel_id");
            int courierId = rs.getInt("courier_id");
            String address = rs.getString("recipient_address");
            String status = rs.getString("parcel_status");

            if (!"Booked".equalsIgnoreCase(status))
                throw new SQLException("Not allowed. Current status: " + status);

            PreparedStatement upd = conn.prepareStatement(
                    "UPDATE parcels SET status = 'Cancelled' WHERE parcel_id = ?");
            upd.setInt(1, parcelId);
            upd.executeUpdate();

            String insertCancel = """
                INSERT INTO parcel_status
                    (parcel_id, courier_id, status_update, recipient_address, timestamp, parcel_statuscol)
                VALUES (?, ?, 'Cancelled', ?, NOW(), 'Cancelled')
            """;

            PreparedStatement ins = conn.prepareStatement(insertCancel);
            ins.setInt(1, parcelId);
            ins.setInt(2, courierId);
            ins.setString(3, address != null ? address : "");
            ins.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }
}
