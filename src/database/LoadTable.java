package database;

import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class LoadTable {

    public static void loadTable(JTable table, String query) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // get metadata
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // create table model
            DefaultTableModel model = new DefaultTableModel();

            // add column names
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            // add rows
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    // Format percentage values for better display
                    if (meta.getColumnName(i).toLowerCase().contains("rate") && value instanceof Number) {
                        double rate = ((Number) value).doubleValue();
                        rowData[i - 1] = String.format("%.2f%%", rate);
                    } else {
                        rowData[i - 1] = value;
                    }
                }
                model.addRow(rowData);
            }

            table.setModel(model);

        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Overloaded method for prepared statements with parameters
    public static void loadTable(JTable table, String query, Object... params) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();

            // add column names
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            // add rows
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    // Format percentage values for better display
                    if (meta.getColumnName(i).toLowerCase().contains("rate") && value instanceof Number) {
                        double rate = ((Number) value).doubleValue();
                        rowData[i - 1] = String.format("%.2f%%", rate);
                    } else {
                        rowData[i - 1] = value;
                    }
                }
                model.addRow(rowData);
            }

            table.setModel(model);

        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}