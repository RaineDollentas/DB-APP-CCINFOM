package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * panel for managing application records
 */
public class RecordManagementPanel extends JPanel {

    public JButton btnCustomers;
    public JButton btnParcels;
    public JButton btnCouriers;
    public JButton btnTracking;

    public JButton btnAdd;
    public JButton btnEdit;
    public JButton btnDelete;

    // tracking buttons
    public JTextField txtSearch;
    public JButton btnFilter;
    public JButton btnSort;
    public JButton btnRefresh;

    public JLabel titleLabel;
    public JTable table;

    /**
     * Constructs the record management panel and initializes the UI.
     */
    public RecordManagementPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerContainer = new JPanel(); // header
        headerContainer.setLayout(new BorderLayout());
        headerContainer.setBackground(Color.WHITE);

        // dataset buttons 
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        headerPanel.setBackground(Color.WHITE);

        btnCustomers = createHeaderButton("Customers");
        btnParcels = createHeaderButton("Parcels");
        btnCouriers = createHeaderButton("Couriers");
        btnTracking = createHeaderButton("Tracking");

        headerPanel.add(btnCustomers);
        headerPanel.add(btnParcels);
        headerPanel.add(btnCouriers);
        headerPanel.add(btnTracking);

        headerContainer.add(headerPanel, BorderLayout.NORTH);

        // add delete edit + (filter, sort, search for tracking)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        titleLabel = new JLabel("Record Management", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setBackground(Color.WHITE);

        // CRUD buttons (always visible except in tracking)
        btnAdd = createHeaderButton("Add");
        btnEdit = createHeaderButton("Edit");
        btnDelete = createHeaderButton("Delete");

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);

        txtSearch = new JTextField(12);
        btnFilter = createHeaderButton("Filter");
        btnSort = createHeaderButton("Sort");
        btnRefresh = createHeaderButton("Refresh");

        txtSearch.setVisible(false);
        btnFilter.setVisible(false);
        btnSort.setVisible(false);
        btnRefresh.setVisible(false);

        actionPanel.add(txtSearch);
        actionPanel.add(btnFilter);
        actionPanel.add(btnSort);
        actionPanel.add(btnRefresh);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        headerContainer.add(topPanel, BorderLayout.SOUTH);

        add(headerContainer, BorderLayout.NORTH);

        // table (fills whole center)
        table = new JTable(new DefaultTableModel());
        table.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);
    }

    /**
     * Helper for button design
     */
    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(new Color(240, 240, 240));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
