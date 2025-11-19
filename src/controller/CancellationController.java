package controller;

import database.DBConnection;
import database.ParcelDatabase;
import view.CancelForm;
import view.TransactionsPanel;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class CancellationController {

    public CancellationController(TransactionsPanel tp, JFrame parent) {
        // Wire the Cancel Booking button
        tp.btnCancelBooking.addActionListener(e -> openCancelForm(parent));
    }

    public void openCancelForm(JFrame parent) {


        CancelForm form = new CancelForm(parent, "Cancel Booking");

        form.btnConfirm.addActionListener(e -> {
            String input = form.getTrackingIdText();

            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(form,
                        "Please enter a Tracking ID.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int trackingId;
            try {
                trackingId = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(form,
                        "Tracking ID must be a valid number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                try {
                    boolean success = ParcelDatabase.cancelBookingByTrackingId(conn, trackingId);

                    if (success) {
                        JOptionPane.showMessageDialog(form,
                                "Booking cancelled successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        form.dispose();
                    } else {
                        JOptionPane.showMessageDialog(form,
                                "Unable to cancel booking.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(form,
                            "Cannot cancel booking: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(form,
                        "Database connection failed.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        form.btnCancel.addActionListener(e -> {form.dispose();});

        form.setVisible(true);
    }
}
