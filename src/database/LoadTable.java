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
                    rowData[i - 1] = rs.getObject(i);
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
