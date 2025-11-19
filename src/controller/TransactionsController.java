package controller;

import view.TransactionsPanel;
import database.ParcelReturn;

import javax.swing.*;
import java.awt.*;

public class TransactionsController {

    private final ParcelReturn parcelReturn;
    private final JFrame parentFrame;

    public TransactionsController(TransactionsPanel view, JPanel mainPanel, CardLayout cardLayout) {

        this.parcelReturn = new ParcelReturn();

        // Get the parent frame safely
        this.parentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);

        // ==== BOOK PARCEL ====
        view.btnBookParcel.addActionListener(e -> {
            // handled externally
        });

        // ==== COMPLETE DELIVERY (now real form) ====
        view.btnCompleteDelivery.addActionListener(e -> {
            DeliveryCompletionController dcc = new DeliveryCompletionController(view, parentFrame);
        });

        // ==== CANCEL BOOKING (uses your cancellation controller) ====
        view.btnCancelBooking.addActionListener(e -> {
            CancellationController cc = new CancellationController(view, parentFrame);
        });

        // ==== PARCEL RETURN ====
        view.btnParcelReturn.addActionListener(e -> showParcelReturnDialog());
    }

    // ---------------------------------------------------------------------------
    //  PARCEL RETURN DIALOG (your existing working return process)
    // ---------------------------------------------------------------------------

    private void showParcelReturnDialog() {
        JDialog returnDialog = new JDialog(parentFrame, "Parcel Return Management", true);
        returnDialog.setSize(500, 400);
        returnDialog.setLocationRelativeTo(parentFrame);
        returnDialog.setLayout(new BorderLayout());

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

        // Buttons
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

        // Add to dialog
        returnDialog.add(mainPanel, BorderLayout.NORTH);
        returnDialog.add(buttonPanel, BorderLayout.CENTER);
        returnDialog.add(resultScroll, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====

        btnCheckParcel.addActionListener(e -> {
            String parcelIdStr = txtParcelId.getText().trim();
            if (parcelIdStr.isEmpty()) {
                resultArea.setText("Please enter a Parcel ID.");
                return;
            }

            try {
                int parcelId = Integer.parseInt(parcelIdStr);
                String details = parcelReturn.getParcelDetailsForReturn(parcelId);

                resultArea.setText(
                        details != null
                                ? "Parcel Details:\n" + details
                                : "Parcel not found or error retrieving details."
                );

            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid Parcel ID format!");
            }
        });

        btnInitiateReturn.addActionListener(e -> {
            String parcelIdStr = txtParcelId.getText().trim();
            String courierIdStr = txtReturnCourierId.getText().trim();
            String reason = cbReturnReason.getSelectedItem().toString();

            if (parcelIdStr.isEmpty() || courierIdStr.isEmpty()) {
                resultArea.setText("Both Parcel ID and Courier ID are required.");
                return;
            }

            try {
                int parcelId = Integer.parseInt(parcelIdStr);
                int courierId = Integer.parseInt(courierIdStr);

                int result = parcelReturn.initiateParcelReturn(parcelId, courierId, reason);

                resultArea.setText(
                        (result == 1)
                                ? "SUCCESS: Return initiated.\nCourier: " + courierId + "\nReason: " + reason
                                : "FAILED: Could not initiate return."
                );

            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid ID format!");
            }
        });

        btnCompleteReturn.addActionListener(e -> {
            String parcelIdStr = txtParcelId.getText().trim();
            String remarks = txtRemarks.getText().trim();

            if (parcelIdStr.isEmpty()) {
                resultArea.setText("Enter Parcel ID.");
                return;
            }

            if (remarks.isEmpty()) remarks = "Return completed successfully";

            try {
                int parcelId = Integer.parseInt(parcelIdStr);
                int result = parcelReturn.completeParcelReturn(parcelId, remarks);

                resultArea.setText(
                        (result == 1)
                                ? "SUCCESS: Return completed.\nRemarks: " + remarks
                                : "FAILED: Could not complete return."
                );

            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid Parcel ID format!");
            }
        });

        returnDialog.setVisible(true);
    }
}
