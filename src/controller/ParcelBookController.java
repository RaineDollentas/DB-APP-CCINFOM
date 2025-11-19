package controller;

import view.BookParcelForm;
import view.TransactionsPanel;
import database.DBConnection;
import database.ParcelDatabase;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

// for bookparcel form
public class ParcelBookController {

    public ParcelBookController(TransactionsPanel transactionsPanel,
                                JPanel mainPanel,
                                CardLayout cardLayout,
                                JFrame parentFrame) {

        // connection to transactions panel
        transactionsPanel.btnBookParcel.addActionListener(e -> openBookingForm(parentFrame));
    }

    // opens the booking form dialog
    private void openBookingForm(JFrame parentFrame) {

        // form title
        BookParcelForm form = new BookParcelForm(parentFrame, "Book Parcel");

        // connect to db and get couriers data
        try (Connection conn = DBConnection.getConnection()) {
            for (String courier : ParcelDatabase.getCouriers(conn)) {
                form.cbCouriers.addItem(courier);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(form, "Error loading couriers");
        }

        // save button action
        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                // get customer ID from data, error if no matching customer ID
                int customerId;
                try {
                    customerId = form.getCustomerId();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(form, "Invalid Customer ID.");
                    return;
                }
                if (!ParcelDatabase.customerExists(conn, customerId)) {
                    JOptionPane.showMessageDialog(form, "Customer does not exist.");
                    return;
                }


                // auto fill pick up address > pu address is customers address, change this nalang if wrong
                String pickupAddress = ParcelDatabase.getCustomerAddress(conn, customerId);
                form.txtPickupAddress.setText(pickupAddress);

                // for recipient address, must not be empty
                if (form.getRecipientAddress().isEmpty()) {
                    JOptionPane.showMessageDialog(form, "Recipient address is required.");
                    return;
                }

                // parse courier
                String selected = form.cbCouriers.getSelectedItem().toString();
                int courierId = Integer.parseInt(selected.split(" - ")[0]);

                // insert parcel
                int parcelId = ParcelDatabase.insertParcel(conn, customerId, courierId, "Booked");

                if (parcelId == -1) {
                    JOptionPane.showMessageDialog(form, "Error saving parcel.");
                    return;
                }

                // insert parcel status
                ParcelDatabase.insertParcelStatus(
                        conn,
                        parcelId,
                        courierId,
                        form.getRecipientAddress()
                );

                JOptionPane.showMessageDialog(form,
                        "Parcel booked! New Parcel ID: " + parcelId);

                form.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Database error.");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());

        form.setVisible(true);
    }
}
