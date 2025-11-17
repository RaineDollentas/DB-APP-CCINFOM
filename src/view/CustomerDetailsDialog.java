package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DBConnection;
import database.CustomerDatabase;

public class CustomerDetailsDialog extends JDialog {

    private JTable parcelsTable;
    private JLabel customerInfoLabel;

    public CustomerDetailsDialog(JFrame parentFrame, int customerId) {
        super(parentFrame, "Customer Details", true);
        setSize(800, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));

        // Customer Information Panel
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        customerPanel.setBackground(Color.WHITE);

        customerInfoLabel = new JLabel();
        customerInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        customerPanel.add(customerInfoLabel, BorderLayout.NORTH);

        add(customerPanel, BorderLayout.NORTH);

        // Parcels Table
        String[] columnNames = {"Parcel ID", "Status", "Booking Date", "Courier"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        parcelsTable = new JTable(model);
        parcelsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScroll = new JScrollPane(parcelsTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Parcel History"));

        add(tableScroll, BorderLayout.CENTER);

        // Close Button
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCustomerData(customerId);
    }

    private void loadCustomerData(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = CustomerDatabase.getCustomerWithParcels(conn, customerId);

            DefaultTableModel model = (DefaultTableModel) parcelsTable.getModel();
            model.setRowCount(0); // Clear existing data

            boolean hasCustomer = false;

            while (rs.next()) {
                if (!hasCustomer) {
                    // Set customer info (first row only)
                    String customerInfo = String.format(
                            "Customer: %s %s | Address: %s | Contact: %s | Email: %s | Member Since: %s",
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("address"),
                            rs.getString("contact_no"),
                            rs.getString("email"),
                            rs.getDate("join_date")
                    );
                    customerInfoLabel.setText(customerInfo);
                    hasCustomer = true;
                }

                // Add parcel data
                Object[] rowData = {
                        rs.getInt("parcel_id"),
                        rs.getString("status"),
                        rs.getTimestamp("booking_date"),
                        rs.getString("courier_first_name") + " " + rs.getString("courier_last_name")
                };
                model.addRow(rowData);
            }

            if (!hasCustomer) {
                customerInfoLabel.setText("Customer not found!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer data: " + ex.getMessage());
        }
    }
}