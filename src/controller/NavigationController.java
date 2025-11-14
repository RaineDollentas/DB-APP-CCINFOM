package controller;

import javax.swing.*;

import view.SidebarPanel;

import java.awt.*;

/*
 * controller for sidepanel 
 */
public class NavigationController {

    public NavigationController(SidebarPanel sidebar, JPanel mainPanel, CardLayout cardLayout) {

        // for TRANSACTIONS menu
        sidebar.btnTransactions.addActionListener(e ->
                cardLayout.show(mainPanel, "TransactionsMenu")
        );

        // for RECORD MANAGEMENT menu
        sidebar.btnRecords.addActionListener(e ->
                cardLayout.show(mainPanel, "RecordMenu")
        );

        // for REPORTS menu
        sidebar.btnReports.addActionListener(e ->
                cardLayout.show(mainPanel, "ReportsMenu")
        );

    }
}
