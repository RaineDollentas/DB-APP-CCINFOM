import com.mysql.cj.protocol.Resultset;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Management Code for 'parcelstatus' table
 * these are all untested btw until further notice
 * @author juwan
 */
public class ParcelStatus {
    private String trackingId; //Primary key
    private String parcelId; //Primary Foreign key from 'parcels' table
    private String courierId; //Foreign key from 'couriers' table
    private String statusUpdate; // 'In transit' 'Delivered' 'Cancelled' etc.
    private String recipientAddress;
    private String timeStamp;

    private String dbUrl = "jdbc:mysql://127.0.0.1:3306/?user=root"; //local to my machine
    private String dbUser = "root";
    private String dbPass = "r00tpAssword"; //local to my machine


    /**
     * Initialize fields with invalid default values
     */
    public ParcelStatus() {
        this.trackingId = "";
        this.parcelId = "";
        this.courierId = "";
        this.statusUpdate = "";
        this.recipientAddress = "";
        this.timeStamp = LocalDateTime.MIN;
    }

    /**
     * Checks if values within the attributes are valid
     * @return validity of the all the attributes
     */
    public boolean checkAttributes(){
        if (this.trackingId.isEmpty() && this.parcelId.isEmpty() && this.courierId.isEmpty() &&
            this.statusUpdate.isEmpty() && this.recipientAddress.isEmpty() && this.timeStamp.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * To be used in conjunction with a method that
     * provides the parameters
     * @param trackingId The tracking ID of the parcel status
     * @param parcelID The ID of the parcel to be tracked
     * @param courierID The ID of the courier handling the delivery
     * @param statusUpdate Current status of the delivery
     * @param recipientAddress The address the parcel is going to
     * @param timeStamp idk yet..
     */
    public void setFields (String trackingId, String parcelID,
                           String courierID, String statusUpdate,
                           String recipientAddress, String timeStamp) {
        this.trackingId = trackingId;
        this.parcelId = parcelID;
        this.courierId = courierID;
        this.statusUpdate = statusUpdate;
        this.recipientAddress = recipientAddress;
        this.timeStamp = timeStamp;
    }

    /**
     * Gets a record that matches a parcelid
     * @param parcelId the record to search for
     * @return 1 if successful, 0 if failed
     */
    public int getRecord(String parcelId) {
        try {
            Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
            conn.setCatalog("lalamove-lite");

            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM parcelstatus WHERE parcel_id = ?");
            pStmt.setString(1, trackingId);

            try (ResultSet rs = pStmt.executeQuery()){
                if (rs.next()){
                    this.trackingId = rs.getString("tracking_id");
                    this.parcelId = parcelId;
                    this.courierId = rs.getString("courier_id");
                    this.statusUpdate = rs.getString("status_update");
                    this.recipientAddress =  rs.getString("recipient_address");
                    this.timeStamp = rs.getString("timestamp");



                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    /**
     * adds a record based on the current attributes of the object
     * @return 1 if successful,
     */
    public int addRecord(){
        if (checkAttributes()) {
            try {
                Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
                conn.setCatalog("lalamove-lite");

                PreparedStatement pStmt = conn.prepareStatement("INSERT INTO parcelstatus VALUES (?,?,?,?,?,?)");
                pStmt.setString(1, this.trackingId);
                pStmt.setString(2, this.parcelId);
                pStmt.setString(3, this.courierId);
                pStmt.setString(4, this.statusUpdate);
                pStmt.setString(5, this.recipientAddress);
                pStmt.setString(6, this.timeStamp);

                pStmt.executeUpdate();
                return 1;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return 0;
    }
}
