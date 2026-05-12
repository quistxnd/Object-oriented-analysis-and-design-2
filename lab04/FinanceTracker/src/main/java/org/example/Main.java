package org.example;

import org.example.db.DatabaseManager;
import org.example.ui.FinanceTrackerGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
       
        DatabaseManager.getInstance();

        
        SwingUtilities.invokeLater(() -> {
            FinanceTrackerGUI gui = new FinanceTrackerGUI();
            gui.setVisible(true);
        });
    }
}
