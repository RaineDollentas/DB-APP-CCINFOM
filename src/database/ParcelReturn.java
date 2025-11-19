package database;

import java.sql.*;

/**
 * Parcel Return Transaction
 * @author Imperial
 */
public class ParcelReturn {

    /**
     * Initiates a parcel return process
     */
    public int initiateParcelReturn(int parcelId, int returnCourierId, String returnReason) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // a. Read parcel and tracking records to confirm delivery exists
            if (!isParcelDelivered(conn, parcelId)) {
                System.out.println("Parcel not delivered or not found");
                return 0;
            }

            // b. Assign return courier and record courier details
            if (assignReturnCourier(conn, parcelId, returnCourierId) == 0) {
                System.out.println("Failed to assign return courier");
                conn.rollback();
                return 0;
            }

            // c. Update parcel status to "Return to Sender" and link to delivery status
            String recipientAddress = getRecipientAddress(conn, parcelId);
            if (recipientAddress == null) {
                System.out.println("Recipient address not found");
                conn.rollback();
                return 0;
            }

            if (updateParcelAndStatus(conn, parcelId, "Return to Sender", returnCourierId, recipientAddress, returnReason) == 0) {
                System.out.println("Failed to update parcel and status");
                conn.rollback();
                return 0;
            }

            conn.commit();
            return 1;

        } catch (SQLException e) {
            System.out.println("Parcel Return Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Completes the parcel return process
     */
    public int completeParcelReturn(int parcelId, String remarks) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // d. Check if parcel is in transit for return
            if (!isParcelInReturnTransit(conn, parcelId)) {
                System.out.println("Parcel not in return transit");
                return 0;
            }

            // e. Get current address and status
            String recipientAddress = getCurrentRecipientAddress(conn, parcelId);
            if (recipientAddress == null) {
                System.out.println("Current recipient address not found");
                return 0;
            }

            // f. Record return completion with actual return date and remarks
            int courierId = getCourierId(conn, parcelId);
            if (courierId == -1) {
                System.out.println("Courier ID not found");
                conn.rollback();
                return 0;
            }

            if (recordReturnCompletion(conn, parcelId, courierId, "Returned", recipientAddress, remarks) == 0) {
                System.out.println("Failed to record return completion");
                conn.rollback();
                return 0;
            }

            // g. Update parcel status to returned
            if (updateParcelMainStatus(conn, parcelId, "Returned") == 0) {
                System.out.println("Failed to update parcel main status");
                conn.rollback();
                return 0;
            }

            conn.commit();
            return 1;

        } catch (SQLException e) {
            System.out.println("Complete Return Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets detailed information about a parcel for return eligibility check
     */
    public String getParcelDetailsForReturn(int parcelId) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement(
                    "SELECT p.parcel_id, p.status, p.courier_id, c.first_name, c.last_name, " +
                            "cust.first_name as cust_first, cust.last_name as cust_last " +
                            "FROM parcels p " +
                            "LEFT JOIN couriers c ON p.courier_id = c.courier_id " +
                            "LEFT JOIN customers cust ON p.customer_id = cust.customer_id " +
                            "WHERE p.parcel_id = ?");
            pStmt.setInt(1, parcelId);

            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return "Parcel ID: " + rs.getInt("parcel_id") +
                        "\nStatus: " + rs.getString("status") +
                        "\nCurrent Courier: " + rs.getInt("courier_id") +
                        " (" + rs.getString("first_name") + " " + rs.getString("last_name") + ")" +
                        "\nCustomer: " + rs.getString("cust_first") + " " + rs.getString("cust_last");
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Get Parcel Details Error: " + e.getMessage());
            return null;
        }
    }

    // Private helper methods
    private boolean isParcelDelivered(Connection conn, int parcelId) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement("SELECT status FROM parcels WHERE parcel_id = ?");
        pStmt.setInt(1, parcelId);
        ResultSet rs = pStmt.executeQuery();
        if (rs.next()) {
            String status = rs.getString("status");
            // More flexible status check
            return status != null && status.toLowerCase().contains("delivered");
        }
        return false;
    }

    private int assignReturnCourier(Connection conn, int parcelId, int returnCourierId) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement("UPDATE parcels SET courier_id = ? WHERE parcel_id = ?");
        pStmt.setInt(1, returnCourierId);
        pStmt.setInt(2, parcelId);
        return pStmt.executeUpdate();
    }

    private String getRecipientAddress(Connection conn, int parcelId) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement(
                "SELECT recipient_address FROM parcel_status WHERE parcel_id = ? ORDER BY timestamp DESC LIMIT 1");
        pStmt.setInt(1, parcelId);
        ResultSet rs = pStmt.executeQuery();
        return rs.next() ? rs.getString("recipient_address") : null;
    }

    private int updateParcelAndStatus(Connection conn, int parcelId, String status,
                                      int courierId, String recipientAddress, String returnReason) throws SQLException {
        // Update main parcel status - use short status only
        if (updateParcelMainStatus(conn, parcelId, "Return Initiated") == 0) {
            return 0;
        }

        // Generate a more unique tracking ID
        int trackingId = generateUniqueTrackingId(conn);

        // Insert new status record for return - TRUNCATE to avoid data too long
        String statusUpdate = truncateStatus(status + " - " + returnReason, 50); // Adjust length as needed

        PreparedStatement pStmt = conn.prepareStatement(
                "INSERT INTO parcel_status (tracking_id, parcel_id, courier_id, status_update, recipient_address, timestamp) VALUES (?,?,?,?,?,NOW())");
        pStmt.setInt(1, trackingId);
        pStmt.setInt(2, parcelId);
        pStmt.setInt(3, courierId);
        pStmt.setString(4, statusUpdate);
        pStmt.setString(5, recipientAddress);

        return pStmt.executeUpdate();
    }

    private boolean isParcelInReturnTransit(Connection conn, int parcelId) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement("SELECT status FROM parcels WHERE parcel_id = ?");
        pStmt.setInt(1, parcelId);
        ResultSet rs = pStmt.executeQuery();
        if (rs.next()) {
            String status = rs.getString("status");
            // More flexible status check for return transit
            return status != null && status.toLowerCase().contains("return");
        }
        return false;
    }

    private String getCurrentRecipientAddress(Connection conn, int parcelId) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement(
                "SELECT recipient_address FROM parcel_status WHERE parcel_id = ? ORDER BY timestamp DESC LIMIT 1");
        pStmt.setInt(1, parcelId);
        ResultSet rs = pStmt.executeQuery();
        return rs.next() ? rs.getString("recipient_address") : null;
    }

    private int getCourierId(Connection conn, int parcelId) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement("SELECT courier_id FROM parcels WHERE parcel_id = ?");
        pStmt.setInt(1, parcelId);
        ResultSet rs = pStmt.executeQuery();
        return rs.next() ? rs.getInt("courier_id") : -1;
    }

    private int recordReturnCompletion(Connection conn, int parcelId, int courierId,
                                       String status, String recipientAddress, String remarks) throws SQLException {
        int trackingId = generateUniqueTrackingId(conn);

        // Truncate the status to avoid data too long error
        String statusUpdate = truncateStatus(status + " - " + remarks, 50);

        PreparedStatement pStmt = conn.prepareStatement(
                "INSERT INTO parcel_status (tracking_id, parcel_id, courier_id, status_update, recipient_address, timestamp) VALUES (?,?,?,?,?,NOW())");
        pStmt.setInt(1, trackingId);
        pStmt.setInt(2, parcelId);
        pStmt.setInt(3, courierId);
        pStmt.setString(4, statusUpdate);
        pStmt.setString(5, recipientAddress);

        return pStmt.executeUpdate();
    }

    private int updateParcelMainStatus(Connection conn, int parcelId, String status) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement("UPDATE parcels SET status = ? WHERE parcel_id = ?");
        pStmt.setString(1, status);
        pStmt.setInt(2, parcelId);
        return pStmt.executeUpdate();
    }

    /**
     * Generates a more unique tracking ID to avoid conflicts
     */
    private int generateUniqueTrackingId(Connection conn) throws SQLException {
        // Use timestamp combined with random component for better uniqueness
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return Math.abs((int) (timestamp % Integer.MAX_VALUE) + random);
    }

    /**
     * Truncates status string to specified maximum length to avoid database errors
     */
    private String truncateStatus(String status, int maxLength) {
        if (status == null) return "";
        if (status.length() <= maxLength) return status;
        return status.substring(0, maxLength - 3) + "...";
    }
}
