package controller;

import view.TransactionsPanel;
import database.ParcelReturn;

import javax.swing.*;
import java.awt.*;

public class TransactionsController {

    private ParcelReturn parcelReturn;

    public TransactionsController(TransactionsPanel view, JPanel mainPanel, CardLayout cardLayout) {
        this.parcelReturn = new ParcelReturn();

        // Book parcel button (handled by ParcelBookController)
        view.btnBookParcel.addActionListener(e -> {
            // This is handled by ParcelBookController
        });

        // Complete Delivery button
        view.btnCompleteDelivery.addActionListener(e -> {
            completeDelivery();
        });

        // Cancel Booking button
        view.btnCancelBooking.addActionListener(e -> {
            cancelBooking();
        });

        // Parcel Return button
        view.btnParcelReturn.addActionListener(e -> {
            showParcelReturnDialog();
        });
    }

    private void completeDelivery() {
        String parcelIdStr = JOptionPane.showInputDialog(null, "Enter Parcel ID to complete delivery:", "Complete Delivery", JOptionPane.QUESTION_MESSAGE);
        if (parcelIdStr == null || parcelIdStr.trim().isEmpty()) return;

        try {
            int parcelId = Integer.parseInt(parcelIdStr);
            // You would need to create a CompleteDelivery class similar to ParcelReturn
            JOptionPane.showMessageDialog(null, "Complete Delivery functionality to be implemented");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Parcel ID format!");
        }
    }

    private void cancelBooking() {
        String parcelIdStr = JOptionPane.showInputDialog(null, "Enter Parcel ID to cancel:", "Cancel Booking", JOptionPane.QUESTION_MESSAGE);
        if (parcelIdStr == null || parcelIdStr.trim().isEmpty()) return;

        try {
            int parcelId = Integer.parseInt(parcelIdStr);
            // You would need to create a ParcelCancellation class
            JOptionPane.showMessageDialog(null, "Cancel Booking functionality to be implemented");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Parcel ID format!");
        }
    }

    private void showParcelReturnDialog() {
        JDialog returnDialog = new JDialog();
        returnDialog.setTitle("Parcel Return Management");
        returnDialog.setSize(500, 400);
        returnDialog.setLocationRelativeTo(null);
        returnDialog.setLayout(new BorderLayout());
        returnDialog.setModal(true);

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtParcelId = new JTextField();
        JTextField txtReturnCourierId = new JTextField();
        JComboBox<String> cbReturnReason = new JComboBox<>(new String[]{
                "Damaged", "Wrong Item", "Refused Delivery", "Address Error", "Customer Request"
        });
        JTextField txtRemarks = new JTextField();

        mainPanel.add(new JLabel("Parcel ID:"));
        mainPanel.add(txtParcelId);
        mainPanel.add(new JLabel("Return Courier ID:"));
        mainPanel.add(txtReturnCourierId);
        mainPanel.add(new JLabel("Return Reason:"));
        mainPanel.add(cbReturnReason);
        mainPanel.add(new JLabel("Remarks:"));
        mainPanel.add(txtRemarks);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCheckParcel = new JButton("Check Parcel");
        JButton btnInitiateReturn = new JButton("Initiate Return");
        JButton btnCompleteReturn = new JButton("Complete Return");

        buttonPanel.add(btnCheckParcel);
        buttonPanel.add(btnInitiateReturn);
        buttonPanel.add(btnCompleteReturn);

        // Result area
        JTextArea resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        // Add components to dialog
        returnDialog.add(mainPanel, BorderLayout.NORTH);
        returnDialog.add(buttonPanel, BorderLayout.CENTER);
        returnDialog.add(resultScroll, BorderLayout.SOUTH);

        // Button actions
        btnCheckParcel.addActionListener(e -> {
            String parcelIdStr = txtParcelId.getText().trim();
            if (parcelIdStr.isEmpty()) {
                resultArea.setText("Please enter a Parcel ID.");
                return;
            }

            try {
                int parcelId = Integer.parseInt(parcelIdStr);
                String details = parcelReturn.getParcelDetailsForReturn(parcelId);
                if (details != null) {
                    resultArea.setText("Parcel Details:\n" + details);
                } else {
                    resultArea.setText("Parcel not found or error retrieving details.");
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid Parcel ID format!");
            }
        });

        btnInitiateReturn.addActionListener(e -> {
            String parcelIdStr = txtParcelId.getText().trim();
            String courierIdStr = txtReturnCourierId.getText().trim();
            String reason = cbReturnReason.getSelectedItem().toString();

            if (parcelIdStr.isEmpty() || courierIdStr.isEmpty()) {
                resultArea.setText("Please enter both Parcel ID and Return Courier ID.");
                return;
            }

            try {
                int parcelId = Integer.parseInt(parcelIdStr);
                int courierId = Integer.parseInt(courierIdStr);

                int result = parcelReturn.initiateParcelReturn(parcelId, courierId, reason);
                if (result == 1) {
                    resultArea.setText("SUCCESS: Return initiated for Parcel ID: " + parcelId +
                            "\nReturn Courier: " + courierId +
                            "\nReason: " + reason);
                } else {
                    resultArea.setText("FAILED: Could not initiate return.\n" +
                            "Make sure parcel is delivered and IDs are valid.");
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid ID format!");
            }
        });

        btnCompleteReturn.addActionListener(e -> {
            String parcelIdStr = txtParcelId.getText().trim();
            String remarks = txtRemarks.getText().trim();

            if (parcelIdStr.isEmpty()) {
                resultArea.setText("Please enter a Parcel ID.");
                return;
            }

            if (remarks.isEmpty()) {
                remarks = "Return completed successfully";
            }

            try {
                int parcelId = Integer.parseInt(parcelIdStr);
                int result = parcelReturn.completeParcelReturn(parcelId, remarks);
                if (result == 1) {
                    resultArea.setText("SUCCESS: Return completed for Parcel ID: " + parcelId +
                            "\nRemarks: " + remarks);
                } else {
                    resultArea.setText("FAILED: Could not complete return.\n" +
                            "Make sure return was initiated and parcel is in return transit.");
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid Parcel ID format!");
            }
        });

        returnDialog.setVisible(true);
    }
}