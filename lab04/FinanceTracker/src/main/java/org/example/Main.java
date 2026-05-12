package org.example;

import org.example.db.DatabaseManager;
import org.example.ui.FinanceTrackerGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Инициализируем базу данных (создаст таблицы, если их нет)
        DatabaseManager.getInstance();

        // 2. Запускаем графический интерфейс в специальном потоке для UI
        SwingUtilities.invokeLater(() -> {
            FinanceTrackerGUI gui = new FinanceTrackerGUI();
            gui.setVisible(true);
        });
    }
}