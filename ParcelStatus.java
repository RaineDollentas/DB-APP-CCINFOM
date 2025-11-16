import java.sql.*;


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

    private final String dbUrl = "jdbc:mysql://127.0.0.1:3306/?user=root"; //local to my machine
    private final String dbUser = "root";
    private final String dbPass = "admin"; //local to my machine


    /**
     * Initialize fields with default values that would
     * be deemed as invalid
     */
    public ParcelStatus() {
        this.resetFields();
    }

    /**
     * Sets all attributes to an empty string
     * which is considered invalid
     */
    public void resetFields() {
        this.trackingId = "";
        this.parcelId = "";
        this.courierId = "";
        this.statusUpdate = "";
        this.recipientAddress = "";
        this.timeStamp = "";
    }

    /**
     * Checks if values within the attributes are valid
     * @return validity of the all the attributes
     */
    public boolean checkAttributes(){
        return !this.trackingId.isEmpty() || !this.parcelId.isEmpty() || !this.courierId.isEmpty() ||
                !this.statusUpdate.isEmpty() || !this.recipientAddress.isEmpty() || !this.timeStamp.isEmpty();
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

            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM parcel_status WHERE parcel_id = ?");
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
            return 0;
        }
    }

    /**
     * Adds a record based on the current attributes of the object
     * @return 1 if successful
     */
    public int addRecord(){
        if (this.checkAttributes()) {
            try {
                Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
                conn.setCatalog("lalamove-lite");

                PreparedStatement pStmt = conn.prepareStatement("INSERT INTO parcel_status VALUES (?,?,?,?,?,?)");
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
                return 0;
            }
        }

        return 0;
    }

    /**
     * Modifies an existing record within the database
     * @param trackingId The tracking_id of the record to be modified
     * @param newRecord the record to get the new info from
     * @return the success of the update
     */
    public int modRecord(String trackingId, ParcelStatus newRecord){

        try{
            Connection conn = DriverManager.getConnection(this.dbUrl,this.dbUser,this.dbPass);
            conn.setCatalog("lalamove-lite");

            PreparedStatement statement = conn.prepareStatement("UPDATE parcel_status SET " +
                    "tracking_id = ?, parcel_id = ?, courier_id = ?, status_update = ?, " +
                    "recipient_address = ?, timestamp = ? " +
                    "WHERE tracking_id = ?");

            statement.setString(1, newRecord.trackingId);
            statement.setString(2, newRecord.parcelId);
            statement.setString(3, newRecord.courierId);
            statement.setString(4, newRecord.statusUpdate);
            statement.setString(5, newRecord.recipientAddress);
            statement.setString(6, newRecord.timeStamp);
            statement.setString(7, trackingId);

            statement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }

    }

    /**
     * Deletes a record from the table
     * @return The success of deletion
     */
    public int deleteRecord(String trackingId, String parcelId) {
        try {
            Connection conn = DriverManager.getConnection(this.dbUrl,this.dbUser,this.dbPass);
            conn.setCatalog("lalamove-lite");

            PreparedStatement statement = conn.prepareStatement("DELETE FROM parcel_status WHERE tracking_id = ? AND parcel_id = ?");

            statement.setString(1, trackingId);
            statement.setString(2, parcelId);

            statement.executeUpdate();
            return 1;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getParcelId() {
        return parcelId;
    }

    public String getCourierId() {
        return courierId;
    }

    public String getStatusUpdate() {
        return statusUpdate;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public void setStatusUpdate(String statusUpdate) {
        this.statusUpdate = statusUpdate;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}

