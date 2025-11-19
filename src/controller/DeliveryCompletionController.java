package controller;

import view.TransactionsPanel;
import view.DeliveryCompletionForm;
import database.DBConnection;
import database.ParcelDatabase;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliveryCompletionController {

    private TransactionsPanel view;
    private DeliveryCompletionForm currentForm = null;

    public DeliveryCompletionController(TransactionsPanel transactionsPanel, JFrame parentFrame) {
        this.view = transactionsPanel;
        initController();
    }

    private void initController() {
        // Remove any existing listeners first to prevent duplicates
        for (var listener : view.btnCompleteDelivery.getActionListeners()) {
            view.btnCompleteDelivery.removeActionListener(listener);
        }

        // Add our listener
        view.btnCompleteDelivery.addActionListener(e -> openDeliveryCompletionForm());
    }

    private void openDeliveryCompletionForm() {
        // If form already exists and is visible, just bring it to front
        if (currentForm != null && currentForm.isVisible()) {
            currentForm.toFront();
            return;
        }

        // Create new form
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        currentForm = new DeliveryCompletionForm(parentFrame);

        // Add window listener to handle closing properly
        currentForm.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                currentForm = null;
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                currentForm = null;
            }
        });

        // Set up form buttons (do this ONLY once)
        setupFormActions(currentForm);

        currentForm.setVisible(true);
    }

    private void setupFormActions(DeliveryCompletionForm form) {
        // Clear existing listeners first
        for (var listener : form.txtParcelId.getActionListeners()) {
            form.txtParcelId.removeActionListener(listener);
        }
        for (var listener : form.btnComplete.getActionListeners()) {
            form.btnComplete.removeActionListener(listener);
        }
        for (var listener : form.btnCancel.getActionListeners()) {
            form.btnCancel.removeActionListener(listener);
        }

        // When parcel ID is entered, auto-fill details
        form.txtParcelId.addActionListener(e -> loadParcelDetails(form));

        // Handle save button click
        form.btnComplete.addActionListener(e -> completeDelivery(form));

        // Handle cancel button
        form.btnCancel.addActionListener(e -> form.dispose());
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

                // Check if parcel can be delivered
                if (!"In Transit".equalsIgnoreCase(status) && !"Out for Delivery".equalsIgnoreCase(status)) {
                    JOptionPane.showMessageDialog(form,
                            "Cannot complete delivery. Parcel status must be 'In Transit' or 'Out for Delivery'. Current status: " + status);
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
