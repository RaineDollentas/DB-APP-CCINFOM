package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class DeliveryCompletionForm extends JDialog {

    public JTextField txtParcelId;
    public JTextField txtCurrentStatus;
    public JTextField txtRecipientAddress;
    public JTextField txtDeliveryDate;
    public JTextArea txtRemarks;

    public JButton btnComplete;
    public JButton btnCancel;

    public DeliveryCompletionForm(JFrame parentFrame) {
        super(parentFrame, "Complete Delivery", true);
        setSize(500, 400);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtParcelId = new JTextField();

        txtCurrentStatus = new JTextField();
        txtCurrentStatus.setEditable(false);
        txtCurrentStatus.setBackground(Color.LIGHT_GRAY);

        txtRecipientAddress = new JTextField();
        txtRecipientAddress.setEditable(false);
        txtRecipientAddress.setBackground(Color.LIGHT_GRAY);

        txtDeliveryDate = new JTextField(Date.valueOf(java.time.LocalDate.now()).toString());
        txtDeliveryDate.setEditable(false);
        txtDeliveryDate.setBackground(Color.LIGHT_GRAY);

        txtRemarks = new JTextArea(3, 20);
        JScrollPane remarksScroll = new JScrollPane(txtRemarks);

        formPanel.add(new JLabel("Parcel ID:"));
        formPanel.add(txtParcelId);

        formPanel.add(new JLabel("Current Status:"));
        formPanel.add(txtCurrentStatus);

        formPanel.add(new JLabel("Recipient Address:"));
        formPanel.add(txtRecipientAddress);

        formPanel.add(new JLabel("Delivery Date:"));
        formPanel.add(txtDeliveryDate);

        formPanel.add(new JLabel("Remarks:"));
        formPanel.add(remarksScroll);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnComplete = new JButton("Complete Delivery");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnComplete);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getters
    public int getParcelId() {
        try {
            return Integer.parseInt(txtParcelId.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getRemarks() { return txtRemarks.getText().trim(); }

    public String getCurrentStatus() { return txtCurrentStatus.getText().trim(); }

    public String getRecipientAddress() { return txtRecipientAddress.getText().trim(); }

    // Setters used by controller
    public void setCurrentStatus(String status) { txtCurrentStatus.setText(status); }
    public void setRecipientAddress(String address) { txtRecipientAddress.setText(address); }
}
