package controller;

import view.RecordManagementPanel;
import view.SortDialog;
import view.CustomerForm;
import view.FilterDialog;
import view.CourierForm;
import view.ParcelForm;
import view.CustomerDetailsDialog;
import database.LoadTable;
import database.DBConnection;
import database.CustomerDatabase;
import database.CourierDatabase;
import database.ParcelDatabase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.*;

public class RecordManagementController {

    private RecordManagementPanel view;
    private String currentTable;

    public RecordManagementController(RecordManagementPanel view) {
        this.view = view;
        initController();
        showCustomers();
    }

    private void initController() {

        // navigation
        view.btnCustomers.addActionListener(e -> showCustomers());
        view.btnParcels.addActionListener(e -> showParcels());
        view.btnCouriers.addActionListener(e -> showCouriers());
        view.btnTracking.addActionListener(e -> showTracking());

        // CRUD buttons
        view.btnAdd.addActionListener(e -> addRecord());
        view.btnEdit.addActionListener(e -> editRecord());
        view.btnDelete.addActionListener(e -> deleteRecord());

        // tracking buttons
        view.btnFilter.addActionListener(e -> {
            if (currentTable.equals("tracking")) openFilterDialog();
        });
        view.btnSort.addActionListener(e -> {
            if (currentTable.equals("tracking")) openSortDialog();
        });
        view.btnRefresh.addActionListener(e -> {
            if (currentTable.equals("tracking")) showTracking();
        });

        // search bar live filter (tracking only)
        view.txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(); }
            public void removeUpdate(DocumentEvent e) { applySearch(); }
            public void changedUpdate(DocumentEvent e) { applySearch(); }
        });

        // double-click for customer details
        view.table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && "customers".equals(currentTable)) {
                    viewCustomerDetails();
                }
            }
        });
    }

    // button mode switching

    private void showCRUDButtons() {
        view.btnAdd.setVisible(true);
        view.btnEdit.setVisible(true);
        view.btnDelete.setVisible(true);

        view.txtSearch.setVisible(false);
        view.btnFilter.setVisible(false);
        view.btnSort.setVisible(false);
        view.btnRefresh.setVisible(false);
    }

    private void showTrackingButtons() {
        view.btnAdd.setVisible(false);
        view.btnEdit.setVisible(false);
        view.btnDelete.setVisible(false);

        view.txtSearch.setVisible(true);
        view.btnFilter.setVisible(true);
        view.btnSort.setVisible(true);
        view.btnRefresh.setVisible(true);
    }

    // table loaders

    private void showCustomers() {
        currentTable = "customers";
        view.titleLabel.setText("Customer Records");
        LoadTable.loadTable(view.table, "SELECT * FROM customers");
        showCRUDButtons();
    }

    private void showParcels() {
        currentTable = "parcels";
        view.titleLabel.setText("Parcel Records");
        LoadTable.loadTable(view.table, "SELECT * FROM parcels");
        showCRUDButtons();
    }

    private void showCouriers() {
        currentTable = "couriers";
        view.titleLabel.setText("Courier Records");
        LoadTable.loadTable(view.table, "SELECT * FROM couriers");
        showCRUDButtons();
    }

    private void showTracking() {
        currentTable = "tracking";
        view.titleLabel.setText("Tracking Records");
        LoadTable.loadTable(view.table, "SELECT * FROM parcel_status ORDER BY timestamp DESC");
        showTrackingButtons();
    }

    // routers

    private void addRecord() {
        switch (currentTable) {
            case "customers": addCustomer(); break;
            case "couriers": addCourier(); break;
            case "parcels": addParcel(); break;
            default:
                JOptionPane.showMessageDialog(view, "Cannot add records to this table.");
        }
    }

    private void editRecord() {
        switch (currentTable) {
            case "customers": editCustomer(); break;
            case "couriers": editCourier(); break;
            case "parcels": editParcel(); break;
            default:
                JOptionPane.showMessageDialog(view, "Cannot edit records in this table.");
        }
    }

    private void deleteRecord() {
        switch (currentTable) {
            case "customers": deleteCustomer(); break;
            case "couriers": deleteCourier(); break;
            case "parcels": deleteParcel(); break;
            default:
                JOptionPane.showMessageDialog(view, "Cannot delete records in this table.");
        }
    }

    // customer CRUD

    private void addCustomer() {
        CustomerForm form = new CustomerForm(null, "Add Customer");
        setupCustomerForm(form, false, -1);
    }

    private void editCustomer() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a customer to edit.");
            return;
        }

        int id = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        CustomerForm form = new CustomerForm(null, "Edit Customer");
        form.txtFirstName.setText(view.table.getValueAt(row, 1).toString());
        form.txtLastName.setText(view.table.getValueAt(row, 2).toString());
        form.txtAddress.setText(view.table.getValueAt(row, 3).toString());
        form.txtContactNo.setText(view.table.getValueAt(row, 4).toString());
        form.txtEmail.setText(view.table.getValueAt(row, 5) != null ? view.table.getValueAt(row, 5).toString() : "");
        form.txtJoinDate.setText(view.table.getValueAt(row, 6).toString());

        setupCustomerForm(form, true, id);
    }

    private void setupCustomerForm(CustomerForm form, boolean isEdit, int id) {

        form.btnSave.addActionListener(e -> {

            if (form.getFirstName().isEmpty() || form.getLastName().isEmpty() ||
                    form.getAddress().isEmpty() || form.getContactNo().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Please fill in all required fields.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                if (isEdit) {
                    CustomerDatabase.updateCustomer(conn, id,
                            form.getFirstName(), form.getLastName(),
                            form.getAddress(), form.getContactNo(),
                            form.getEmail(), form.getJoinDate());
                    JOptionPane.showMessageDialog(form, "Customer updated!");
                } else {
                    CustomerDatabase.insertCustomer(conn,
                            form.getFirstName(), form.getLastName(),
                            form.getAddress(), form.getContactNo(),
                            form.getEmail(), form.getJoinDate());
                    JOptionPane.showMessageDialog(form, "Customer added!");
                }

                form.dispose();
                showCustomers();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error saving customer.");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    private void deleteCustomer() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a customer to delete.");
            return;
        }

        int id = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete this customer?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            CustomerDatabase.deleteCustomer(conn, id);
            JOptionPane.showMessageDialog(view, "Customer deleted!");
            showCustomers();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error deleting customer.");
        }
    }

    // courier CRUD

    private void addCourier() {
        CourierForm form = new CourierForm(null, "Add Courier", false);
        setupCourierForm(form, false, -1);
    }

    private void editCourier() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a courier to edit.");
            return;
        }

        int id = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        CourierForm form = new CourierForm(null, "Edit Courier", true);
        form.setCourierId(id);

        form.setLastName(view.table.getValueAt(row, 1).toString());
        form.setFirstName(view.table.getValueAt(row, 2).toString());
        form.setVehicleType(view.table.getValueAt(row, 3).toString());
        form.setHireDate(Date.valueOf(view.table.getValueAt(row, 4).toString()));
        form.setEmail(view.table.getValueAt(row, 5) != null ? view.table.getValueAt(row, 5).toString() : "");
        form.setContactNo(view.table.getValueAt(row, 6) != null ? view.table.getValueAt(row, 6).toString() : "");

        setupCourierForm(form, true, id);
    }

    private void setupCourierForm(CourierForm form, boolean isEdit, int id) {

        form.btnSave.addActionListener(e -> {

            if (form.getFirstName().isEmpty() || form.getLastName().isEmpty() ||
                    form.getContactNo().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Please fill in all required fields.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                if (isEdit) {
                    CourierDatabase.updateCourier(conn, id,
                            form.getFirstName(), form.getLastName(),
                            form.getVehicleType(), form.getEmail(), form.getContactNo());
                    JOptionPane.showMessageDialog(form, "Courier updated!");
                } else {
                    if (CourierDatabase.courierExists(conn, form.getCourierId())) {
                        JOptionPane.showMessageDialog(form, "Courier ID already exists!");
                        return;
                    }

                    CourierDatabase.insertCourier(conn,
                            form.getCourierId(), form.getFirstName(), form.getLastName(),
                            form.getVehicleType(), form.getEmail(), form.getContactNo());
                    JOptionPane.showMessageDialog(form, "Courier added!");
                }

                form.dispose();
                showCouriers();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error saving courier.");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    private void deleteCourier() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a courier to delete.");
            return;
        }

        int id = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete this courier?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            CourierDatabase.deleteCourier(conn, id);
            JOptionPane.showMessageDialog(view, "Courier deleted!");
            showCouriers();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error deleting courier.");
        }
    }

    // parcel CRUD

    private void addParcel() {
        ParcelForm form = new ParcelForm(null, "Add Parcel", false);

        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                int customerId = form.getCustomerId();
                int courierId = form.getCourierId();
                String status = form.getStatus();

                if (!ParcelDatabase.customerExists(conn, customerId)) {
                    JOptionPane.showMessageDialog(form, "Invalid Customer ID!");
                    return;
                }

                if (!ParcelDatabase.courierExists(conn, courierId)) {
                    JOptionPane.showMessageDialog(form, "Invalid Courier ID!");
                    return;
                }

                int parcelId = ParcelDatabase.insertParcel(conn, customerId, courierId, status);

                if (parcelId != -1) {
                    JOptionPane.showMessageDialog(form, "Parcel added! ID: " + parcelId);
                    form.dispose();
                    showParcels();
                } else {
                    JOptionPane.showMessageDialog(form, "Failed to add parcel.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error adding parcel.");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    private void editParcel() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a parcel to edit.");
            return;
        }

        int parcelId = Integer.parseInt(view.table.getValueAt(row, 0).toString());
        int customerId = Integer.parseInt(view.table.getValueAt(row, 1).toString());
        int courierId = Integer.parseInt(view.table.getValueAt(row, 2).toString());
        String status = view.table.getValueAt(row, 3).toString();

        ParcelForm form = new ParcelForm(null, "Edit Parcel", true);
        form.setParcelId(parcelId);
        form.setCustomerId(customerId);
        form.setCourierId(courierId);
        form.setStatus(status);

        form.btnSave.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                int newCustomerId = form.getCustomerId();
                int newCourierId = form.getCourierId();
                String newStatus = form.getStatus();

                if (!ParcelDatabase.customerExists(conn, newCustomerId)) {
                    JOptionPane.showMessageDialog(form, "Invalid Customer ID!");
                    return;
                }

                if (!ParcelDatabase.courierExists(conn, newCourierId)) {
                    JOptionPane.showMessageDialog(form, "Invalid Courier ID!");
                    return;
                }

                ParcelDatabase.updateParcel(conn, parcelId, newCustomerId, newCourierId, newStatus);

                JOptionPane.showMessageDialog(form, "Parcel updated!");
                form.dispose();
                showParcels();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(form, "Error updating parcel.");
            }
        });

        form.btnCancel.addActionListener(e -> form.dispose());
        form.setVisible(true);
    }

    private void deleteParcel() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a parcel to delete.");
            return;
        }

        int parcelId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete this parcel?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            ParcelDatabase.deleteParcel(conn, parcelId);
            JOptionPane.showMessageDialog(view, "Parcel deleted!");
            showParcels();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error deleting parcel.");
        }
    }

    // tracking: filter / sort / search

    private void openFilterDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        FilterDialog dlg = new FilterDialog(parent);

        dlg.btnApply.addActionListener(e -> {

            StringBuilder query = new StringBuilder("SELECT * FROM parcel_status WHERE 1=1");

            String courierText = dlg.txtCourierId.getText().trim();
            if (!courierText.isEmpty()) {
                query.append(" AND courier_id = ").append(courierText);
            }

            String status = dlg.cbStatus.getSelectedItem().toString();
            if (!"All".equals(status)) {
                query.append(" AND status_update LIKE '%").append(status).append("%'");
            }

            java.util.Date from = (java.util.Date) dlg.dateFrom.getValue();
            java.util.Date to = (java.util.Date) dlg.dateTo.getValue();

            java.sql.Timestamp fromTs = new java.sql.Timestamp(from.getTime());
            java.sql.Timestamp toTs = new java.sql.Timestamp(to.getTime());

            query.append(" AND timestamp BETWEEN '")
                    .append(fromTs)
                    .append("' AND '")
                    .append(toTs)
                    .append("'");

            query.append(" ORDER BY timestamp DESC");

            LoadTable.loadTable(view.table, query.toString());
            dlg.dispose();
        });

        dlg.btnCancel.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void openSortDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        SortDialog dlg = new SortDialog(parent);

        dlg.btnApply.addActionListener(e -> {
            String field = dlg.cbSortField.getSelectedItem().toString();
            String order = dlg.cbOrder.getSelectedItem().toString();

            String query = "SELECT * FROM parcel_status ORDER BY " + field + " " + order;
            LoadTable.loadTable(view.table, query);

            dlg.dispose();
        });

        dlg.btnCancel.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void applySearch() {
        if (!"tracking".equals(currentTable)) return;

        String text = view.txtSearch.getText().trim();

        if (text.isEmpty()) {
        
            LoadTable.loadTable(view.table, "SELECT * FROM parcel_status ORDER BY timestamp DESC");
            return;
        }

        String query = "SELECT * FROM parcel_status WHERE " +
                "CAST(parcel_id AS CHAR) LIKE '%" + text + "%' OR " +
                "CAST(courier_id AS CHAR) LIKE '%" + text + "%' OR " +
                "status_update LIKE '%" + text + "%' OR " +
                "recipient_address LIKE '%" + text + "%' " +
                "ORDER BY timestamp DESC";

        LoadTable.loadTable(view.table, query);
    }

    // customer details

    private void viewCustomerDetails() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Select a customer first.");
            return;
        }

        int customerId = Integer.parseInt(view.table.getValueAt(row, 0).toString());

        CustomerDetailsDialog dialog = new CustomerDetailsDialog(
                (JFrame) SwingUtilities.getWindowAncestor(view),
                customerId
        );
        dialog.setVisible(true);
    }
}
