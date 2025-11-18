package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

/**
 * Popup used to create or edit a courier record.
 */
public class CourierForm extends JDialog {

    public JTextField txtCourierId;
    public JTextField txtFirstName;
    public JTextField txtLastName;
    public JComboBox<String> cbVehicleType;
    public JTextField txtEmail;
    public JTextField txtContactNo;
    public JTextField txtHireDate;

    public JButton btnSave;
    public JButton btnCancel;

    /**
     * Constructs the courier form dialog
     */
    public CourierForm(JFrame parentFrame, String title, boolean isEditMode) {
        super(parentFrame, title, true);

        setSize(450, 450);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form components
        txtCourierId = new JTextField();
        txtFirstName = new JTextField();
        txtLastName = new JTextField();

        // Vehicle type dropdown
        String[] vehicleTypes = {"Motorcycle", "Car", "Van", "Truck", "Bicycle"};
        cbVehicleType = new JComboBox<>(vehicleTypes);

        txtEmail = new JTextField();
        txtContactNo = new JTextField();

        // Hire date - auto today for new couriers
        txtHireDate = new JTextField(Date.valueOf(java.time.LocalDate.now()).toString());
        txtHireDate.setEditable(false);
        txtHireDate.setBackground(Color.LIGHT_GRAY);

        // Add components to form
        formPanel.add(new JLabel("Courier ID:"));
        formPanel.add(txtCourierId);
        txtCourierId.setEditable(!isEditMode); // Can't edit ID when updating

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(txtFirstName);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(txtLastName);

        formPanel.add(new JLabel("Vehicle Type:"));
        formPanel.add(cbVehicleType);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Contact No:"));
        formPanel.add(txtContactNo);

        formPanel.add(new JLabel("Hire Date:"));
        formPanel.add(txtHireDate);

        add(formPanel, BorderLayout.CENTER);

        // Buttons Save / Cancel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getters
    public int getCourierId() {
        try {
            return Integer.parseInt(txtCourierId.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    public String getFirstName() { return txtFirstName.getText().trim(); }
    public String getLastName() { return txtLastName.getText().trim(); }
    public String getVehicleType() { return cbVehicleType.getSelectedItem().toString(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getContactNo() { return txtContactNo.getText().trim(); }
    public Date getHireDate() { return Date.valueOf(txtHireDate.getText().trim()); }

    // Setters for edit mode
    public void setCourierId(int id) { txtCourierId.setText(String.valueOf(id)); }
    public void setFirstName(String name) { txtFirstName.setText(name); }
    public void setLastName(String name) { txtLastName.setText(name); }
    public void setVehicleType(String type) { cbVehicleType.setSelectedItem(type); }
    public void setEmail(String email) { txtEmail.setText(email); }
    public void setContactNo(String contact) { txtContactNo.setText(contact); }
    public void setHireDate(Date date) { txtHireDate.setText(date.toString()); }
}
