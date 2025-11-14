package view;

import javax.swing.*;
import java.awt.*;

/**
 * sidebar navigation panel
 */
public class SidebarPanel extends JPanel {
    /*
    * Buttons for the Transactions view
    */
    public JButton btnTransactions, btnRecords, btnReports;

    /**
     * Constructs the sidebar and initializes navigation buttons.
     */
    public SidebarPanel() {
        setLayout(new GridLayout(6, 1, 0, 10)); 
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(220, 700));

        // title
        JLabel title = new JLabel("Lalamove-Lite", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(title);

        // main nav buttons
        btnTransactions = createNavButton("Transactions");
        btnRecords = createNavButton("Record Management");
        btnReports = createNavButton("Reports");

        add(btnTransactions);
        add(btnRecords);
        add(btnReports);
    }

  /**
     * Helper for button design
     * @param text the text label for the button
     * @return button
     */
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 45, 45));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 140, 0)); // orange
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 45, 45));
            }
        });

        return btn;
    }
}
