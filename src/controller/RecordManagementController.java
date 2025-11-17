package controller;

import view.RecordManagementPanel;
import view.CustomerForm;
import view.CustomerDetailsDialog;
import database.LoadTable;
import database.DBConnection;
import database.CustomerDatabase;

import javax.swing.*;
import java.sql.*;

public class RecordManagementController {

    private RecordManagementPanel view;

    public RecordManagementController(RecordManagementPanel view) {
        this.view = view;
        initController();
        showCustomers(); // load as default table, change nalang if yall want
    }

    private void initController() {
        view.btnCustomers.addActionListener(e -> showCustomers());
        view.btnParcels.addActionListener(e -> showParcels());
        view.btnCouriers.addActionListener(e -> showCouriers());
        view.btnTracking.addActionListener(e -> showTracking());

        view.btnAdd.addActionListener(e -> addCustomer());
        view.btnEdit.addActionListener(e -> editCustomer());
        view.btnDelete.addActionListener(e -> deleteCustomer());

        // Add double-click to view customer details (Corpuz)
        view.table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double click
                    viewCustomerDetails();}
                }
         });
    }

    // show tables
    private void showCustomers() {
        view.titleLabel.setText("Customer Records");
        LoadTable.loadTable(view.table, "SELECT * FROM customers");
    }

    private void showParcels() {
        view.titleLabel.setText("Parcel Records");
        LoadTable.loadTable(view.table, "SELECT * FROM parcels");
    }

    private void showCouriers() {
        view.titleLabel.setText("Courier Records");
        LoadTable.loadTable(view.table, "SELECT * FROM couriers");
    }

    private void showTracking() {
        view.titleLabel.setText("Tracking Records");
        LoadTable.loadTable(view.table, "SELECT * FROM parcel_status ORDER BY timestamp DESC");
    }

    // controller for add button (in customer)
    private void addCustomer() {
        CustomerForm form = new CustomerForm(null, "Add Customer");

        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                // validation
                // name, address, number is required, email is optional for now
                if (form.getFirstName().isEmpty() || form.getLastName().isEmpty() ||
                    form.getAddress().isEmpty() || form.getContactNo().isEmpty()) {

                    JOptionPane.showMessageDialog(form, "Please fill in all required fields.");
                    return;
                }

                CustomerDatabase.insertCustomer(
                        conn,
                        form.getFirstName(),
                        form.getLastName(),
                        form.getAddress(),
                        form.getContactNo(),
                        form.getEmail(),
                        form.getJoinDate()
                );

                JOptionPane.showMessageDialog(form, "Customer added successfully");
                form.dispose();
                showCustomers(); 

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error adding customer");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    // edit customer
    private void editCustomer() {
        int row = view.table.getSelectedRow(); 
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a customer to edit.");
            return;
        }

        int customerId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        // pre-fill form
        CustomerForm form = new CustomerForm(null, "Edit Customer");

        form.txtFirstName.setText(view.table.getValueAt(row, 1).toString());
        form.txtLastName.setText(view.table.getValueAt(row, 2).toString());
        form.txtAddress.setText(view.table.getValueAt(row, 3).toString());
        form.txtContactNo.setText(view.table.getValueAt(row, 4).toString());
        form.txtEmail.setText(view.table.getValueAt(row, 5) != null ? view.table.getValueAt(row, 5).toString() : "");
        form.txtJoinDate.setText(view.table.getValueAt(row, 6).toString());

        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                // insert to database
                CustomerDatabase.updateCustomer(
                        conn,
                        customerId,
                        form.getFirstName(),
                        form.getLastName(),
                        form.getAddress(),
                        form.getContactNo(),
                        form.getEmail(),
                        form.getJoinDate()
                );

                JOptionPane.showMessageDialog(form, "Customer updated!");
                form.dispose();
                showCustomers();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error updating customer!");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    // delete customer
    private void deleteCustomer() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a customer to delete.");
            return;
        }

        int customerId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {

                CustomerDatabase.deleteCustomer(conn, customerId);

                JOptionPane.showMessageDialog(view, "Customer deleted");
                showCustomers();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error deleting customer");
            }
        }
    }
    // View Customer Details with Parcels (Corpuz)
    private void viewCustomerDetails() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a customer to view details.");
            return;
        }

        int customerId = Integer.parseInt(view.table.getValueAt(row, 0).toString());
        
        // Open customer details dialog
        CustomerDetailsDialog dialog = new CustomerDetailsDialog(
            (JFrame) SwingUtilities.getWindowAncestor(view), 
            customerId
        );
        dialog.setVisible(true);
    }
}
