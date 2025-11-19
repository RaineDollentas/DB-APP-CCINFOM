package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DBConnection;
import database.CourierReportDatabase;
import database.LoadTable;

public class CourierPerformanceReportForm extends JDialog {

    private JComboBox<String> cbPeriod;
    private JComboBox<Integer> cbYear;
    private JComboBox<String> cbMonth;
    private JTextField txtDate;
    private JButton btnGenerate;
    private JButton btnExport;
    private JButton btnClose;
    private JTable reportTable;
    private JTextArea summaryArea;

    public CourierPerformanceReportForm(JFrame parentFrame) {
        super(parentFrame, "Courier Performance Report", true);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadInitialData();
    }

    private void initializeComponents() {
        String[] periods = {"Daily", "Monthly", "Yearly"};
        cbPeriod = new JComboBox<>(periods);

        cbYear = new JComboBox<>();

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        cbMonth = new JComboBox<>(months);
        cbMonth.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);

        txtDate = new JTextField(java.time.LocalDate.now().toString());

        btnGenerate = new JButton("Generate Report");
        btnExport = new JButton("Export to CSV");
        btnClose = new JButton("Close");

        reportTable = new JTable();

        summaryArea = new JTextArea(5, 40);
        summaryArea.setEditable(false);
        summaryArea.setBackground(new Color(240, 240, 240));
        summaryArea.setBorder(BorderFactory.createTitledBorder("Performance Summary"));
    }

    private void setupLayout() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(true);

        JPanel controlPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        controlPanel.add(new JLabel("Report Period:"));
        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(new JLabel("Date (YYYY-MM-DD):"));

        controlPanel.add(cbPeriod);
        controlPanel.add(cbYear);
        controlPanel.add(cbMonth);
        controlPanel.add(txtDate);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnClose);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(controlPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateControlVisibility();
    }

    private void setupEventListeners() {
        cbPeriod.addActionListener(e -> updateControlVisibility());

        btnGenerate.addActionListener(e -> generateReport());

        btnExport.addActionListener(e -> exportToCSV());

        btnClose.addActionListener(e -> dispose());

        /** -----------------------------
         * FIX: Date picker now activates ONLY on click,
         *      NOT on focus.
         * ----------------------------- */
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showDatePicker();
            }
        });
    }

    private void loadInitialData() {
        try (Connection conn = DBConnection.getConnection()) {
            for (int year : database.CourierReportDatabase.getAvailableYears(conn)) {
                cbYear.addItem(year);
            }
            cbYear.setSelectedItem(java.time.LocalDate.now().getYear());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading years: " + ex.getMessage());
        }
    }

    private void updateControlVisibility() {
        String period = (String) cbPeriod.getSelectedItem();

        switch (period) {
            case "Daily":
                txtDate.setEnabled(true);
                cbMonth.setEnabled(false);
                break;
            case "Monthly":
                txtDate.setEnabled(false);
                cbMonth.setEnabled(true);
                break;
            case "Yearly":
                txtDate.setEnabled(false);
                cbMonth.setEnabled(false);
                break;
        }
    }

    private void generateReport() {
        String period = (String) cbPeriod.getSelectedItem();
        Integer year = (Integer) cbYear.getSelectedItem();

        if (year == null) {
            JOptionPane.showMessageDialog(this, "Please select a year.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = buildQuery(period, year);
            LoadTable.loadTable(reportTable, query);

            String summary = generateSummary(conn, period, year);
            summaryArea.setText(summary);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String buildQuery(String period, int year) {
        int month = cbMonth.getSelectedIndex() + 1;
        String date = txtDate.getText().trim();

        switch (period) {
            case "Daily":
                return String.format(
                        "SELECT c.courier_id AS 'Courier ID', " +
                                "CONCAT(c.first_name, ' ', c.last_name) AS 'Courier Name', " +
                                "c.vehicle_type AS 'Vehicle Type', " +
                                "COUNT(ps.parcel_id) AS 'Total Deliveries', " +
                                "SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) AS 'Successful', " +
                                "SUM(CASE WHEN ps.status_update IN ('Cancelled','Returned','Failed') THEN 1 ELSE 0 END) AS 'Unsuccessful', " +
                                "ROUND((SUM(CASE WHEN ps.status_update='Delivered' THEN 1 ELSE 0 END)/COUNT(ps.parcel_id)*100),2) AS 'Success Rate %%' " +
                                "FROM parcel_status ps " +
                                "JOIN couriers c ON ps.courier_id=c.courier_id " +
                                "WHERE DATE(ps.timestamp)='%s' " +
                                "GROUP BY c.courier_id ORDER BY `Success Rate %%` DESC",
                        date);

            case "Monthly":
                return String.format(
                        "SELECT c.courier_id AS 'Courier ID', " +
                                "CONCAT(c.first_name, ' ', c.last_name) AS 'Courier Name', " +
                                "c.vehicle_type AS 'Vehicle Type', " +
                                "COUNT(ps.parcel_id) AS 'Total Deliveries', " +
                                "SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) AS 'Successful', " +
                                "SUM(CASE WHEN ps.status_update IN ('Cancelled','Returned','Failed') THEN 1 ELSE 0 END) AS 'Unsuccessful', " +
                                "ROUND((SUM(CASE WHEN ps.status_update='Delivered' THEN 1 ELSE 0 END)/COUNT(ps.parcel_id)*100),2) AS 'Success Rate %%' " +
                                "FROM parcel_status ps " +
                                "JOIN couriers c ON ps.courier_id=c.courier_id " +
                                "WHERE YEAR(ps.timestamp)=%d AND MONTH(ps.timestamp)=%d " +
                                "GROUP BY c.courier_id ORDER BY `Success Rate %%` DESC",
                        year, month);

            case "Yearly":
                return String.format(
                        "SELECT c.courier_id AS 'Courier ID', " +
                                "CONCAT(c.first_name, ' ', c.last_name) AS 'Courier Name', " +
                                "c.vehicle_type AS 'Vehicle Type', " +
                                "COUNT(ps.parcel_id) AS 'Total Deliveries', " +
                                "SUM(CASE WHEN ps.status_update = 'Delivered' THEN 1 ELSE 0 END) AS 'Successful', " +
                                "SUM(CASE WHEN ps.status_update IN ('Cancelled','Returned','Failed') THEN 1 ELSE 0 END) AS 'Unsuccessful', " +
                                "ROUND((SUM(CASE WHEN ps.status_update='Delivered' THEN 1 ELSE 0 END)/COUNT(ps.parcel_id)*100),2) AS 'Success Rate %%' " +
                                "FROM parcel_status ps " +
                                "JOIN couriers c ON ps.courier_id=c.courier_id " +
                                "WHERE YEAR(ps.timestamp)=%d " +
                                "GROUP BY c.courier_id ORDER BY `Success Rate %%` DESC",
                        year);

            default:
                return "SELECT * FROM couriers LIMIT 0";
        }
    }

    private String generateSummary(Connection conn, String period, int year) throws SQLException {
        int month = cbMonth.getSelectedIndex() + 1;
        String date = txtDate.getText().trim();

        ResultSet rs;
        switch (period) {
            case "Daily":
                rs = CourierReportDatabase.getDailyCourierPerformance(conn, date);
                break;
            case "Monthly":
                rs = CourierReportDatabase.getMonthlyCourierPerformance(conn, year, month);
                break;
            case "Yearly":
                rs = CourierReportDatabase.getYearlyCourierPerformance(conn, year);
                break;
            default:
                return "Invalid period";
        }

        int totalCouriers = 0;
        int totalDeliveries = 0;
        int totalSuccessful = 0;
        int totalUnsuccessful = 0;
        double avgSuccessRate = 0;
        double highestSuccessRate = 0;
        String bestCourier = "N/A";

        while (rs.next()) {
            totalCouriers++;
            totalDeliveries += rs.getInt("total_deliveries");
            totalSuccessful += rs.getInt("successful_deliveries");
            totalUnsuccessful += rs.getInt("unsuccessful_deliveries");

            double successRate = rs.getDouble("success_rate");
            avgSuccessRate += successRate;

            if (successRate > highestSuccessRate) {
                highestSuccessRate = successRate;
                bestCourier = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        }

        if (totalCouriers > 0) {
            avgSuccessRate /= totalCouriers;
        }

        return String.format("""
            PERFORMANCE SUMMARY (%s Report)
            =================================
            Total Couriers: %d
            Total Deliveries: %d
            Successful Deliveries: %d
            Unsuccessful Deliveries: %d
            Average Success Rate: %.2f%%
            Best Performing Courier: %s (%.2f%%)
            =================================
            Report Period: %s
            """,
                period, totalCouriers, totalDeliveries, totalSuccessful,
                totalUnsuccessful, avgSuccessRate, bestCourier, highestSuccessRate,
                getPeriodDescription(period, year, month, date));
    }

    private String getPeriodDescription(String period, int year, int month, String date) {
        switch (period) {
            case "Daily": return date;
            case "Monthly": return String.format("%s %d", cbMonth.getSelectedItem(), year);
            case "Yearly": return String.format("Year %d", year);
            default: return "Unknown";
        }
    }

    private void exportToCSV() {
        JOptionPane.showMessageDialog(this, "Export to CSV functionality would be implemented here.");
    }

    private void showDatePicker() {
        String result = JOptionPane.showInputDialog(this, "Enter date (YYYY-MM-DD):", txtDate.getText());
        if (result != null && !result.trim().isEmpty()) {
            txtDate.setText(result.trim());
        }
    }
}
