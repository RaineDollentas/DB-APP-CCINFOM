package view;

import javax.swing.*;
import java.awt.*;
import controller.NavigationController;
import controller.ParcelBookController;
import controller.RecordManagementController;
import controller.TransactionsController;
import controller.ReportsController; // ADD THIS IMPORT

/**
 * Main application window
 */
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebar;

    /**
     * Constructs the main frame and initializes the UI
     */
    public MainFrame() {
        setTitle("Lalamove-Lite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        // sidebar
        sidebar = new SidebarPanel();

        // main panel w card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(28, 28, 28));

        // load Transactions Panel
        TransactionsPanel transactionsPanel = new TransactionsPanel();
        mainPanel.add(transactionsPanel, "TransactionsMenu");

        // call controller for transactions panel
        new TransactionsController(transactionsPanel, mainPanel, cardLayout);
        // call controller for book parcel form
        new ParcelBookController(transactionsPanel, mainPanel, cardLayout, this);

        // load Record Management Panel
        RecordManagementPanel recordPanel = new RecordManagementPanel();
        mainPanel.add(recordPanel, "RecordMenu");

        // call controller for record management panel
        new RecordManagementController(recordPanel);

        // load Reports Panel
        ReportsPanel reportsPanel = new ReportsPanel();
        mainPanel.add(reportsPanel, "ReportsMenu");

        // NEWLY ADDED: Reports Controller pls check if works
        new ReportsController(reportsPanel, mainPanel, cardLayout);

        // show transactions menu by default
        cardLayout.show(mainPanel, "TransactionsMenu");

        // Add sidebar + main panel
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // connect to controller, handles sidebar buttons
        new NavigationController(sidebar, mainPanel, cardLayout);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}