package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:finance.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void initDatabase() {
        // 1. Описываем структуру таблиц
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);";
        String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL NOT NULL, " +
                "type TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "category_id INTEGER, " +
                "user_id INTEGER, " +
                "description TEXT, " +
                "FOREIGN KEY(category_id) REFERENCES categories(id), " +
                "FOREIGN KEY(user_id) REFERENCES users(id));";

        String createGoalsTable = "CREATE TABLE IF NOT EXISTS goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "target_amount REAL NOT NULL);";

        // 2. Выполняем создание таблиц внутри блока try
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createCategoriesTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createGoalsTable);

            // 3. Добавляем начальные данные (пользователей и категории)
            stmt.execute("INSERT OR IGNORE INTO users (id, name) VALUES (1, 'Иван (Основной)')");

            stmt.execute("INSERT OR IGNORE INTO categories (id, name) VALUES (1, 'Зарплата')");
            stmt.execute("INSERT OR IGNORE INTO categories (id, name) VALUES (2, 'Продукты')");
            stmt.execute("INSERT OR IGNORE INTO categories (id, name) VALUES (3, 'Транспорт')");
            stmt.execute("INSERT OR IGNORE INTO categories (id, name) VALUES (4, 'Развлечения')");

            System.out.println("База данных успешно инициализирована.");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании БД: " + e.getMessage());
        }
    } // Конец метода initDatabase
} // Конец класса DatabaseManager