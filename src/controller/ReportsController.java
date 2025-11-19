package controller;

import view.ReportsPanel;
import view.CustomerTrendsReportDialog; //corpuz
import view.CourierPerformanceReportForm;
import view.DeliveryTrendsReportForm;
import view.DeliveryStatusReportForm;

import javax.swing.*;
import java.awt.*;

// connected to reports panel
public class ReportsController {

    private ReportsPanel view;
    private CustomerTrendsReportDialog currentDialog = null; // corpuz

    public ReportsController(ReportsPanel view, JPanel mainPanel, CardLayout cardLayout) {
        this.view = view;
        initController();
    }

    private void initController() {
        // Remove any existing listeners first (corpuz)
        for (var listener : view.btnCustomerTrends.getActionListeners()) {
            view.btnCustomerTrends.removeActionListener(listener);
        }

        // Delivery Status Report 
        view.btnDeliveryStatus.addActionListener(e -> showDeliveryStatusReport());

        // Courier Performance Report
        view.btnCourierPerformance.addActionListener(e -> showCourierPerformanceReport());

        // Customer Trends Report (corpuz)
        view.btnCustomerTrends.addActionListener(e -> openCustomerTrendsReport());

        // Delivery Trends Report 
        view.btnDeliveryTrends.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
            new DeliveryTrendsReportForm(parent).setVisible(true);
        });
    }

    private void openCustomerTrendsReport() {
        // If dialog already exists and is visible, just bring it to front
        if (currentDialog != null && currentDialog.isVisible()) {
            currentDialog.toFront();
            return;
        }

        // Create new dialog
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        currentDialog = new CustomerTrendsReportDialog(parentFrame);

        // Add window listener to handle closing properly
        currentDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                currentDialog = null;
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                currentDialog = null;
            }
        });

        currentDialog.setVisible(true);
    }
    
    /**
     * Shows a basic info dialog for Delivery Status reports.
     */
    private void showDeliveryStatusReport() {
        DeliveryStatusReportForm form = new DeliveryStatusReportForm(null);
        form.setLocationRelativeTo(null); // Center on screen
        form.setVisible(true);
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
}
