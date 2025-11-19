package view;

import javax.swing.*;
import java.awt.*;

//for tracking record management filterr
public class FilterDialog extends JDialog {

    public JTextField txtCourierId;
    public JComboBox<String> cbStatus;
    public JSpinner dateFrom;
    public JSpinner dateTo;

    public JButton btnApply;
    public JButton btnCancel;

    public FilterDialog(JFrame parent) {
        super(parent, "Filter Tracking Records", true);

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));
        setResizable(false);

        txtCourierId = new JTextField();

        String[] statusOptions = {"All", "Booked", "In Transit", "Delivered", "Return to Sender", "Returned", "Cancelled"};
        cbStatus = new JComboBox<>(statusOptions);

        dateFrom = new JSpinner(new SpinnerDateModel());
        dateTo = new JSpinner(new SpinnerDateModel());
        dateFrom.setEditor(new JSpinner.DateEditor(dateFrom, "yyyy-MM-dd HH:mm:ss"));
        dateTo.setEditor(new JSpinner.DateEditor(dateTo, "yyyy-MM-dd HH:mm:ss"));

        btnApply = new JButton("Apply Filter");
        btnCancel = new JButton("Cancel");

        add(new JLabel("Courier ID:"));
        add(txtCourierId);

        add(new JLabel("Status:"));
        add(cbStatus);

        add(new JLabel("Date From:"));
        add(dateFrom);

        add(new JLabel("Date To:"));
        add(dateTo);

        add(btnApply);
        add(btnCancel);
    }
}
