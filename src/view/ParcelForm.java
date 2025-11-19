package view;

import javax.swing.*;
import java.awt.*;

public class ParcelForm extends JDialog {

    public JTextField txtParcelId;      // disabled for edit
    public JTextField txtCustomerId;
    public JTextField txtCourierId;
    public JComboBox<String> cbStatus;

    public JButton btnSave;
    public JButton btnCancel;

    public ParcelForm(JFrame parent, String title, boolean isEditMode) {
        super(parent, title, true);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtParcelId = new JTextField();
        txtParcelId.setEditable(false); // cannot change parcel ID

        txtCustomerId = new JTextField();
        txtCourierId = new JTextField();

        String[] statuses = {"Booked", "In Transit", "Delivered", "Returned", "Cancelled"};
        cbStatus = new JComboBox<>(statuses);

        // Form Layout
        if (isEditMode) {
            panel.add(new JLabel("Parcel ID:"));
            panel.add(txtParcelId);
        }

        panel.add(new JLabel("Customer ID:"));
        panel.add(txtCustomerId);

        panel.add(new JLabel("Courier ID:"));
        panel.add(txtCourierId);

        panel.add(new JLabel("Status:"));
        panel.add(cbStatus);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getters
    public int getParcelId() { return Integer.parseInt(txtParcelId.getText()); }
    public int getCustomerId() { return Integer.parseInt(txtCustomerId.getText()); }
    public int getCourierId() { return Integer.parseInt(txtCourierId.getText()); }
    public String getStatus() { return cbStatus.getSelectedItem().toString(); }

    // Setters for edit mode
    public void setParcelId(int id) { txtParcelId.setText(String.valueOf(id)); }
    public void setCustomerId(int id) { txtCustomerId.setText(String.valueOf(id)); }
    public void setCourierId(int id) { txtCourierId.setText(String.valueOf(id)); }
    public void setStatus(String s) { cbStatus.setSelectedItem(s); }
}
