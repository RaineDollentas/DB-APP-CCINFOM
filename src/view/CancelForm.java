package view;

import javax.swing.*;
import java.awt.*;

public class CancelForm extends JDialog {

    private JTextField txtTrackingId;
    public JButton btnConfirm;
    public JButton btnCancel;

    public CancelForm(JFrame parent, String title) {
        super(parent, title, true);

        setSize(400, 180);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel lbl = new JLabel("Enter Tracking ID to cancel:");
        txtTrackingId = new JTextField();

        centerPanel.add(lbl);
        centerPanel.add(txtTrackingId);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirm = new JButton("Confirm");
        btnCancel  = new JButton("Cancel");

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getTrackingIdText() {
        return txtTrackingId.getText().trim();
    }
}
