package view;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;

import database.DBConnection;
import database.DeliveryTrendsDatabase;
import database.LoadTable;

public class DeliveryTrendsReportForm extends JDialog {

    private JComboBox<Integer> cbYear;
    private JComboBox<String> cbMonth;

    private JButton btnGenerate;
    private JButton btnExportDaily;
    private JButton btnExportGeo;
    private JButton btnClose;

    private JTable trendsTable;
    private JTable geoTable;

    private JTextArea summaryArea;

    public DeliveryTrendsReportForm(JFrame parent) {
        super(parent, "Delivery Trends Report", true);
        initializeComponents();
        setupLayout();
        setupEvents();
        loadAvailableYears();

        setSize(900, 600);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {

        cbYear = new JComboBox<>();
        cbMonth = new JComboBox<>(new String[]{
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        });

        cbMonth.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);

        btnGenerate = new JButton("Generate");
        btnExportDaily = new JButton("Export Daily CSV");
        btnExportGeo = new JButton("Export Location CSV");
        btnClose = new JButton("Close");

        trendsTable = new JTable();
        geoTable = new JTable();

        summaryArea = new JTextArea(5, 30);
        summaryArea.setEditable(false);
        summaryArea.setBackground(new Color(240, 240, 240));
        summaryArea.setBorder(BorderFactory.createTitledBorder("Summary"));
    }

    private void setupLayout() {

        JPanel top = new JPanel(new GridLayout(1, 4, 10, 10));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        top.add(new JLabel("Select Year:"));
        top.add(cbYear);
        top.add(new JLabel("Select Month:"));
        top.add(cbMonth);

        JPanel buttons = new JPanel();
        buttons.add(btnGenerate);
        buttons.add(btnExportDaily);
        buttons.add(btnExportGeo);
        buttons.add(btnClose);

        JSplitPane tables = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(trendsTable),
                new JScrollPane(geoTable));
        tables.setDividerLocation(250);

        setLayout(new BorderLayout(10, 10));
        add(top, BorderLayout.NORTH);
        add(tables, BorderLayout.CENTER);
        add(summaryArea, BorderLayout.SOUTH);
        add(buttons, BorderLayout.PAGE_END);
    }

    private void setupEvents() {

        btnGenerate.addActionListener(e -> generateReport());
        btnClose.addActionListener(e -> dispose());

        btnExportDaily.addActionListener(e -> exportTableToCSV(trendsTable, "daily_trends.csv"));
        btnExportGeo.addActionListener(e -> exportTableToCSV(geoTable, "geo_distribution.csv"));
    }

    private void loadAvailableYears() {
        try (Connection conn = DBConnection.getConnection()) {

            for (int year : DeliveryTrendsDatabase.getAvailableYears(conn)) {
                cbYear.addItem(year);
            }

            cbYear.setSelectedItem(java.time.LocalDate.now().getYear());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading years.");
        }
    }

    private void generateReport() {

        int year = (Integer) cbYear.getSelectedItem();
        int month = cbMonth.getSelectedIndex() + 1;

        try (Connection conn = DBConnection.getConnection()) {

            // daily trends table
            LoadTable.loadTable(trendsTable,
                    DeliveryTrendsDatabase.buildDailyQuery(year, month));

            // geographic distribution
            LoadTable.loadTable(geoTable,
                    DeliveryTrendsDatabase.buildGeoQuery(year, month));

            summaryArea.setText(
                    DeliveryTrendsDatabase.generateSummary(conn, year, month)
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report:\n" + ex.getMessage());
        }
    }

    private void exportTableToCSV(JTable table, String defaultName) {

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultName));

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
}
