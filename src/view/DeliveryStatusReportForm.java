package view;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DBConnection;
import database.DeliveryStatusDatabase;
import database.LoadTable;

public class DeliveryStatusReportForm extends JDialog {

    private JComboBox<String> cbPeriod;
    private JComboBox<Integer> cbYear;
    private JComboBox<String> cbMonth;
    private JTextField txtDate;
    private JButton btnGenerate;
    private JButton btnExport;
    private JButton btnClose;
    private JTable reportTable;
    private JTextArea summaryArea;

    public DeliveryStatusReportForm(JFrame parentFrame) {
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

        btnExport.addActionListener(e -> exportToCSV(reportTable));

        btnClose.addActionListener(e -> dispose());

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
                        """
                            SELECT
                                DATE(timestamp) AS delivery_date,
                                COUNT(*) AS total_deliveries,
                                SUM(CASE WHEN status_update = 'Delivered' THEN 1 ELSE 0 END) AS total_successful_deliveries,
                                SUM(CASE WHEN status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) AS total_unsuccessful_deliveries,
                                ROUND((SUM(CASE WHEN status_update = 'Delivered' THEN 1.0 ELSE 0 END) / COUNT(parcel_id) * 100), 2) as success_rate
                            FROM parcel_status
                            WHERE DATE(timestamp) = %s
                            GROUP BY DATE(timestamp)
                        """,
                        date);

            case "Monthly":
                return String.format(
                        """
                           SELECT
                                DATE_FORMAT(timestamp, '%%Y-%%m') AS delivery_month,
                                COUNT(*) AS total_deliveries,
                                SUM(CASE WHEN status_update = 'Delivered' THEN 1 ELSE 0 END) AS total_successful_deliveries,
                                SUM(CASE WHEN status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) AS total_unsuccessful_deliveries,
                                ROUND((SUM(CASE WHEN status_update = 'Delivered' THEN 1.0 ELSE 0 END) / COUNT(parcel_id) * 100), 2) as success_rate
                            FROM parcel_status
                            WHERE YEAR(timestamp) = %d AND MONTH(timestamp) = %d
                            GROUP BY DATE_FORMAT(timestamp, '%%Y-%%m');
                        """,
                        year, month);

            case "Yearly":
                return String.format(
                        """
                            SELECT
                                 YEAR(timestamp) AS delivery_year,
                                    COUNT(*) AS total_deliveries,
                                    SUM(CASE WHEN status_update = 'Delivered' THEN 1 ELSE 0 END) AS total_successful_deliveries,
                                    SUM(CASE WHEN status_update IN ('Cancelled', 'Returned', 'Failed') THEN 1 ELSE 0 END) AS total_unsuccessful_deliveries,
                                    ROUND((SUM(CASE WHEN status_update = 'Delivered' THEN 1.0 ELSE 0 END) / COUNT(parcel_id) * 100), 2) AS success_rate
                                FROM parcel_status
                                WHERE YEAR(timestamp) = %d
                                GROUP BY YEAR(timestamp)
                        """,
                        year);

            default:
                return "SELECT * FROM parcel_status LIMIT 0";
        }
    }

    private String generateSummary(Connection conn, String period, int year) throws SQLException {
        int month = cbMonth.getSelectedIndex() + 1;
        String date = txtDate.getText().trim();

        ResultSet rs;

        switch (period) {
            case "Daily":
                rs = DeliveryStatusDatabase.getDailyDeliveryReport(conn, date);
                break;
            case "Monthly":
                rs = DeliveryStatusDatabase.getMonthlyDeliveryReport(conn, year, month);
                break;
            case "Yearly":
                rs = DeliveryStatusDatabase.getYearlyDeliveryReport(conn, year);
                break;
            default:
                return "Invalid period";
        }

        int totalDeliveries = 0;
        int totalSuccessful = 0;
        int totalUnsuccessful = 0;
        double avgSuccessRate = 0;

        while (rs.next()) {
            totalDeliveries += rs.getInt("total_deliveries");
            totalSuccessful += rs.getInt("total_successful_deliveries");
            totalUnsuccessful += rs.getInt("total_unsuccessful_deliveries");
            avgSuccessRate += rs.getDouble("success_rate");
        }

        return String.format("""
            DELIVERY SUMMARY (%s Report)
            =================================
            Total Deliveries: %d
            Successful Deliveries: %d
            Unsuccessful Deliveries: %d
            Average Delivery Success Rate: %.2f%%
            =================================
            Report Period: %s
            """, period, totalDeliveries, totalSuccessful, totalUnsuccessful, avgSuccessRate,
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

    private void exportToCSV(JTable table) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Delivery_Report.csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {

            // headers
            for (int col = 0; col < table.getColumnCount(); col++) {
                fw.write(table.getColumnName(col) + ",");
            }
            fw.write("\n");

            // rows
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    fw.write(String.valueOf(table.getValueAt(row, col)) + ",");
                }
                fw.write("\n");
            }

            JOptionPane.showMessageDialog(this, "CSV exported successfully!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed:\n" + ex.getMessage());
        }
    }

    private void showDatePicker() {
        String result = JOptionPane.showInputDialog(this, "Enter date (YYYY-MM-DD):", txtDate.getText());
        if (result != null && !result.trim().isEmpty()) {
            txtDate.setText(result.trim());
        }
    }
}
