package org.example.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private double amount;
    private String type;
    private LocalDate date;
    private Category category;
    private User user;
    private String description; // 1. Новое поле

    // 2. Конструктор для создания новой транзакции (теперь 6 параметров)
    public Transaction(double amount, String type, LocalDate date, Category category, User user, String description) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.user = user;
        this.description = description;
    }

    // 3. Конструктор для загрузки из БД (теперь 7 параметров)
    public Transaction(int id, double amount, String type, LocalDate date, Category category, User user, String description) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.user = user;
        this.description = description;
    }

    // Геттеры
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public LocalDate getDate() { return date; }
    public Category getCategory() { return category; }
    public User getUser() { return user; }
    public String getDescription() { return description; } // Геттер для описания
}