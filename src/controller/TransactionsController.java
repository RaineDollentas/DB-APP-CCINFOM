package controller;

import view.TransactionsPanel;
import java.awt.*;
import javax.swing.*;

// controllers for the 4 buttons in transaction panel menu, its just for those 4 and nothing else
public class TransactionsController {

    public TransactionsController(TransactionsPanel view, JPanel mainPanel,CardLayout cardLayout) {

        // book parcel button, once clicked will open bookparcelform (handdled by parcelbookcontroller)
        view.btnBookParcel.addActionListener(e -> {
        });

        // edit these nalang 
        view.btnCompleteDelivery.addActionListener(e ->{}
        );

        view.btnCancelBooking.addActionListener(e ->{}
        );

        view.btnParcelReturn.addActionListener(e ->{}
        );
    }
}
