package view;

import javax.swing.*;
import java.awt.*;
import controller.NavigationController;
import controller.ParcelBookController;
import controller.RecordManagementController;
import controller.TransactionsController;

/**
 * Main application window
 * Contains the application sidebar and a cardbased main panel that
 * switches between transactions, record management, and reports 
 */
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebar;

    /**
     * Constructs the main frame and initializes the UI
     */
    public MainFrame() {
        setTitle("Lalamove-Lite"); // title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700); 
        setLocationRelativeTo(null);
        setResizable(false); // fixed size

        setLayout(new BorderLayout());

        // sidebar
        sidebar = new SidebarPanel();  // Transactions, Records, Reports buttons

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

        // load Reports Panel, pero no logic yet or anything
        mainPanel.add(new ReportsPanel(), "ReportsMenu");          

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
