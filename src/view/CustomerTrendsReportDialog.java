package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DBConnection;
import database.ReportsDatabase;

public class CustomerTrendsReportDialog extends JDialog {

    private JTable reportTable;
    private JComboBox<String> cbPeriodType;
    private JComboBox<String> cbMonth;
    private JComboBox<String> cbYear;
    private JButton btnGenerate;
    private JButton btnClose;

    public CustomerTrendsReportDialog(JFrame parentFrame) {
        super(parentFrame, "Customer Trends Report", true);
        setSize(800, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Important!

        // Report Criteria Panel
        JPanel criteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        criteriaPanel.setBorder(BorderFactory.createTitledBorder("Report Criteria"));
        criteriaPanel.setBackground(Color.WHITE);

        criteriaPanel.add(new JLabel("Period:"));
        cbPeriodType = new JComboBox<>(new String[]{"Monthly", "Yearly"});
        criteriaPanel.add(cbPeriodType);

        criteriaPanel.add(new JLabel("Month:"));
        cbMonth = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        cbMonth.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
        criteriaPanel.add(cbMonth);

        criteriaPanel.add(new JLabel("Year:"));
        cbYear = new JComboBox<>();
        int currentYear = java.time.LocalDate.now().getYear();
        for (int year = currentYear; year >= currentYear - 5; year--) {
            cbYear.addItem(String.valueOf(year));
        }
        cbYear.setSelectedItem(String.valueOf(currentYear));
        criteriaPanel.add(cbYear);

        btnGenerate = new JButton("Generate Report");
        criteriaPanel.add(btnGenerate);

        add(criteriaPanel, BorderLayout.NORTH);

        // Report Table
        String[] columnNames = {"Rank", "Customer ID", "Customer Name", "Total Deliveries", "Address", "Contact No"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(model);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Top Customers by Delivery Count"));
        add(tableScroll, BorderLayout.CENTER);

        // Close Button
        btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose()); // This should close the dialog

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);

        // Generate report only on button click
        btnGenerate.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        String periodType = cbPeriodType.getSelectedItem().toString();
        int month = cbMonth.getSelectedIndex() + 1;
        int year = Integer.parseInt(cbYear.getSelectedItem().toString());

        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = ReportsDatabase.getCustomerTrendsReport(conn, periodType, month, year);

            DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
            model.setRowCount(0);

            int rank = 1;
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                Object[] rowData = {
                        rank++,
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("total_deliveries"),
                        rs.getString("address"),
                        rs.getString("contact_no")
                };
                model.addRow(rowData);
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No data found for the selected criteria.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }
}