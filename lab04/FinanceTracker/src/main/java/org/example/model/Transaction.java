package org.example.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private double amount;
    private String type;
    private LocalDate date;
    private Category category;
    private User user;
    private String description; 

    // конструктор для создания новой транзакции 
    public Transaction(double amount, String type, LocalDate date, Category category, User user, String description) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.user = user;
        this.description = description;
    }

    // конструктор для загрузки из БД 
    public Transaction(int id, double amount, String type, LocalDate date, Category category, User user, String description) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.user = user;
        this.description = description;
    }

    
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public LocalDate getDate() { return date; }
    public Category getCategory() { return category; }
    public User getUser() { return user; }
    public String getDescription() { return description; } 
}
