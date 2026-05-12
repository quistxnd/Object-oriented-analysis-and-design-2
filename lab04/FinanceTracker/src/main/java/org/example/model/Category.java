package org.example.model;

public class Category {
    private int id;
    private String name;

    // Конструктор для создания новой категории (без id)
    public Category(String name) {
        this.name = name;
    }

    // Конструктор для загрузки категории из БД (с id)
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Переопределяем toString, чтобы в выпадающем списке (ComboBox) в GUI красиво отображалось название
    @Override
    public String toString() {
        return name;
    }
}