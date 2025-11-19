package view;

import javax.swing.*;
import java.awt.*;

/**
 * REPORT PAGE
 */
public class ReportsPanel extends JPanel {

    public JButton btnDeliveryStatus;
    public JButton btnCourierPerformance;
    public JButton btnCustomerTrends;
    public JButton btnDeliveryTrends;

    /**
     * constructs the reports panel and initializes the ui
     */
    public ReportsPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // buttons
        btnDeliveryStatus = createTileButton("Delivery Status Report");
        btnCourierPerformance = createTileButton("Courier Performance Report");
        btnCustomerTrends = createTileButton("Customer Trends Report");
        btnDeliveryTrends = createTileButton("Delivery Trends Report");

        // layout (2×2 grid) for buttons
        gbc.gridx = 0; 
        gbc.gridy = 0;
        add(btnDeliveryStatus, gbc);
        gbc.gridx = 1; 
        gbc.gridy = 0;
        add(btnCourierPerformance, gbc);
        gbc.gridx = 0; 
        gbc.gridy = 1;
        add(btnCustomerTrends, gbc);
        gbc.gridx = 1; 
        gbc.gridy = 1;
        add(btnDeliveryTrends, gbc);

        // Connect controller
        new controller.ReportsController(this);
    }

    /**
     * Helper for button design
     * @param text the text label for the button
     * @return button
     */
    private JButton createTileButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(280, 120));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 140, 0));  
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }
}
