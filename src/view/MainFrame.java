package view;

import javax.swing.*;
import java.awt.*;
import controller.NavigationController;
import controller.RecordManagementController;

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

        // menu panels, add to main panel
        mainPanel.add(new TransactionsPanel(), "TransactionsMenu");
        
        RecordManagementPanel recordPanel = new RecordManagementPanel();
        mainPanel.add(recordPanel, "RecordMenu");

        // call controller
        new RecordManagementController(recordPanel);

        mainPanel.add(new ReportsPanel(), "ReportsMenu");          

        // add subpanels here too (will continue later)

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
