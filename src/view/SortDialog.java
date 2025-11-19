package view;

import javax.swing.*;
import java.awt.*;

//for tracking record management sort
public class SortDialog extends JDialog {

    public JComboBox<String> cbSortField;
    public JComboBox<String> cbOrder;
    public JButton btnApply;
    public JButton btnCancel;

    public SortDialog(JFrame parent) {
        super(parent, "Sort Tracking Records", true);

        setSize(350, 180);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 2, 10, 10));
        setResizable(false);

        String[] fields = {"timestamp", "parcel_id", "courier_id", "status_update"};
        cbSortField = new JComboBox<>(fields);

        String[] orderOptions = {"ASC", "DESC"};
        cbOrder = new JComboBox<>(orderOptions);

        btnApply = new JButton("Apply Sort");
        btnCancel = new JButton("Cancel");

        add(new JLabel("Sort By:"));
        add(cbSortField);

        add(new JLabel("Order:"));
        add(cbOrder);

        add(btnApply);
        add(btnCancel);
    }
}
