import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Management for Couriers table
 * @author Imperial
 */
public class Courier {
    private String courierId;
    private String lastName;
    private String firstName;
    private String vehicleType;
    private String hireDate;
    private String email;
    private String contactNo;

    private String dbUrl = "jdbc:mysql://127.0.0.1:3306/?user=root";
    private String dbUser = "root";
    private String dbPass = "Hatdog020";

    public Courier() {
        this.courierId = "";
        this.lastName = "";
        this.firstName = "";
        this.vehicleType = "";
        this.hireDate = "";
        this.email = "";
        this.contactNo = "";
    }

    public boolean checkAttributes(){
        return !(this.courierId.isEmpty() && this.lastName.isEmpty() && this.firstName.isEmpty());
    }

    public void setFields(String courierId, String lastName, String firstName,
                          String vehicleType, String hireDate, String email, String contactNo) {
        this.courierId = courierId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.vehicleType = vehicleType;
        this.hireDate = hireDate;
        this.email = email;
        this.contactNo = contactNo;
    }

    public int getRecord(String courierId) {
        try {
            Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
            conn.setCatalog("lalamove-lite");

            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM couriers WHERE courier_id = ?");
            pStmt.setString(1, courierId);

            try (ResultSet rs = pStmt.executeQuery()){
                if (rs.next()){
                    this.courierId = courierId;
                    this.lastName = rs.getString("last_name");
                    this.firstName = rs.getString("first_name");
                    this.vehicleType = rs.getString("vehicle_type");
                    this.hireDate = rs.getString("hire_date");
                    this.email = rs.getString("email");
                    this.contactNo = rs.getString("contact_no");
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

    public int addRecord(){
        if (checkAttributes()) {
            try {
                Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
                conn.setCatalog("lalamove-lite");

                PreparedStatement pStmt = conn.prepareStatement("INSERT INTO couriers (courier_id, last_name, first_name, vehicle_type, hire_date, email, contact_no) VALUES (?,?,?,?,?,?,?)");
                pStmt.setString(1, this.courierId);
                pStmt.setString(2, this.lastName);
                pStmt.setString(3, this.firstName);
                pStmt.setString(4, this.vehicleType);
                pStmt.setString(5, this.hireDate);
                pStmt.setString(6, this.email);
                pStmt.setString(7, this.contactNo);

                pStmt.executeUpdate();
                return 1;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return 0;
    }

    public int updateRecord(){
        if (checkAttributes()) {
            try {
                Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
                conn.setCatalog("lalamove-lite");

                PreparedStatement pStmt = conn.prepareStatement("UPDATE couriers SET last_name=?, first_name=?, vehicle_type=?, hire_date=?, email=?, contact_no=? WHERE courier_id=?");
                pStmt.setString(1, this.lastName);
                pStmt.setString(2, this.firstName);
                pStmt.setString(3, this.vehicleType);
                pStmt.setString(4, this.hireDate);
                pStmt.setString(5, this.email);
                pStmt.setString(6, this.contactNo);
                pStmt.setString(7, this.courierId);

                int rowsAffected = pStmt.executeUpdate();
                return rowsAffected > 0 ? 1 : 0;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return 0;
    }

    public int deleteRecord(String courierId){
        try {
            Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
            conn.setCatalog("lalamove-lite");

            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM couriers WHERE courier_id=?");
            pStmt.setString(1, courierId);

            int rowsAffected = pStmt.executeUpdate();
            return rowsAffected > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}