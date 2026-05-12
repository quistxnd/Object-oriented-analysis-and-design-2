package org.example.model;
public class Goal {
    private int id;
    private String name;
    private double targetAmount;
    public Goal(int id, String name, double targetAmount) { this.id = id; this.name = name; this.targetAmount = targetAmount; }
    public Goal(String name, double targetAmount) { this.name = name; this.targetAmount = targetAmount; }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
}