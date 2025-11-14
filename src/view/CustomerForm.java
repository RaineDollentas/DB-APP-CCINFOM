package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;


/**
 * popup used to create or edit a customer record.
 */
public class CustomerForm extends JDialog {

    public JTextField txtFirstName;
    public JTextField txtLastName;
    public JTextField txtAddress;
    public JTextField txtContactNo;
    public JTextField txtEmail;
    public JTextField txtJoinDate;

    public JButton btnSave;
    public JButton btnCancel;

    /**
     * constructs the customer form dialog
     *
     * @param parentFrame the parent frame used for dialog modality and positioning
     * @param title the dialog title
     */
    public CustomerForm(JFrame parentFrame, String title) {
        super(parentFrame, title, true);

        setSize(400, 400);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // create text fields
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtAddress = new JTextField();
        txtContactNo = new JTextField();
        txtEmail = new JTextField();

        // join Date = auto today, feel free to change this
        txtJoinDate = new JTextField(Date.valueOf(java.time.LocalDate.now()).toString());
        txtJoinDate.setEditable(false);  // auto
        txtJoinDate.setBackground(Color.LIGHT_GRAY);

        // add components to form
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(txtFirstName);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(txtLastName);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(txtAddress);

        formPanel.add(new JLabel("Contact No:"));
        formPanel.add(txtContactNo);

        formPanel.add(new JLabel("Email (optional):"));
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Join Date:"));
        formPanel.add(txtJoinDate);

        add(formPanel, BorderLayout.CENTER);

        // buttons Save / Cancel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);

        
    }

    // getters
    public String getFirstName() { return txtFirstName.getText().trim(); }
    public String getLastName() { return txtLastName.getText().trim(); }
    public String getAddress() { return txtAddress.getText().trim(); }
    public String getContactNo() { return txtContactNo.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public Date getJoinDate() { return Date.valueOf(txtJoinDate.getText().trim()); }

}
