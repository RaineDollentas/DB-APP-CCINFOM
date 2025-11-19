package controller;

import view.TransactionsPanel;
import view.DeliveryCompletionForm;
import database.DBConnection;
import database.ParcelDatabase;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliveryCompletionController {

    public DeliveryCompletionController(TransactionsPanel transactionsPanel, JFrame parentFrame) {

        transactionsPanel.btnCompleteDelivery.addActionListener(e -> openDeliveryCompletionForm(parentFrame));
    }

    private void openDeliveryCompletionForm(JFrame parentFrame) {
        DeliveryCompletionForm form = new DeliveryCompletionForm(parentFrame);

        // Auto-fill when user presses ENTER
        form.txtParcelId.addActionListener(e -> loadParcelDetails(form));

        form.btnComplete.addActionListener(e -> completeDelivery(form));
        form.btnCancel.addActionListener(e -> form.dispose());

        form.setVisible(true);
    }

    private void loadParcelDetails(DeliveryCompletionForm form) {
        int parcelId = form.getParcelId();

        if (parcelId <= 0) {
            JOptionPane.showMessageDialog(form, "Please enter a valid Parcel ID");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = ParcelDatabase.getParcelForDelivery(conn, parcelId);

            if (rs.next()) {
                String status = rs.getString("status");
                String address = rs.getString("recipient_address");

                form.setCurrentStatus(status);
                form.setRecipientAddress(address != null ? address : "Address not found");

                String normalized = status.trim().toLowerCase();

                // Only allow delivery completion when it makes sense
                if (!normalized.equals("in transit") && !normalized.equals("out for delivery")) {
                    JOptionPane.showMessageDialog(form,
                            "Cannot complete delivery.\nParcel status must be 'In Transit' or 'Out for Delivery'.\n\nCurrent Status: "
                                    + status);
                }

            } else {
                JOptionPane.showMessageDialog(form, "Parcel not found!");
                form.setCurrentStatus("Not Found");
                form.setRecipientAddress("");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(form, "Error loading parcel details: " + ex.getMessage());
        }
    }

    private void completeDelivery(DeliveryCompletionForm form) {
        int parcelId = form.getParcelId();
        String remarks = form.getRemarks();

        if (parcelId <= 0) {
            JOptionPane.showMessageDialog(form, "Please enter a valid Parcel ID");
            return;
        }

        if (remarks.isEmpty()) {
            remarks = "Delivery completed successfully";
        }

        int confirm = JOptionPane.showConfirmDialog(form,
                "Are you sure you want to mark parcel #" + parcelId + " as delivered?",
                "Confirm Delivery Completion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                boolean success = ParcelDatabase.completeDelivery(conn, parcelId, remarks);

                if (success) {
                    JOptionPane.showMessageDialog(form,
                            "Delivery completed successfully!\nParcel #" + parcelId + " has been marked as delivered.");
                    form.dispose();
                } else {
                    JOptionPane.showMessageDialog(form, "Failed to complete delivery.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error completing delivery: " + ex.getMessage());
            }
        }
    }
}
