package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class BookParcelForm extends JDialog {

    public JTextField txtCustomerId;
    public JTextField txtPickupAddress;  // auto-filled from DB
    public JComboBox<String> cbCouriers; // dropdown list
    public JTextField txtRecipientAddress;
    public JTextField txtBookingDate; // auto today

    public JButton btnSave;
    public JButton btnCancel;

    public BookParcelForm(JFrame parentFrame, String title) {
        super(parentFrame, title, true);

        setSize(420, 420);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // text fields
        txtCustomerId = new JTextField();
        txtPickupAddress = new JTextField();
        txtPickupAddress.setEditable(false);
        txtPickupAddress.setBackground(Color.LIGHT_GRAY);

        cbCouriers = new JComboBox<>();

        txtRecipientAddress = new JTextField();

        txtBookingDate = new JTextField(Date.valueOf(java.time.LocalDate.now()).toString());
        txtBookingDate.setEditable(false);
        txtBookingDate.setBackground(Color.LIGHT_GRAY);

        // labels + fields
        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(txtCustomerId);

        formPanel.add(new JLabel("Pickup Address:"));
        formPanel.add(txtPickupAddress);

        formPanel.add(new JLabel("Assign Courier:"));
        formPanel.add(cbCouriers);

        formPanel.add(new JLabel("Recipient Address:"));
        formPanel.add(txtRecipientAddress);

        formPanel.add(new JLabel("Booking Date:"));
        formPanel.add(txtBookingDate);

        add(formPanel, BorderLayout.CENTER);

        // bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save Booking");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // getters
    public int getCustomerId() { return Integer.parseInt(txtCustomerId.getText().trim()); }
    public String getPickupAddress() { return txtPickupAddress.getText().trim(); }
    public String getRecipientAddress() { return txtRecipientAddress.getText().trim(); }
}
