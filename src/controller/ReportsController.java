package controller;

import view.ReportsPanel;
import view.CourierPerformanceReportForm;
import view.DeliveryTrendsReportForm;

import javax.swing.*;
import java.awt.*;

// connected to reports panel
public class ReportsController {

    public ReportsController(ReportsPanel view, JPanel mainPanel, CardLayout cardLayout) {

        // Delivery Status Report
        view.btnDeliveryStatus.addActionListener(e -> showDeliveryStatusReport());

        // Courier Performance Report
        view.btnCourierPerformance.addActionListener(e -> showCourierPerformanceReport());

        // Customer Trends Report
        view.btnCustomerTrends.addActionListener(e -> showCustomerTrendsReport());

        // Delivery Trends Report
        view.btnDeliveryTrends.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
            new DeliveryTrendsReportForm(parent).setVisible(true);
        });
    }

    /**
     * Shows a basic info dialog for Delivery Status reports.
     */
    private void showDeliveryStatusReport() {
        JOptionPane.showMessageDialog(
                null,
                "Delivery Status Report\n\n" +
                        "This report would show:\n" +
                        "- Total deliveries\n" +
                        "- Successful deliveries\n" +
                        "- Unsuccessful deliveries\n" +
                        "For a selected period (Daily / Monthly / Yearly)",
                "Delivery Status Report",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Opens the Courier Performance Report form safely.
     * FIX: Removed the null getWindowAncestor() call that caused NPE.
     */
    private void showCourierPerformanceReport() {
        // No parent frame needed — prevents NullPointerException.
        CourierPerformanceReportForm form = new CourierPerformanceReportForm(null);
        form.setLocationRelativeTo(null); // Center on screen
        form.setVisible(true);
    }

    /**
     * Shows a basic info dialog for Customer Trends.
     */
    private void showCustomerTrendsReport() {
        JOptionPane.showMessageDialog(
                null,
                "Customer Trends Report\n\n" +
                        "This report would show:\n" +
                        "- Top customers by delivery volume\n" +
                        "- Customer growth trends\n" +
                        "- Repeat customer analysis",
                "Customer Trends Report",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}