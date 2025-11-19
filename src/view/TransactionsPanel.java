package view;

import javax.swing.*;
import java.awt.*;

/**
 * TRANSACTION PAGE
 */
public class TransactionsPanel extends JPanel {

    public JButton btnBookParcel;
    public JButton btnCompleteDelivery;
    public JButton btnCancelBooking;
    public JButton btnParcelReturn;

    public TransactionsPanel() {
        setLayout(new GridBagLayout()); 
        setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); 

        // buttons
        btnBookParcel = createTileButton("Book Parcel");
        btnCompleteDelivery = createTileButton("Complete Delivery");
        btnCancelBooking = createTileButton("Cancel Booking");
        btnParcelReturn = createTileButton("Parcel Return");

        // layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(btnBookParcel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(btnCompleteDelivery, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(btnCancelBooking, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(btnParcelReturn, gbc);
    }

  /**
     * Helper for button design
     * @param text the text label for the button
     * @return button
     */
    private JButton createTileButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(250, 120));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(new Color(255, 140, 0));  
        btn.setForeground(Color.WHITE);

        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return btn;
    }
}
