package controller;

import view.RecordManagementPanel;
import view.CustomerForm;
import view.CourierForm;
import database.LoadTable;
import database.DBConnection;
import database.CustomerDatabase;
import database.CourierDatabase;

import javax.swing.*;
import java.sql.*;

public class RecordManagementController {

    private RecordManagementPanel view;
    private String currentTable;

    public RecordManagementController(RecordManagementPanel view) {
        this.view = view;
        initController();
        showCustomers(); // load as default table
    }

    private void initController() {
        view.btnCustomers.addActionListener(e -> showCustomers());
        view.btnParcels.addActionListener(e -> showParcels());
        view.btnCouriers.addActionListener(e -> showCouriers());
        view.btnTracking.addActionListener(e -> showTracking());

        view.btnAdd.addActionListener(e -> addRecord());
        view.btnEdit.addActionListener(e -> editRecord());
        view.btnDelete.addActionListener(e -> deleteRecord());
    }

    // Show tables
    private void showCustomers() {
        currentTable = "customers";
        view.titleLabel.setText("Customer Records");
        LoadTable.loadTable(view.table, "SELECT * FROM customers");
    }

    private void showParcels() {
        currentTable = "parcels";
        view.titleLabel.setText("Parcel Records");
        LoadTable.loadTable(view.table, "SELECT * FROM parcels");
    }

    private void showCouriers() {
        currentTable = "couriers";
        view.titleLabel.setText("Courier Records");
        LoadTable.loadTable(view.table, "SELECT * FROM couriers");
    }

    private void showTracking() {
        currentTable = "tracking";
        view.titleLabel.setText("Tracking Records");
        LoadTable.loadTable(view.table, "SELECT * FROM parcel_status ORDER BY timestamp DESC");
    }

    // Add record based on current table
    private void addRecord() {
        switch (currentTable) {
            case "customers":
                addCustomer();
                break;
            case "couriers":
                addCourier();
                break;
            case "parcels":
            case "tracking":
                JOptionPane.showMessageDialog(view, "Use transactions to add parcels and tracking records.");
                break;
            default:
                JOptionPane.showMessageDialog(view, "Select a valid table to add records.");
        }
    }

    // Edit record based on current table
    private void editRecord() {
        switch (currentTable) {
            case "customers":
                editCustomer();
                break;
            case "couriers":
                editCourier();
                break;
            case "parcels":
            case "tracking":
                JOptionPane.showMessageDialog(view, "Editing not supported for this table.");
                break;
            default:
                JOptionPane.showMessageDialog(view, "Select a valid record to edit.");
        }
    }

    // Delete record based on current table
    private void deleteRecord() {
        switch (currentTable) {
            case "customers":
                deleteCustomer();
                break;
            case "couriers":
                deleteCourier();
                break;
            case "parcels":
            case "tracking":
                JOptionPane.showMessageDialog(view, "Deletion not supported for this table.");
                break;
            default:
                JOptionPane.showMessageDialog(view, "Select a valid record to delete.");
        }
    }

    // Customer methods (existing)
    private void addCustomer() {
        CustomerForm form = new CustomerForm(null, "Add Customer");
        setupCustomerForm(form, false, -1);
    }

    private void editCustomer() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a customer to edit.");
            return;
        }
        int customerId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        CustomerForm form = new CustomerForm(null, "Edit Customer");
        form.txtFirstName.setText(view.table.getValueAt(row, 1).toString());
        form.txtLastName.setText(view.table.getValueAt(row, 2).toString());
        form.txtAddress.setText(view.table.getValueAt(row, 3).toString());
        form.txtContactNo.setText(view.table.getValueAt(row, 4).toString());
        form.txtEmail.setText(view.table.getValueAt(row, 5) != null ? view.table.getValueAt(row, 5).toString() : "");
        form.txtJoinDate.setText(view.table.getValueAt(row, 6).toString());

        setupCustomerForm(form, true, customerId);
    }

    private void setupCustomerForm(CustomerForm form, boolean isEdit, int customerId) {
        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                if (form.getFirstName().isEmpty() || form.getLastName().isEmpty() ||
                        form.getAddress().isEmpty() || form.getContactNo().isEmpty()) {
                    JOptionPane.showMessageDialog(form, "Please fill in all required fields.");
                    return;
                }

                if (isEdit) {
                    CustomerDatabase.updateCustomer(
                            conn, customerId, form.getFirstName(), form.getLastName(),
                            form.getAddress(), form.getContactNo(), form.getEmail(), form.getJoinDate()
                    );
                    JOptionPane.showMessageDialog(form, "Customer updated successfully!");
                } else {
                    CustomerDatabase.insertCustomer(
                            conn, form.getFirstName(), form.getLastName(), form.getAddress(),
                            form.getContactNo(), form.getEmail(), form.getJoinDate()
                    );
                    JOptionPane.showMessageDialog(form, "Customer added successfully!");
                }

                form.dispose();
                showCustomers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error saving customer!");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    private void deleteCustomer() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a customer to delete.");
            return;
        }
        int customerId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this customer?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                CustomerDatabase.deleteCustomer(conn, customerId);
                JOptionPane.showMessageDialog(view, "Customer deleted successfully!");
                showCustomers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error deleting customer!");
            }
        }
    }

    /**
     * NEW: PLEASE APPEND TNX
     * COURIER METHODS
     * **/
    private void addCourier() {
        CourierForm form = new CourierForm(null, "Add Courier", false);
        setupCourierForm(form, false, -1);
    }

    private void editCourier() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a courier to edit.");
            return;
        }
        int courierId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        CourierForm form = new CourierForm(null, "Edit Courier", true);
        form.setCourierId(courierId);
        form.setFirstName(view.table.getValueAt(row, 2).toString()); // first_name is column 2
        form.setLastName(view.table.getValueAt(row, 1).toString());  // last_name is column 1
        form.setVehicleType(view.table.getValueAt(row, 3).toString());
        form.setEmail(view.table.getValueAt(row, 5) != null ? view.table.getValueAt(row, 5).toString() : "");
        form.setContactNo(view.table.getValueAt(row, 6) != null ? view.table.getValueAt(row, 6).toString() : "");
        form.setHireDate(Date.valueOf(view.table.getValueAt(row, 4).toString()));

        setupCourierForm(form, true, courierId);
    }

    private void setupCourierForm(CourierForm form, boolean isEdit, int courierId) {
        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                if (form.getCourierId() == -1 || form.getFirstName().isEmpty() ||
                        form.getLastName().isEmpty() || form.getContactNo().isEmpty()) {
                    JOptionPane.showMessageDialog(form, "Please fill in all required fields.");
                    return;
                }

                if (isEdit) {
                    CourierDatabase.updateCourier(
                            conn, courierId, form.getFirstName(), form.getLastName(),
                            form.getVehicleType(), form.getEmail(), form.getContactNo()
                    );
                    JOptionPane.showMessageDialog(form, "Courier updated successfully!");
                } else {
                    // Check if courier ID already exists
                    if (CourierDatabase.courierExists(conn, form.getCourierId())) {
                        JOptionPane.showMessageDialog(form, "Courier ID already exists!");
                        return;
                    }
                    CourierDatabase.insertCourier(
                            conn, form.getCourierId(), form.getFirstName(), form.getLastName(),
                            form.getVehicleType(), form.getEmail(), form.getContactNo()
                    );
                    JOptionPane.showMessageDialog(form, "Courier added successfully!");
                }

                form.dispose();
                showCouriers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error saving courier!");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    private void deleteCourier() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Please select a courier to delete.");
            return;
        }
        int courierId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this courier?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                CourierDatabase.deleteCourier(conn, courierId);
                JOptionPane.showMessageDialog(view, "Courier deleted successfully!");
                showCouriers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error deleting courier!");
            }
        }
    }
}